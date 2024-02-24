package Helper;

import com.example.c195_version1.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * customer query class
 */
public abstract class customerQuery {
    static PreparedStatement pst = null;

    /**
     * Gets customer data from database
     * @return Observable list
     */
    public static ObservableList getDataCustomer(){
        Connection conn = JDBC.connection;
        ObservableList list = FXCollections.observableArrayList();
        try{
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM CUSTOMERS");
            ResultSet rs = ps.executeQuery();

            while (rs.next()){
                list.add(new Customer(Integer.parseInt(rs.getString("Customer_ID")),rs.getString("Customer_Name"),rs.getString("Address"),rs.getString("Postal_Code"),rs.getString("Phone")));

            }
        } catch (SQLException e) {
        }

        return list;
    }

}
