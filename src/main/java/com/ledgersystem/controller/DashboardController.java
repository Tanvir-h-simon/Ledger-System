package com.ledgersystem.controller;

import com.ledgersystem.repository.csv.CsvBankRepository;
import com.ledgersystem.repository.csv.CsvLoanRepository;
import com.ledgersystem.repository.csv.CsvUserRepository;
import com.ledgersystem.service.AuthService;
import com.ledgersystem.service.LoanService;
import com.ledgersystem.util.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class DashboardController {

    @FXML
    private Label welcomeLabel;

    // Red banner at the top — only made visible if the user's loan is overdue
    @FXML
    private Label overdueBanner;

    // The center pane where sub-views (transaction, history, etc.) are loaded into
    @FXML
    private StackPane centerPane;

    private final AuthService authService = new AuthService(new CsvUserRepository());
    private final LoanService loanService = new LoanService(new CsvLoanRepository(), new CsvBankRepository());

    // Called automatically by FXMLLoader once all @FXML fields are set
    @FXML
    public void initialize() {
        welcomeLabel.setText("Welcome, " + SessionManager.getCurrentUser().getName());

        // Check once at login if the loan is overdue and show the red banner if so
        int userId = SessionManager.getCurrentUser().getUserId();
        if (loanService.isLoanOverdue(userId)) {
            overdueBanner.setVisible(true);
            overdueBanner.setManaged(true);
        }

        // Show the home dashboard by default when the user logs in
        loadView("/com/ledgersystem/fxml/home.fxml");
    }

    // ----- Nav button handlers -----

    @FXML
    private void handleShowHome(ActionEvent event) {
        loadView("/com/ledgersystem/fxml/home.fxml");
    }

    @FXML
    private void handleShowTransaction(ActionEvent event) {
        loadView("/com/ledgersystem/fxml/transaction.fxml");
    }

    @FXML
    private void handleShowHistory(ActionEvent event) {
        loadView("/com/ledgersystem/fxml/history.fxml");
    }

    @FXML
    private void handleShowSavings(ActionEvent event) {
        loadView("/com/ledgersystem/fxml/savings.fxml");
    }

    @FXML
    private void handleShowLoan(ActionEvent event) {
        loadView("/com/ledgersystem/fxml/loan.fxml");
    }

    @FXML
    private void handleShowPredictor(ActionEvent event) {
        loadView("/com/ledgersystem/fxml/predictor.fxml");
    }

    @FXML
    private void handleShowCharts(ActionEvent event) {
        loadView("/com/ledgersystem/fxml/charts.fxml");
    }

    // ----- Logout -----

    @FXML
    private void handleLogout(ActionEvent event) {
        authService.logout();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ledgersystem/fxml/login.fxml"));
            Parent root = loader.load();

            // Logout replaces the whole scene — carry the CSS over so login stays styled
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            Scene newScene = new Scene(root, stage.getWidth(), stage.getHeight());
            newScene.getStylesheets().addAll(stage.getScene().getStylesheets());
            stage.setScene(newScene);
        } catch (IOException e) {
            System.out.println("Could not load login screen");
        }
    }

    // ----- View loader -----

    // Loads an FXML file and places it in the center pane, replacing whatever was there before
    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node view = loader.load();
            centerPane.getChildren().clear();
            centerPane.getChildren().add(view);
        } catch (Exception e) {
            // Print the full cause so errors are easy to diagnose
            System.out.println("Could not load view: " + fxmlPath);
            System.out.println("Reason: " + e.getMessage());
            if (e.getCause() != null) {
                System.out.println("Caused by: " + e.getCause().getMessage());
            }
        }
    }
}