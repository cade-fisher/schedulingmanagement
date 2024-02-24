module com.example.c195_version1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.c195_version1 to javafx.fxml;
    exports com.example.c195_version1;
    exports Controller;
    opens Controller to javafx.fxml;
}