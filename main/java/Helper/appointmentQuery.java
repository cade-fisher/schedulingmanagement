package Helper;

import com.example.c195_version1.Appointment;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Appointment query class
 */
public abstract class appointmentQuery {
    Connection conn = null;

    /**
     * Creates an observable list for appointments and gets data from appointment table
     * @return observable list
     */
    public static ObservableList<Appointment> getDataAppt() {
        Connection conn = JDBC.connection;
        ObservableList<Appointment> list = FXCollections.observableArrayList();

        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM APPOINTMENTS");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int appointmentId = Integer.parseInt(rs.getString("Appointment_ID"));
                String title = rs.getString("Title");
                String description = rs.getString("Description");
                String location = rs.getString("Location");
                int contactId = Integer.parseInt(rs.getString("Contact_ID"));
                String type = rs.getString("Type");
                String start = rs.getString("Start");
                String end = rs.getString("End");
                int customerId = Integer.parseInt(rs.getString("Customer_ID"));
                int userId = Integer.parseInt(rs.getString("User_ID"));

                list.add(new Appointment(appointmentId, title, description, location, contactId, type, start, end, customerId, userId));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Retrieves contact name from contact ID
     * @param contactId
     * @return contact name
     */
    public static String getContactNameById(int contactId) {
        Connection conn = null;
        try {
            conn = JDBC.connection;
            String query = "SELECT Contact_Name FROM contacts WHERE Contact_ID = ?";
            try (PreparedStatement pst = conn.prepareStatement(query)) {
                pst.setInt(1, contactId);

                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("Contact_Name");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Contact Name";
    }


}
