package Controller;

import DAO.WeekExtractor;
import Helper.JDBC;
import Helper.appointmentQuery;
import Helper.customerQuery;
import com.example.c195_version1.Appointment;
import com.example.c195_version1.Customer;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.application.Platform;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

/**
 * main menu controller
 */
public class mainMenuController implements Initializable {
    /**
     * customer table view
     */
    public TableView<Customer> customersTV;

    public TableColumn<Customer, Integer> customerCUSIdCOL;
    public TableColumn<Customer, String> nameCOL;
    public TableColumn<Customer, String> phoneCOL;
    public TableColumn<Customer, String> addressCOL;
    public TableColumn<Customer, String> stateCOL;
    public TableColumn<Customer, String> postalCodeCOL;
    /**
     * Appointment table view
     */
    public TableView<Appointment> mainMenuTV;
    public TableColumn<Appointment, Integer> apptIdCOL;
    public TableColumn<Appointment, String> titleCOL;
    public TableColumn<Appointment, String> descriptionCOL;
    public TableColumn<Appointment, String> locationCOL;
    public TableColumn<Appointment, String> contactCOL;
    public TableColumn<Appointment, String> typeCOL;
    public TableColumn<Appointment, String> startTimeCOL;
    public TableColumn<Appointment, String> endTimeCOL;
    public TableColumn<Appointment, Integer> customerIdCOL;
    public TableColumn<Appointment, Integer> userIdCOL;
    /**
     * Timezone label
     */
    public Label TZchangeMeTXT;
    /**
     * Current month radio button
     */
    public RadioButton monthRBT;
    /**
     * View all radio button
     */
    public RadioButton viewAllRBT;
    /**
     * Current week radio button
     */
    public RadioButton weekRBT;
    ObservableList<Customer> listM;
    ObservableList<Appointment> listA;
    Connection conn = null;

    /**
     * open add appointment fxml
     * @param actionEvent
     * @throws IOException
     */
    public void onActionAddAppointment(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/c195_version1/addAppointment.fxml"));
        Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 730, 690);
        stage.setTitle("Add Appointment");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Opens modify appointment fxml and passes data to the modify appointment controller
     * @param actionEvent
     * @throws IOException
     */
    public void onActionModAppt(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/c195_version1/modifyAppointment.fxml"));
        Parent root = loader.load();
        modifyAppointmentController pass = loader.getController();
        pass.passAppt(mainMenuTV.getSelectionModel().getSelectedItem(),mainMenuTV.getSelectionModel().getSelectedIndex());
        Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setTitle("Modify Appointment");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Opens reports fxml
     * @param actionEvent
     * @throws IOException
     */

    public void onActionReports(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/c195_version1/reports.fxml"));
        Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 749, 478);
        stage.setTitle("Reports");
        stage.setScene(scene);
        stage.show();

    }

    /**
     * Cancels appointments and asks user for confirmation
     * @param actionEvent
     * @throws SQLException
     */
    public void onActionDeleteAppt(ActionEvent actionEvent) throws SQLException {
            Appointment selectedAppt = mainMenuTV.getSelectionModel().getSelectedItem();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Confirm Cancellation");
            alert.setContentText("Are you sure you want to cancel Appointment ID: " + selectedAppt.getApptId() +
                " type: " + selectedAppt.getType() + "?");

        ButtonType result = alert.showAndWait().orElse(ButtonType.CANCEL);
            if (result == ButtonType.OK) {
                try {
                    conn = JDBC.connection;
                    PreparedStatement pst = conn.prepareStatement("DELETE FROM appointments WHERE Appointment_ID = ?");
                    pst.setInt(1, selectedAppt.getApptId());
                    pst.executeUpdate();
                    listA.remove(selectedAppt);
                    mainMenuTV.setItems(listA);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }


    /**
     * Returns user to login screen
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
     * Deletes customer and asks user for confirmation
     * Also checks for appointments that are assigned to the customer
     * @param actionEvent
     * @throws SQLException
     */
    public void onActionDeleteCust(ActionEvent actionEvent) throws SQLException {
        Customer selectedCustomer = customersTV.getSelectionModel().getSelectedItem();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Confirm Deletion");
        alert.setContentText("Are you sure you want to delete this customer?");
        ButtonType result = alert.showAndWait().orElse(ButtonType.CANCEL);

        if (result == ButtonType.OK) {
            try {
                conn = JDBC.connection;
                if (hasAppointments(selectedCustomer.getCustomerId())) {
                    Alert deleteAppointmentsAlert = new Alert(Alert.AlertType.CONFIRMATION);
                    deleteAppointmentsAlert.setTitle("Confirm Deletion");
                    deleteAppointmentsAlert.setHeaderText("Delete Associated Appointments?");
                    deleteAppointmentsAlert.setContentText("Do you want to delete appointments associated with this customer?");
                    ButtonType deleteAppointmentsResult = deleteAppointmentsAlert.showAndWait().orElse(ButtonType.CANCEL);

                    if (deleteAppointmentsResult == ButtonType.OK) {
                        deleteAppointments(selectedCustomer.getCustomerId());
                    } else {
                        return;
                    }
                }

                PreparedStatement pst = conn.prepareStatement("DELETE FROM customers WHERE Customer_ID = ?");
                pst.setInt(1, selectedCustomer.getCustomerId());
                pst.executeUpdate();
                listM.remove(selectedCustomer);
                customersTV.setItems(listM);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean hasAppointments(int customerId) throws SQLException {
        String query = "SELECT * FROM appointments WHERE Customer_ID = ?";
        try (PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setInt(1, customerId);
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next();
            }
        }
    }
    private void deleteAppointments(int customerId) throws SQLException {
        PreparedStatement pstAppointments = conn.prepareStatement("DELETE FROM appointments WHERE Customer_ID = ?");
        pstAppointments.setInt(1, customerId);
        pstAppointments.executeUpdate();
        List<Appointment> appointmentsToRemove = listA.stream()
                .filter(appointment -> appointment.getCustomerId() == customerId)
                .collect(Collectors.toList());
        listA.removeAll(appointmentsToRemove);
        mainMenuTV.setItems(listA);
    }

    /**
     * Opens modify customer fxml and passes information to controller
     * @param actionEvent
     * @throws IOException
     */
    public void onActionModCust(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/c195_version1/modifyCustomer.fxml"));
        Parent root = loader.load();
        modifyCustomerController pass = loader.getController();
        pass.passCust(customersTV.getSelectionModel().getSelectedItem(),customersTV.getSelectionModel().getSelectedIndex());
        Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setTitle("Modify Customer");
        stage.setScene(scene);
        stage.show();

    }

    /**
     * Opens add customer fxml
     * @param actionEvent
     * @throws IOException
     */
    public void onActionAddCust(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/c195_version1/addCustomer.fxml"));
        Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 577, 402);
        stage.setTitle("Add Customer");
        stage.setScene(scene);
        stage.show();
    }

    private String getStateFromCustomer(int customerId) {
        String state = null;
        try {
            conn = JDBC.connection;
            String divisionNumberSql = "SELECT Division_ID FROM customers WHERE Customer_ID = ?";
            try (PreparedStatement divisionNumberStatement = conn.prepareStatement(divisionNumberSql)) {
                divisionNumberStatement.setInt(1, customerId);

                try (ResultSet divisionNumberResult = divisionNumberStatement.executeQuery()) {
                    if (divisionNumberResult.next()) {
                        int divisionNumber = divisionNumberResult.getInt("Division_ID");

                        String stateSql = "SELECT Division FROM first_level_divisions WHERE Division_ID = ?";
                        try (PreparedStatement stateStatement = conn.prepareStatement(stateSql)) {
                            stateStatement.setInt(1, divisionNumber);

                            try (ResultSet stateResult = stateStatement.executeQuery()) {
                                if (stateResult.next()) {
                                    state = stateResult.getString("Division");
                                }
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return state;

    }

    @Override
    /**
     * Initializes timezone to users computer settings
     * populates customer data to customer table view
     * populates appointments on appointment table view
     * Checks if there are any appointments within 15 minutes of user logging in
     */
    public void initialize(URL url, ResourceBundle resourceBundle) {

        TimeZone timeZone = TimeZone.getDefault();
        ZoneId zoneId = timeZone.toZoneId();
        ZoneId userTimeZone = ZoneId.systemDefault();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(userTimeZone);
        String timeZoneName = timeZone.getDisplayName();
        TZchangeMeTXT.setText(timeZoneName);

        customerCUSIdCOL.setCellValueFactory(new PropertyValueFactory<Customer,Integer>("customerId"));
        nameCOL.setCellValueFactory(new PropertyValueFactory<Customer,String>("customerName"));
        addressCOL.setCellValueFactory(new PropertyValueFactory<Customer,String>("address"));
        postalCodeCOL.setCellValueFactory(new PropertyValueFactory<Customer,String >("postalCode"));
        phoneCOL.setCellValueFactory(new PropertyValueFactory<Customer,String>("phoneNo"));
        stateCOL.setCellValueFactory(new PropertyValueFactory<Customer, String>("state"));

        listM = customerQuery.getDataCustomer();
        for (Customer customer : listM) {
            int customerId = customer.getCustomerId();
            String state = getStateFromCustomer(customerId);
            customer.setState(state);
        }

        customersTV.setItems(listM);

        apptIdCOL.setCellValueFactory(new PropertyValueFactory<>("apptId"));
        titleCOL.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionCOL.setCellValueFactory(new PropertyValueFactory<>("description"));
        locationCOL.setCellValueFactory(new PropertyValueFactory<>("location"));
        contactCOL.setCellValueFactory(cellData -> new SimpleStringProperty(appointmentQuery.getContactNameById(cellData.getValue().getContactId())));
        typeCOL.setCellValueFactory(new PropertyValueFactory<>("type"));


        startTimeCOL.setCellValueFactory(cellData -> {
            String startDateString = cellData.getValue().getStartDate();
            if (startDateString != null && !startDateString.isEmpty()) {
                LocalDateTime startTime = LocalDateTime.parse(startDateString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                ZonedDateTime startTimeInUserZone = ZonedDateTime.of(startTime, ZoneId.of("UTC")).withZoneSameInstant(userTimeZone);
                return new SimpleStringProperty(startTimeInUserZone.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            } else {
                return new SimpleStringProperty("");
            }
        });

        endTimeCOL.setCellValueFactory(cellData -> {
            String endDateString = cellData.getValue().getEndTime();
            if (endDateString != null && !endDateString.isEmpty()) {
                LocalDateTime endTime = LocalDateTime.parse(endDateString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                ZonedDateTime endTimeInUserZone = ZonedDateTime.of(endTime, ZoneId.of("UTC")).withZoneSameInstant(userTimeZone);
                return new SimpleStringProperty(endTimeInUserZone.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            } else {
                return new SimpleStringProperty("");
            }
        });

        customerIdCOL.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        userIdCOL.setCellValueFactory(new PropertyValueFactory<>("userId"));

        listA = appointmentQuery.getDataAppt();
        mainMenuTV.setItems(listA);

        checkAppointmentsWithin15Minutes();


    }



    private void checkAppointmentsWithin15Minutes() {
        try {
            List<Appointment> upcomingAppointments = listA.stream()
                    .filter(this::isWithin15Minutes)
                    .collect(Collectors.toList());

            if (!upcomingAppointments.isEmpty()) {
                Platform.runLater(() -> showUpcomingAppointmentsDialog(upcomingAppointments));
            }else {
                Platform.runLater(this::showNoUpcomingAppointmentsAlert);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private boolean isWithin15Minutes(Appointment appointment) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        LocalDateTime appointmentDateTime = LocalDateTime.parse(appointment.getStartDate(), formatter);
        ZonedDateTime appointmentZonedTime = appointmentDateTime.atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.systemDefault());

        LocalDateTime currentDateTime = LocalDateTime.now(ZoneId.systemDefault());
        ZonedDateTime currentZonedTime = currentDateTime.atZone(ZoneId.systemDefault());

        long minutesDifference = ChronoUnit.MINUTES.between(currentZonedTime, appointmentZonedTime);
        return minutesDifference > 0 && minutesDifference <= 15;
    }
    private void showUpcomingAppointmentsDialog(List<Appointment> upcomingAppointments) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Upcoming Appointments");
        alert.setHeaderText("You have upcoming appointments within 15 minutes");

        StringBuilder contentText = new StringBuilder("Please review your schedule:\n");

        for (Appointment appointment : upcomingAppointments) {
            DateTimeFormatter utcFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime appointmentDateTime = LocalDateTime.parse(appointment.getStartDate(), utcFormatter);

            ZoneId utcZone = ZoneId.of("UTC");
            ZoneId userZone = ZoneId.systemDefault();

            ZonedDateTime utcZonedDateTime = appointmentDateTime.atZone(utcZone);
            ZonedDateTime userZonedDateTime = utcZonedDateTime.withZoneSameInstant(userZone);

            DateTimeFormatter userFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            contentText.append("Appointment ID: ").append(appointment.getApptId()).append("\n");
            contentText.append("Start Date and Time: ").append(userFormatter.format(userZonedDateTime)).append("\n\n");
        }

        alert.setContentText(contentText.toString());
        alert.showAndWait();
    }
    private void showNoUpcomingAppointmentsAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("No Upcoming Appointments");
        alert.setHeaderText("You have no upcoming appointments within 15 minutes");
        alert.showAndWait();
    }


    /**
     * Lambda expression, filters appointment table view by current week
     */
    // Lambda expression
    private WeekExtractor weekExtractor = startDate -> {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDate localDate = LocalDate.parse(startDate, formatter);
        return localDate.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
    };

    /**
     *
     *
     * @param actionEvent
     */
    public void onActionCurrentWeek(ActionEvent actionEvent) {
        // Method to handle the action when selecting the current week
        LocalDate currentDate = LocalDate.now();
        int currentWeek = currentDate.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());

        List<Appointment> filteredAppointments = listA.stream()
                .filter(appointment -> weekExtractor.extractWeek(appointment.getStartDate()) == currentWeek)
                .collect(Collectors.toList());

        listA.setAll(filteredAppointments);
        mainMenuTV.setItems(listA);
    }

    /**
     * filters appointment table view by current month
     * @param actionEvent
     */
    public void onActionCurrentMonth(ActionEvent actionEvent) {
        LocalDate currentDate = LocalDate.now();
        int currentMonth = currentDate.getMonthValue();
        List<Appointment> filteredAppointments = listA.stream()
                .filter(appointment -> getMonthFromStartDate(appointment.getStartDate()) == currentMonth)
                .collect(Collectors.toList());
        listA.setAll(filteredAppointments);
        mainMenuTV.setItems(listA);
    }
    private int getMonthFromStartDate(String startDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDate localDate = LocalDate.parse(startDate, formatter);
        return localDate.getMonthValue();
    }

    /**
     * resets appoint table view and shows all appointments
     * @param actionEvent
     */
    public void onActionViewAll(ActionEvent actionEvent) {
        listA = appointmentQuery.getDataAppt();
        mainMenuTV.setItems(listA);
    }
}

