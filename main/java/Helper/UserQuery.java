package Helper;

import com.example.c195_version1.HelloApplication;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

/**
 * User query class
 */
public abstract class UserQuery {
    /**
     * Selects information from user table and verifies user information
     * @param userName
     * @param passWord
     * @return true or false
     * @throws SQLException
     * @throws IOException
     */
    public static boolean select(String userName, String passWord) throws SQLException, IOException {
        String sql = "SELECT * FROM USERS WHERE User_Name = ? AND Password = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1,userName);
        ps.setString(2,passWord);
        ResultSet resultSet = ps.executeQuery();

        if (resultSet.next()){
            HelloApplication m = new HelloApplication();
            m.changeScene("mainMenu.fxml");
            return true;

        }
        else {
            if (Locale.getDefault().getLanguage().equals("fr")) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("");
                alert.setContentText("identifiant ou mot de passe incorrect.");
                alert.showAndWait();

            }else {Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("");
                alert.setContentText("Incorrect username or password.");
                alert.showAndWait();

            }
        }

        return false;
    }
}
