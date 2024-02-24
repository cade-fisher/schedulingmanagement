package Controller;

import Helper.UserQuery;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TimeZone;

/**
 * Login controller
 */
public class loginController implements Initializable {
    /**
     * Username text field
     */
    public TextField userNameTXTField;
    /**
     * Password text field
     */
    public TextField passwordTXTField;
    /**
     * timezone label
     */
    public Label timeZoneChangeTXT;
    /**
     * language choice box
     */
    public ChoiceBox<String> languageCB;
    /**
     * login button
     */
    public Button loginBTN;
    /**
     * reset button
     */
    public Button resetBTN;
    /**
     * username label
     */
    public Label userNameTXT;
    /**
     * password label
     */
    public Label passwordTXT;
    /**
     * language label
     */
    public Label languageTXT;
    /**
     * timezone label
     */
    public Label TimeZoneTXT;
    /**
     * login label
     */
    public Label loginTXT;
    private String[] langauge = {"English","French"};

    /**
     * Login button uses Userquery to verify information from database and will send user to main menu
     * @param actionEvent
     * @throws IOException
     * @throws SQLException
     */
    public void loginBTN(ActionEvent actionEvent) throws IOException, SQLException {
        boolean loginSuccess = UserQuery.select(userNameTXTField.getText(), passwordTXTField.getText());

        logLoginAttempt(userNameTXTField.getText(), LocalDateTime.now(), loginSuccess);
    }
    private void logLoginAttempt(String username, LocalDateTime timestamp, boolean success) {
        String logEntry = String.format("Username: %s | Timestamp: %s | Success: %s%n",
                username, timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), success);

        try {
            String filePath = "src/main/resources/login_activity.txt";
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                Files.createFile(path);
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
                writer.write(logEntry);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * resets text fields
     * @param actionEvent
     */
    public void resetBTN(ActionEvent actionEvent) {
        userNameTXTField.setText("");
        passwordTXTField.setText("");

    }


    @Override
    /**
     * initializes timezone based on users computers setting.
     * Also changes language based on computer settings.
     * Will also change language based on language choice box
     */
    public void initialize(URL url, ResourceBundle resourceBundle) {
        TimeZone timeZone = TimeZone.getDefault();
        String timeZoneName = timeZone.getDisplayName();
        timeZoneChangeTXT.setText(timeZoneName);
        languageCB.setOnAction(actionEvent -> {
            String selectedlanguage = languageCB.getValue();
            switch (selectedlanguage) {
                case "French":
                    passwordTXT.setText("Mot de passe");
                    TimeZoneTXT.setText("Fuseau horaire:");
                    loginBTN.setText("Se connecter");
                    userNameTXT.setText("Nom d'utilisateur");
                    resetBTN.setText("Réinitialiser");
                    languageTXT.setText("Langue");
                    loginTXT.setText("Se connecter");
                    break;

                case "English":
                    passwordTXT.setText("Password");
                    TimeZoneTXT.setText("Time Zone:");
                    loginBTN.setText("Login");
                    userNameTXT.setText("Username");
                    resetBTN.setText("Reset");
                    languageTXT.setText("Language");
                    loginTXT.setText("Login");
                    break;


            }
        });
        languageCB.getItems().addAll(langauge);
        Locale defaultLocale = Locale.getDefault();
        if (defaultLocale.getLanguage().equals("fr")) {
            languageCB.setValue("French");
        } else {
            languageCB.setValue("English");
        }



    }


}
