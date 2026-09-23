package com.ledgersystem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ledgersystem/fxml/login.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 1400, 860);

        // Apply the global stylesheet so every screen is styled
        scene.getStylesheets().add(getClass().getResource("/com/ledgersystem/css/style.css").toExternalForm());

        primaryStage.setTitle("Ledger System");
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}