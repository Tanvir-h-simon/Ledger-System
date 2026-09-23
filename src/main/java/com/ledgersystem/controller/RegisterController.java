package com.ledgersystem.controller;

import com.ledgersystem.repository.csv.CsvUserRepository;
import com.ledgersystem.service.AuthService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class RegisterController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label errorLabel;

    private final AuthService authService = new AuthService(new CsvUserRepository());

    @FXML
    private void handleRegister(ActionEvent event) {
        String name = nameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        try {
            authService.register(name, email, password, confirmPassword);
            handleGoToLogin(event);
        } catch (IllegalArgumentException e) {
            errorLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void handleGoToLogin(ActionEvent event) {
        switchScene(event, "/com/ledgersystem/fxml/login.fxml");
    }

    private void switchScene(ActionEvent event, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();

            // Use scene dimensions (not stage dimensions) to avoid window growing
            double width  = stage.getScene().getWidth();
            double height = stage.getScene().getHeight();

            Scene newScene = new Scene(root, width, height);
            newScene.getStylesheets().addAll(stage.getScene().getStylesheets());
            stage.setScene(newScene);
        } catch (IOException e) {
            errorLabel.setText("Could not load the next screen");
        }
    }

}