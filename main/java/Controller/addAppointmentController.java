package Controller;

import Helper.JDBC;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ResourceBundle;
import java.time.LocalDateTime;

/**
 * Add appointment controller
 */
public class addAppointmentController implements Initializable {
    /**
     * appointment ID text field
     */
    public TextField appintmentIdTXT;
    /**
     * title text field
     */
    public TextField titleTXT;
    /**
     * description text field
     */
    public TextField descriptionTXT;
    /**
     * loaction text field
     */
    public TextField locationTXT;
    /**
     * type text field
     */
    public TextField typeTXT;
    /**
     * Contact name combo box
     */
    public ComboBox<String> contactIdCB;
    /**
     * Customer id combo box
     */
    public ComboBox<Integer>  customerIdCB;
    /**
     * user ID combo box
     */
    public ComboBox<Integer>  userIdCB;
    /**
     * start date, date picker
     */
    public DatePicker startDateDP;
    /**
     * end date, date picker
     */
    public DatePicker endDateDP;
    /**
     * Start minute spinner
     */
    public Spinner<Integer> startTimeMin;
    /**
     * start hour spinner
     */
    public Spinner<Integer> startTimeHour;
    /**
     * end minute spinner
     */
    public Spinner<Integer> endTimeMin;
    /**
     * end hour spinner
     */
    public Spinner<Integer> endTimeHour;
    /**
     * Save button
     */
    public Button saveBTN;
    /**
     * Cancel button
     */
    public Button cancelBTN;
    Connection conn = null;
    PreparedStatement pst = null;

    /**
     * Returns back to main menu
     * @param actionEvent
     * @throws IOException
     */
    public void onActionReturnMain(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/c195_version1/mainMenu.fxml"));
        Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 970, 760);
        stage.setTitle("Main Menu");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Gets the contact ID from the contact name
     * @param contactName
     * @return contact ID
     * @throws SQLException
     */
    private int getContactIdFromName(String contactName) throws SQLException {
        conn = JDBC.connection;
        String getIdSql = "SELECT Contact_ID FROM contacts WHERE Contact_Name = ?";
        try (PreparedStatement getIdStmt = conn.prepareStatement(getIdSql)) {
            getIdStmt.setString(1, contactName);

            try (ResultSet getIdRs = getIdStmt.executeQuery()) {
                if (getIdRs.next()) {
                    return getIdRs.getInt("Contact_ID");
                }
            }
        }
        throw new RuntimeException("Contact_ID not found for Contact_Name: " + contactName);
    }

    /**
     * Saves appointment and returns to main menu, gives user an error if appointment is overlapping or outside business hours
     * @param actionEvent
     * @throws IOException
     * @throws SQLException
     */
    public void onActionAddAppointment(ActionEvent actionEvent) throws IOException, SQLException {

        LocalDate startSelectedDate = startDateDP.getValue();
        int startHour = startTimeHour.getValue();
        int startMin = startTimeMin.getValue();
        LocalDateTime startTime = LocalDateTime.of(startSelectedDate, LocalTime.of(startHour, startMin));

        LocalDate endSelectedDate = endDateDP.getValue();
        int endHour = endTimeHour.getValue();
        int endMin = endTimeMin.getValue();
        LocalDateTime endTime = LocalDateTime.of(endSelectedDate, LocalTime.of(endHour, endMin));

        if (startTime.isAfter(endTime)) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("Invalid Date/Time Range");
            alert.setContentText("Start date/time cannot be after end date/time. Please choose a valid range.");
            alert.showAndWait();
            return;
        }

        LocalTime businessStart = LocalTime.of(8, 0);
        LocalTime businessEnd = LocalTime.of(22, 0);

        ZoneId easternTimeZone = ZoneId.of("America/New_York");
        LocalDateTime businessStartDateTime = LocalDateTime.of(startSelectedDate, businessStart).atZone(easternTimeZone).toLocalDateTime();
        LocalDateTime businessEndDateTime = LocalDateTime.of(startSelectedDate, businessEnd).atZone(easternTimeZone).toLocalDateTime();
        LocalDateTime startDateTime = LocalDateTime.of(startSelectedDate, LocalTime.of(startHour, startMin)).atZone(easternTimeZone).toLocalDateTime();
        LocalDateTime endDateTime = LocalDateTime.of(endSelectedDate, LocalTime.of(endHour, endMin)).atZone(easternTimeZone).toLocalDateTime();

        if (startDateTime.isBefore(businessStartDateTime) || endDateTime.isAfter(businessEndDateTime)) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("Invalid Appointment Time");
            alert.setContentText("Appointments can only be scheduled between 8:00 and 22:00 Eastern Time Zone. Please choose a valid time.");
            alert.showAndWait();
            return;
        }


        if (isAppointmentOverlapping(startDateTime, endDateTime)) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("Overlapping Appointments");
            alert.setContentText("The selected time range overlaps with an existing appointment. Please choose a different time.");
            alert.showAndWait();
            return;
        }

        conn = JDBC.connection;

        String getIdSql = "SELECT MAX(Appointment_ID) + 1 FROM appointments";

        Statement getIdStmt = conn.createStatement();
        ResultSet getIdRs = getIdStmt.executeQuery(getIdSql);
        int nextApptId = 1;
        if (getIdRs.next()) {
            nextApptId = getIdRs.getInt(1);
        }
        LocalDateTime startTimeUTC = startTime.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        LocalDateTime endTimeUTC = endTime.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        int selectedContactId = getContactIdFromName(contactIdCB.getValue());
        String sql = "INSERT INTO appointments VALUES(?, ?, ?, ?, ?, ?, ?, NOW(), 'script', NOW(), 'script', ?, ?, ?)";
        try {
            pst = conn.prepareStatement(sql);
            pst.setInt(1, nextApptId);
            pst.setString(2,titleTXT.getText());
            pst.setString(3,descriptionTXT.getText());
            pst.setString(4,locationTXT.getText());
            pst.setString(5,typeTXT.getText());
            pst.setString(6, String.valueOf(startTimeUTC));
            pst.setString(7, String.valueOf(endTimeUTC));
            pst.setInt(8, customerIdCB.getValue());
            pst.setInt(9, userIdCB.getValue());
            pst.setInt(10, selectedContactId);
            pst.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/c195_version1/mainMenu.fxml"));
        Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 970, 760);
        stage.setTitle("Main Menu");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Checks the database for over lapping times with appointments and gives user an error
     * @param newStart
     * @param newEnd
     * @return overlaps
     * @throws SQLException
     */
    private boolean isAppointmentOverlapping(LocalDateTime newStart, LocalDateTime newEnd) throws SQLException {
        conn = JDBC.connection;
        String overlapSql = "SELECT * FROM appointments " +
                "WHERE ( ? < End AND ? > Start ) " +
                "OR ( ? <= End AND ? >= Start )";
        pst = conn.prepareStatement(overlapSql);

        Timestamp startTimestamp = Timestamp.valueOf(newStart);
        Timestamp endTimestamp = Timestamp.valueOf(newEnd);

        pst.setTimestamp(1, startTimestamp);
        pst.setTimestamp(2, endTimestamp);
        pst.setTimestamp(3, startTimestamp);
        pst.setTimestamp(4, endTimestamp);

        ResultSet overlapRs = pst.executeQuery();
        return overlapRs.next();
    }

    /**
     * populates contact, customer, and user combo boxes
     */
    private void populateComboBox(){
        try{
            conn = JDBC.connection;
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT Customer_ID FROM customers");

            while (resultSet.next()){
                customerIdCB.getItems().add(resultSet.getInt("Customer_ID"));
            }
            resultSet.close();
            statement.close();

        }catch (Exception e) {
            e.printStackTrace();
        }
        try{
            conn = JDBC.connection;
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT Contact_Name FROM contacts");

            while (resultSet.next()){
                contactIdCB.getItems().add(resultSet.getString("Contact_Name"));
            }
            resultSet.close();
            statement.close();

        }catch (Exception e) {
            e.printStackTrace();
        }
        try{
            conn = JDBC.connection;
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT User_ID FROM users");

            while (resultSet.next()){
                userIdCB.getItems().add(resultSet.getInt("User_ID"));
            }
            resultSet.close();
            statement.close();

        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    /**
     * Initializes the spinners for correct time formatting, and calls the populate combo box method
     */
    public void initialize(URL url, ResourceBundle resourceBundle) {
        populateComboBox();

        startDateDP.setValue(LocalDate.now());
        SpinnerValueFactory<Integer> hourValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, LocalDateTime.now().getHour());
        startTimeHour.setValueFactory(hourValueFactory);
        SpinnerValueFactory<Integer> minuteValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, LocalDateTime.now().getMinute());
        startTimeMin.setValueFactory(minuteValueFactory);

        endDateDP.setValue(LocalDate.now());
        SpinnerValueFactory<Integer> endhourValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, LocalDateTime.now().getHour());
        endTimeHour.setValueFactory(endhourValueFactory);
        SpinnerValueFactory<Integer> endminuteValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, LocalDateTime.now().getMinute());
        endTimeMin.setValueFactory(endminuteValueFactory);




    }



}
