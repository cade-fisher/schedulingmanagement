package Controller;

import Helper.JDBC;
import Helper.appointmentQuery;
import com.example.c195_version1.Appointment;
import com.example.c195_version1.CustomerAppointment;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * reports controller class
 */
public class reportsController implements Initializable {
    /**
     * Table view for appointments by contact
     */
    public TableView<Appointment> contactTV;
    /**
     * appointment ID column
     */
    public TableColumn<Appointment,Integer> idCOL;
    /**
     * title column
     */
    public TableColumn<Appointment, String > titleCOL;
    /**
     * type column
     */
    public TableColumn<Appointment,String > typeCOL;
    /**
     * description column
     */
    public TableColumn<Appointment,String > descriptionCOL;
    /**
     * location column
     */
    public TableColumn<Appointment,String> locationCOL;
    public TableColumn<Appointment, String> startCOL;
    /**
     * start column
     */
    public TableColumn<Appointment, String > endCOL;
    /**
     * Customer ID column
     */
    public TableColumn<Appointment, Integer> customerIDCOL;
    /**
     * Appointment by type and month table
     */
    public TableView<Appointment> apptTV;
    /**
     * Month column
     */
    public TableColumn<Appointment, String > apptMonthCOL;
    /**
     * type column
     */
    public TableColumn<Appointment, String > apptTypeCOL;
    /**
     * appointment total column
     */
    public TableColumn<Appointment,Integer> apptTotalCOL;
    /**
     * Customer table
     */
    public TableView customerTV;
    /**
     * Customer name column
     */
    public TableColumn customerCOL;
    /**
     * Number of appointments associated with customer column
     */
    public TableColumn numberOfApptCOL;
    /**
     * back button
     */
    public Button backBTN;
    /**
     * log out button
     */
    public Button logoutBTN;
    /**
     * Contact name combo box
     */
    public ComboBox contactCB;
    ObservableList<Appointment> listA;
    Connection conn = null;
    private Map<String, Integer> totalCountsMap = new HashMap<>();

    /**
     * Returns user to main menu
     * @param actionEvent
     * @throws IOException
     */
    public void onActionBackToMain(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/c195_version1/mainMenu.fxml"));
        Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 970, 760);
        stage.setTitle("Main Menu");
        stage.setScene(scene);
        stage.show();

    }

    /**
     * returns user to log in fxml
     * @param actionEvent
     * @throws IOException
     */
    public void onActionLogout(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/c195_version1/login.fxml"));
        Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 600, 400);
        stage.setTitle("login");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Allows user to select contact to sort appointment by contact
     *
     * @param actionEvent
     */
    public void onActionSelectContact(ActionEvent actionEvent) {
        String selectedContactName = (String) contactCB.getValue();

        if ("View All".equals(selectedContactName)) {
            contactTV.setItems(listA);
        } else if (selectedContactName != null) {
            try {
                conn = JDBC.connection;
                PreparedStatement preparedStatement = conn.prepareStatement("SELECT Contact_ID FROM contacts WHERE Contact_Name = ?");
                preparedStatement.setString(1, selectedContactName);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    int selectedContactId = resultSet.getInt("Contact_ID");
                    ObservableList<Appointment> filteredAppointments = listA.filtered(appointment ->
                            appointment.getContactId() == selectedContactId);

                    contactTV.setItems(filteredAppointments);
                }

                resultSet.close();
                preparedStatement.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Populates contact name to combo box
     */
    public void populateContactCB(){
        try {
            conn = JDBC.connection;
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT Contact_Name FROM contacts");
            contactCB.getItems().clear();
            contactCB.getItems().add("View All");

            while (resultSet.next()) {
                contactCB.getItems().add(resultSet.getString("Contact_Name"));
            }

            resultSet.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the name of the month from the month number
     * @return
     * @throws SQLException
     * @throws ParseException
     */
    public Set<String> getDistinctMonthNames() throws SQLException, ParseException {
        Connection conn = JDBC.connection; // Assuming JDBC.connection represents your database connection
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT DISTINCT MONTHNAME(Start) AS MonthName FROM appointments");

        Set<String> distinctMonthNames = new HashSet<>();
        while (resultSet.next()) {
            distinctMonthNames.add(resultSet.getString("MonthName"));
        }

        resultSet.close();
        statement.close();

        return distinctMonthNames;
    }

    /**
     * Selects distinct type of appointment from database
     * @return
     * @throws SQLException
     */
    public Set<String> getDistinctTypes() throws SQLException {
        Connection conn = JDBC.connection;
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT DISTINCT Type FROM appointments");

        Set<String> distinctTypes = new HashSet<>();


        while (resultSet.next()) {
            distinctTypes.add(resultSet.getString("Type"));
        }


        resultSet.close();
        statement.close();

        return distinctTypes;
    }

    /**
     * populates the appointment table with month, type and total number.
     * Removes the month and type if total is 0
     * @throws SQLException
     * @throws ParseException
     */
    public void populateAppointmentTable() throws SQLException, ParseException {
        Set<String> distinctMonthNames = getDistinctMonthNames();
        Set<String> distinctTypes = getDistinctTypes();
        calculateTotalByTypeAndMonth();

        ObservableList<Appointment> data = FXCollections.observableArrayList();

        for (String month : distinctMonthNames) {
            for (String type : distinctTypes) {
                String key = type + "-" + month;
                int total = totalCountsMap.getOrDefault(key, 0);

                if (total > 0) {
                    data.add(new Appointment(month, type, total));
                }
            }
        }


        apptMonthCOL.setCellValueFactory(new PropertyValueFactory<>("month"));
        apptTypeCOL.setCellValueFactory(new PropertyValueFactory<>("type"));
        apptTotalCOL.setCellValueFactory(new PropertyValueFactory<>("total"));

        apptTV.setItems(data);
    }

    /**
     * Calculates the total number of appointments by month and type
     * @return
     * @throws SQLException
     * @throws ParseException
     */
    public Set<Integer> calculateTotalByTypeAndMonth() throws SQLException, ParseException {
        Set<String> distinctMonthNames = getDistinctMonthNames();
        Set<String> distinctTypes = getDistinctTypes();
        Connection conn = JDBC.connection;
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT Start, Type FROM appointments");
        totalCountsMap = new HashMap<>();

        while (resultSet.next()) {
            String startDateTimeStr = resultSet.getString("Start");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            java.util.Date startDate = sdf.parse(startDateTimeStr);
            SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM");
            String monthName = monthFormat.format(startDate);
            String type = resultSet.getString("Type");
            String key = type + "-" + monthName;
            totalCountsMap.put(key, totalCountsMap.getOrDefault(key, 0) + 1);
        }

        resultSet.close();
        statement.close();
        return new HashSet<>(totalCountsMap.values());
    }

    /**
     * populates the customer table
     */
    public void populateCustomerTable() {
        ObservableList<CustomerAppointment> customerData = FXCollections.observableArrayList();

        Set<String> distinctCustomers = getDistinctCustomers();

        for (String customerName : distinctCustomers) {
            int numberOfAppointments = calculateAppointmentsForCustomer(customerName);
            customerData.add(new CustomerAppointment(customerName, numberOfAppointments));
        }

        customerCOL.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        numberOfApptCOL.setCellValueFactory(new PropertyValueFactory<>("numberOfAppointments"));

        customerTV.setItems(customerData);
    }

    private Set<String> getDistinctCustomers() {
        Set<String> distinctCustomers = new HashSet<>();

        try {
            conn = JDBC.connection;
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT DISTINCT Customer_Name FROM customers");

            while (resultSet.next()) {
                distinctCustomers.add(resultSet.getString("Customer_Name"));
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return distinctCustomers;
    }

    private int calculateAppointmentsForCustomer(String customerName) {
        int numberOfAppointments = 0;

        try {
            conn = JDBC.connection;
            PreparedStatement preparedStatement = conn.prepareStatement(
                    "SELECT COUNT(*) AS AppointmentCount " +
                            "FROM appointments a " +
                            "JOIN customers c ON a.Customer_ID = c.Customer_ID " +
                            "WHERE c.Customer_Name = ?"
            );
            preparedStatement.setString(1, customerName);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                numberOfAppointments = resultSet.getInt("AppointmentCount");
            }

            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();

        }

        return numberOfAppointments;
    }


    @Override
    /**
     * Initializes controller
     * Populates the tables
     */
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            populateAppointmentTable();
            populateCustomerTable();
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
        }

        contactCB.setValue("View All");

        ZoneId userTimeZone = ZoneId.systemDefault();
        populateContactCB();

        idCOL.setCellValueFactory(new PropertyValueFactory<>("apptId"));
        titleCOL.setCellValueFactory(new PropertyValueFactory<>("title"));
        typeCOL.setCellValueFactory(new PropertyValueFactory<>("type"));
        descriptionCOL.setCellValueFactory(new PropertyValueFactory<>("description"));
        locationCOL.setCellValueFactory(new PropertyValueFactory<>("location"));
        startCOL.setCellValueFactory(cellData -> {
            String startDateString = cellData.getValue().getStartDate();
            if (startDateString != null && !startDateString.isEmpty()) {
                LocalDateTime startTime = LocalDateTime.parse(startDateString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                ZonedDateTime startTimeInUserZone = ZonedDateTime.of(startTime, ZoneId.of("UTC")).withZoneSameInstant(userTimeZone);
                return new SimpleStringProperty(startTimeInUserZone.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            } else {
                return new SimpleStringProperty("");
            }
        });

        endCOL.setCellValueFactory(cellData -> {
            String endDateString = cellData.getValue().getEndTime();
            if (endDateString != null && !endDateString.isEmpty()) {
                LocalDateTime endTime = LocalDateTime.parse(endDateString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                ZonedDateTime endTimeInUserZone = ZonedDateTime.of(endTime, ZoneId.of("UTC")).withZoneSameInstant(userTimeZone);
                return new SimpleStringProperty(endTimeInUserZone.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            } else {
                return new SimpleStringProperty("");
            }
        });

        customerIDCOL.setCellValueFactory(new PropertyValueFactory<>("customerId"));


        listA = appointmentQuery.getDataAppt();
        contactTV.setItems(listA);



    }



}
