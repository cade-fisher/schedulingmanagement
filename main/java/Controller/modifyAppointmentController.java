package Controller;

import Helper.JDBC;
import com.example.c195_version1.Appointment;
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
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

/**
 * Modify appointment controller class
 */
public class modifyAppointmentController implements Initializable {
    /**
     * Appoinmtnet ID text field
     */
    public TextField ModappintmentIdTXT;
    /**
     * title text field
     */
    public TextField modtitleTXT;
    /**
     * description text field
     */
    public TextField ModdescriptionTXT;
    /**
     * location text field
     */
    public TextField ModlocationTXT;
    /**
     * type text field
     */
    public TextField ModtypeTXT;
    /**
     * contact name combo box
     */
    public ComboBox<String> modcontactIdCB;
    /**
     * customer ID combo box
     */
    public ComboBox<Integer> ModcustomerIdCB;
    /**
     * user ID combo box
     */
    public ComboBox<Integer> ModuserIdCB;
    /**
     * Start date, date picker
     */
    public DatePicker ModstartDateDP;
    /**
     * end date, date picker
     */
    public DatePicker ModendDateDP;
    /**
     * start minute spinner
     */
    public Spinner<Integer> ModstartTimeMin;
    /**
     * start hour spinner
     */
    public Spinner<Integer> ModstartTimeHour;
    /**
     * end minute spinner
     */
    public Spinner<Integer> ModendTimeMin;
    /**
     * end hour spinner
     */
    public Spinner<Integer> ModendTimeHour;
    /**
     * Save button
     */
    public Button saveBTN;
    /**
     * Cancel button
     */
    public Button cancelBTN;
    /**
     * Mock appointment
     */
    public Appointment theAppointment;
    private static int theIndex;
    Connection conn = null;
    PreparedStatement pst = null;
    private LocalDateTime getStartDateFromDatabase(int appointmentId) {
        try {
            conn = JDBC.connection;
            String getStartDateSql = "SELECT Start FROM appointments WHERE Appointment_ID = ?";
            try (PreparedStatement getStartDateStmt = conn.prepareStatement(getStartDateSql)) {
                getStartDateStmt.setInt(1, appointmentId);
                try (ResultSet getStartDateRs = getStartDateStmt.executeQuery()) {
                    if (getStartDateRs.next()) {
                        String startString = getStartDateRs.getString("Start");
                        LocalDateTime startDateTime = LocalDateTime.parse(startString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        return startDateTime.atZone(ZoneOffset.UTC).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return LocalDateTime.now();
    }

    private LocalDateTime getEndDateFromDatabase(int appointmentId) {
        try {
            conn = JDBC.connection;
            String getEndDateSql = "SELECT End FROM appointments WHERE Appointment_ID = ?";
            try (PreparedStatement getEndDateStmt = conn.prepareStatement(getEndDateSql)) {
                getEndDateStmt.setInt(1, appointmentId);
                try (ResultSet getEndDateRs = getEndDateStmt.executeQuery()) {
                    if (getEndDateRs.next()) {
                        String endString = getEndDateRs.getString("End");
                        LocalDateTime endDateTime = LocalDateTime.parse(endString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        return endDateTime.atZone(ZoneOffset.UTC).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return LocalDateTime.now();
    }
    private int getStartHourFromDatabase(int appointmentId) {
        try {
            conn = JDBC.connection;
            String getStartHourSql = "SELECT Start FROM appointments WHERE Appointment_ID = ?";
            try (PreparedStatement getStartHourStmt = conn.prepareStatement(getStartHourSql)) {
                getStartHourStmt.setInt(1, appointmentId);
                try (ResultSet getStartHourRs = getStartHourStmt.executeQuery()) {
                    if (getStartHourRs.next()) {
                        String startString = getStartHourRs.getString("Start");
                        LocalDateTime startDateTime = LocalDateTime.parse(startString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                        ZoneId userTimeZone = ZoneId.systemDefault();
                        ZonedDateTime localStartDateTime = startDateTime.atZone(ZoneOffset.UTC).withZoneSameInstant(userTimeZone);

                        return localStartDateTime.getHour();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    private int getStartMinuteFromDatabase(int appointmentId) {
        try {
            conn = JDBC.connection;
            String getStartMinuteSql = "SELECT Start FROM appointments WHERE Appointment_ID = ?";
            try (PreparedStatement getStartMinuteStmt = conn.prepareStatement(getStartMinuteSql)) {
                getStartMinuteStmt.setInt(1, appointmentId);
                try (ResultSet getStartMinuteRs = getStartMinuteStmt.executeQuery()) {
                    if (getStartMinuteRs.next()) {
                        String startString = getStartMinuteRs.getString("Start");
                        LocalDateTime startDateTime = LocalDateTime.parse(startString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        return startDateTime.getMinute();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    private int getEndHourFromDatabase(int appointmentId) {
        try {
            conn = JDBC.connection;
            String getEndHourSql = "SELECT End FROM appointments WHERE Appointment_ID = ?";
            try (PreparedStatement getEndHourStmt = conn.prepareStatement(getEndHourSql)) {
                getEndHourStmt.setInt(1, appointmentId);
                try (ResultSet getEndHourRs = getEndHourStmt.executeQuery()) {
                    if (getEndHourRs.next()) {
                        String endString = getEndHourRs.getString("End");
                        LocalDateTime endDateTime = LocalDateTime.parse(endString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        ZoneId userTimeZone = ZoneId.systemDefault();
                        ZonedDateTime localEndDateTime = endDateTime.atZone(ZoneOffset.UTC).withZoneSameInstant(userTimeZone);

                        return localEndDateTime.getHour();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    private int getEndMinuteFromDatabase(int appointmentId) {
        try {
            conn = JDBC.connection;
            String getStartMinuteSql = "SELECT End FROM appointments WHERE Appointment_ID = ?";
            try (PreparedStatement getStartMinuteStmt = conn.prepareStatement(getStartMinuteSql)) {
                getStartMinuteStmt.setInt(1, appointmentId);
                try (ResultSet getStartMinuteRs = getStartMinuteStmt.executeQuery()) {
                    if (getStartMinuteRs.next()) {
                        String startString = getStartMinuteRs.getString("End");
                        LocalDateTime startDateTime = LocalDateTime.parse(startString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        return startDateTime.getMinute();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Passes appointment data to controller
     * @param selectedAppt
     * @param selectedindex
     */
    public void passAppt (Appointment selectedAppt, int selectedindex){
        LocalDateTime startDateTimeUTC = getStartDateFromDatabase(selectedAppt.getApptId());
        LocalDateTime endDateTimeUTC = getEndDateFromDatabase(selectedAppt.getApptId());
        ZoneId userTimeZone = ZoneId.systemDefault();
        LocalDateTime startDateTime = startDateTimeUTC.atZone(ZoneOffset.UTC).withZoneSameInstant(userTimeZone).toLocalDateTime();
        LocalDateTime endDateTime = endDateTimeUTC.atZone(ZoneOffset.UTC).withZoneSameInstant(userTimeZone).toLocalDateTime();


        theAppointment = selectedAppt;
        theIndex = selectedindex;
        String contactName = getContactNameById(theAppointment.getContactId());
        ModappintmentIdTXT.setText(Integer.toString(theAppointment.getApptId()));
        modtitleTXT.setText(theAppointment.getTitle());
        ModdescriptionTXT.setText(theAppointment.getDescription());
        ModlocationTXT.setText(theAppointment.getLocation());
        ModtypeTXT.setText(theAppointment.getType());
        modcontactIdCB.setValue(contactName);
        ModcustomerIdCB.setValue(theAppointment.getCustomerId());
        ModuserIdCB.setValue(theAppointment.getUserId());
        ModstartDateDP.setValue(startDateTime.toLocalDate());
        ModendDateDP.setValue(endDateTime.toLocalDate());

        ModstartTimeHour.getValueFactory().setValue(getStartHourFromDatabase(selectedAppt.getApptId()));
        ModstartTimeMin.getValueFactory().setValue(getStartMinuteFromDatabase(selectedAppt.getApptId()));
        ModendTimeHour.getValueFactory().setValue(getEndHourFromDatabase(selectedAppt.getApptId()));
        ModendTimeMin.getValueFactory().setValue(getEndMinuteFromDatabase(selectedAppt.getApptId()));
    }
    private String getContactNameById(int contactId) {
        try {
            conn = JDBC.connection;
            String getNameSql = "SELECT Contact_Name FROM contacts WHERE Contact_ID = ?";
            try (PreparedStatement getNameStmt = conn.prepareStatement(getNameSql)) {
                getNameStmt.setInt(1, contactId);

                try (ResultSet getNameRs = getNameStmt.executeQuery()) {
                    if (getNameRs.next()) {
                        return getNameRs.getString("Contact_Name");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Contact Name";
    }

    /**
     * Returns back to main menu
     * @param actionEvent
     * @throws IOException
     */
    public void onActionMainMenu(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/c195_version1/mainMenu.fxml"));
        Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 970, 760);
        stage.setTitle("Main Menu");
        stage.setScene(scene);
        stage.show();
    }
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
     * Saves the modification to existing appointment
     * Checks if start time is before end time and gives user an error
     * Checks if the scheduled time is between business hours
     * Checks is there are overlapping appointments
     * Returns back to main menu
     * @param actionEvent
     * @throws IOException
     * @throws SQLException
     */
    public void onActionSaveModAppt(ActionEvent actionEvent) throws IOException, SQLException {

        LocalDate startSelectedDate = ModstartDateDP.getValue();
        int startHour = ModstartTimeHour.getValue();
        int startMin = ModstartTimeMin.getValue();
        LocalDateTime startTime = LocalDateTime.of(startSelectedDate, LocalTime.of(startHour,startMin));

        LocalDate endSelectedDate = ModendDateDP.getValue();
        int endHour = ModendTimeHour.getValue();
        int endMin = ModendTimeMin.getValue();
        LocalDateTime endTime = LocalDateTime.of(endSelectedDate, LocalTime.of(endHour,endMin));

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
        LocalDateTime startTimeUTC = startTime.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        LocalDateTime endTimeUTC = endTime.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        int selectedContactId = getContactIdFromName(modcontactIdCB.getValue());
        String sql = "UPDATE appointments SET Title=?, Description=?, Location=?, Type=?, Start=?, End=?, Customer_ID=?, User_ID=?, Contact_ID=?, Last_Update=NOW(), Last_Updated_By='script' WHERE Appointment_ID=?";
        try {
            pst = conn.prepareStatement(sql);
            pst.setString(1, modtitleTXT.getText());
            pst.setString(2, ModdescriptionTXT.getText());
            pst.setString(3, ModlocationTXT.getText());
            pst.setString(4, ModtypeTXT.getText());
            pst.setString(5, String.valueOf(startTimeUTC));
            pst.setString(6, String.valueOf(endTimeUTC));
            pst.setInt(7, ModcustomerIdCB.getValue());
            pst.setInt(8, ModuserIdCB.getValue());
            pst.setInt(9, selectedContactId);
            pst.setInt(10, Integer.parseInt(ModappintmentIdTXT.getText()));
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
    private boolean isAppointmentOverlapping(LocalDateTime newStart, LocalDateTime newEnd) throws SQLException {
        conn = JDBC.connection;
        String overlapSql = "SELECT * FROM appointments " +
                "WHERE (( ? < End AND ? > Start ) " +
                "OR ( ? <= End AND ? >= Start )) " +
                "AND Appointment_ID <> ?";
        pst = conn.prepareStatement(overlapSql);

        Timestamp startTimestamp = Timestamp.valueOf(newStart);
        Timestamp endTimestamp = Timestamp.valueOf(newEnd);

        pst.setTimestamp(1, startTimestamp);
        pst.setTimestamp(2, endTimestamp);
        pst.setTimestamp(3, startTimestamp);
        pst.setTimestamp(4, endTimestamp);
        pst.setInt(5, Integer.parseInt(ModappintmentIdTXT.getText()));

        ResultSet overlapRs = pst.executeQuery();
        return overlapRs.next();
    }

    private void populateComboBox(){
        try{
            conn = JDBC.connection;
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT Customer_ID FROM customers");

            while (resultSet.next()){
                ModcustomerIdCB.getItems().add(resultSet.getInt("Customer_ID"));
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
                modcontactIdCB.getItems().add(resultSet.getString("Contact_Name"));
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
                ModuserIdCB.getItems().add(resultSet.getInt("User_ID"));
            }
            resultSet.close();
            statement.close();

        }catch (Exception e) {
            e.printStackTrace();
        }

    }
    @Override
    /**
     * Initializes controller, populates combo boxes and allows spinners to function
     */
    public void initialize(URL url, ResourceBundle resourceBundle) {
        populateComboBox();


        SpinnerValueFactory<Integer> hourValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23);
        ModstartTimeHour.setValueFactory(hourValueFactory);
        SpinnerValueFactory<Integer> minuteValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59);
        ModstartTimeMin.setValueFactory(minuteValueFactory);


        SpinnerValueFactory<Integer> endhourValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23);
        ModendTimeHour.setValueFactory(endhourValueFactory);
        SpinnerValueFactory<Integer> endminuteValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59);
        ModendTimeMin.setValueFactory(endminuteValueFactory);
    }
}
