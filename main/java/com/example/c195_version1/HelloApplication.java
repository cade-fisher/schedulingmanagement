package com.example.c195_version1;

import Helper.JDBC;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JAVADOCS FOLDER LOCATION: In this project under the scr folder
 * @author cadefisher
 *
 */
public class HelloApplication extends Application {
   
    private static  Stage stg;
    @Override
    /**
     * Launches application
     */
    public void start(Stage primaryStage) throws Exception {
        stg = primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
        primaryStage.setTitle("login");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    /**
     * Allows scene to change to mainMenu after logging in
     * @param fxml fxml
     * @throws IOException IOException
     */
    public void changeScene(String fxml) throws IOException{
        Parent pane = FXMLLoader.load(getClass().getResource(fxml));
        stg.setMaximized(true);
        stg.getScene().setRoot(pane);
    }

    /**
     * launches application
     * @param args arguments
     */
    public static void main(String[] args) {
        JDBC.openConnection();
        launch();
        JDBC.closeConnection();
    }
}

