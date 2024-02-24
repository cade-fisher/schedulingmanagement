package Controller;

import Helper.JDBC;
import javafx.collections.FXCollections;
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
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Add customer controller
 */

public class addCustomerController implements Initializable {
    /**
     * customer ID text field
     */
    public TextField customerIIdTXT;
    /**
     * customer name text field
     */
    public TextField nameTXT;
    /**
     * phone number text field
     */
    public TextField phoneNoTXT;
    /**
     * address text field
     */
    public TextField addressTXT;
    /**
     * postal code text field
     */
    public TextField postalCodeTXT;
    /**
     * Save button
     */

    public Button saveBTN;
    /**
     * cancel button
     */
    public Button cancelBTN;
    /**
     * country choice box
     */
    public ChoiceBox<String> countryChoiceBox;
    /**
     * state choice box
     */
    public ChoiceBox<String> stateChoiceBox;
    Connection conn = null;
    ResultSet rs = null;
    PreparedStatement pst = null;




        private String[][] states = {
                {"Alabama","Alaska","Arizona","Arkansas","California","Colorado","Connecticut","Delaware","Florida","Georgia","Hawaii","Idaho","Illinois","Indiana","Iowa","Kansas","Kentucky","Louisiana","Maine","Maryland","Massachusetts","Michigan","Minnesota","Mississippi","Montana","Nebraska","Nevada","New Hampshire","New Jersey","New Mexico","New York","North Carolina","North Dakota","Ohio","Oklahoma","Oregon","Pennsylvania","Rhode Island","South Carolina","South Dakota","Tennessee","Texas","Utah","Vermont","Virginia","Washington","West Virginia","Wisconsin","Wyoming"},
                {"England", "Scotland", "Wales", "Northern Ireland"},
                {"Alberta","British Columbia","Manitoba","New Brunswick","Newfoundland and Labrador","Nova Scotia","Ontario","Prince Edward Island","Quebec","Saskatchewan"}
        };
        private Map<String, Integer> divisionNumbers = new HashMap<>();

    /**
     * Returns to main menu
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

    /**
     * Saves customer to database and creates customer ID based on database. Returns user back to main menu
     * @param actionEvent
     * @throws SQLException
     * @throws IOException
     */

    public void  onActionAddCustomer(ActionEvent actionEvent) throws SQLException, IOException {

        String selectedCountry = countryChoiceBox.getValue();
        String selectedState = stateChoiceBox.getValue();
        int divisionNumber = getDivisionNumber(selectedState);
        conn = JDBC.connection;

        String getIdSql = "SELECT MAX(Customer_ID) + 1 FROM customers";
        Statement getIdStmt = conn.createStatement();
        ResultSet getIdRs = getIdStmt.executeQuery(getIdSql);
        int nextCustomerId = 1; // Default value if no customers exist yet
        if (getIdRs.next()) {
            nextCustomerId = getIdRs.getInt(1);
        }

        String sql = "INSERT INTO customers VALUES (?, ?, ?, ?, ?, NOW(), 'script', NOW(), 'script', ?)";
        try {
            pst = conn.prepareStatement(sql);
            pst.setInt(1, nextCustomerId);
            pst.setString(2, nameTXT.getText());
            pst.setString(3, addressTXT.getText());
            pst.setString(4, postalCodeTXT.getText());
            pst.setString(5, phoneNoTXT.getText());
            pst.setInt(6, divisionNumber);
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
     * Initializes country and state choice box
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        countryChoiceBox.setItems(FXCollections.observableArrayList("United States", "United Kingdom", "Canada"));
        countryChoiceBox.setOnAction(this::onCountrySelected);
        updateStates();
        initializeDivisionNumber();
    }
    private void onCountrySelected(ActionEvent event){
        updateStates();
    }

    private void updateStates(){
        int selectedCountryIndex = countryChoiceBox.getSelectionModel().getSelectedIndex();
        if (selectedCountryIndex >= 0 && selectedCountryIndex < states.length) {
            stateChoiceBox.setItems(FXCollections.observableArrayList(states[selectedCountryIndex]));
        } else {
            stateChoiceBox.setItems(FXCollections.emptyObservableList());
        }

    }
    private void initializeDivisionNumber(){
        divisionNumbers.put("Alabama", 1);
        divisionNumbers.put("Arizona", 2);
        divisionNumbers.put("Arkansas", 3);
        divisionNumbers.put("California", 4);
        divisionNumbers.put("Colorado", 5);
        divisionNumbers.put("Connecticut", 6);
        divisionNumbers.put("Delaware", 7);
        divisionNumbers.put("District of Columbia", 8);
        divisionNumbers.put("Florida", 9);
        divisionNumbers.put("Georgia", 10);
        divisionNumbers.put("Hawaii", 52);
        divisionNumbers.put("Idaho", 11);
        divisionNumbers.put("Illinois", 12);
        divisionNumbers.put("Indiana", 13);
        divisionNumbers.put("Iowa", 14);
        divisionNumbers.put("Kansas", 15);
        divisionNumbers.put("Kentucky", 16);
        divisionNumbers.put("Louisiana", 17);
        divisionNumbers.put("Maine", 18);
        divisionNumbers.put("Maryland", 19);
        divisionNumbers.put("Massachusetts", 20);
        divisionNumbers.put("Michigan", 21);
        divisionNumbers.put("Minnesota", 22);
        divisionNumbers.put("Mississippi", 23);
        divisionNumbers.put("Missouri", 24);
        divisionNumbers.put("Montana", 25);
        divisionNumbers.put("Nebraska", 26);
        divisionNumbers.put("Nevada", 27);
        divisionNumbers.put("New Hampshire", 28);
        divisionNumbers.put("New Jersey", 29);
        divisionNumbers.put("New Mexico", 30);
        divisionNumbers.put("New York", 31);
        divisionNumbers.put("North Carolina", 32);
        divisionNumbers.put("North Dakota", 33);
        divisionNumbers.put("Ohio", 34);
        divisionNumbers.put("Oklahoma", 35);
        divisionNumbers.put("Oregon", 36);
        divisionNumbers.put("Pennsylvania", 37);
        divisionNumbers.put("Rhode Island", 38);
        divisionNumbers.put("South Carolina", 39);
        divisionNumbers.put("South Dakota", 40);
        divisionNumbers.put("Tennessee", 41);
        divisionNumbers.put("Texas", 42);
        divisionNumbers.put("Utah", 43);
        divisionNumbers.put("Vermont", 44);
        divisionNumbers.put("Virginia", 45);
        divisionNumbers.put("Washington", 46);
        divisionNumbers.put("West Virginia", 47);
        divisionNumbers.put("Wisconsin", 48);
        divisionNumbers.put("Wyoming", 49);
        divisionNumbers.put("Alaska", 54);
        divisionNumbers.put("Northwest Territories", 60);
        divisionNumbers.put("Alberta", 61);
        divisionNumbers.put("British Columbia", 62);
        divisionNumbers.put("Manitoba", 63);
        divisionNumbers.put("New Brunswick", 64);
        divisionNumbers.put("Nova Scotia", 65);
        divisionNumbers.put("Prince Edward Island", 66);
        divisionNumbers.put("Ontario", 67);
        divisionNumbers.put("Québec", 68);
        divisionNumbers.put("Saskatchewan", 69);
        divisionNumbers.put("Nunavut", 70);
        divisionNumbers.put("Yukon", 71);
        divisionNumbers.put("Newfoundland and Labrador", 72);
        divisionNumbers.put("England", 101);
        divisionNumbers.put("Wales", 102);
        divisionNumbers.put("Scotland", 103);
        divisionNumbers.put("Northern Ireland", 104);

    }
    private int getDivisionNumber (String state){
        return divisionNumbers.getOrDefault(state, 0);
    }
    }


