package com.ledgersystem.controller;

import com.ledgersystem.model.Savings;
import com.ledgersystem.repository.csv.CsvSavingsRepository;
import com.ledgersystem.repository.csv.CsvTransactionRepository;
import com.ledgersystem.service.SavingsService;
import com.ledgersystem.util.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class SavingsController {

    @FXML
    private Label statusLabel;

    @FXML
    private Label percentLabel;

    @FXML
    private Label totalLabel;

    @FXML
    private TextField percentageField;

    @FXML
    private Label messageLabel;

    private final SavingsService savingsService =
            new SavingsService(new CsvSavingsRepository(), new CsvTransactionRepository());

    @FXML
    public void initialize() {
        refreshInfo();
    }

    @FXML
    private void handleEnable(ActionEvent event) {
        String input = percentageField.getText().trim();

        int percentage;
        try {
            percentage = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            showMessage("Enter a whole number for the percentage", false);
            return;
        }

        try {
            int userId = SessionManager.getCurrentUser().getUserId();
            savingsService.activate(userId, percentage);
            refreshInfo();
            showMessage("Savings enabled at " + percentage + "%", true);
        } catch (IllegalArgumentException e) {
            showMessage(e.getMessage(), false);
        }
    }

    @FXML
    private void handleDisable(ActionEvent event) {
        try {
            int userId = SessionManager.getCurrentUser().getUserId();
            savingsService.deactivate(userId);
            refreshInfo();
            showMessage("Savings disabled", true);
        } catch (IllegalArgumentException e) {
            showMessage(e.getMessage(), false);
        }
    }

    // Reloads the status, percentage, and total saved labels from the service
    private void refreshInfo() {
        int userId = SessionManager.getCurrentUser().getUserId();
        Savings savings = savingsService.getSavings(userId);

        if (savings == null) {
            statusLabel.setText("Status: Not set up");
            percentLabel.setText("Percentage: —");
        } else if (savings.isActive()) {
            statusLabel.setText("Status: Active");
            percentLabel.setText("Percentage: " + savings.getPercentage() + "%");
        } else {
            statusLabel.setText("Status: Inactive");
            percentLabel.setText("Percentage: " + savings.getPercentage() + "% (paused)");
        }

        double totalSaved = savingsService.getTotalSaved(userId);
        totalLabel.setText(String.format("Total saved so far: %.2f", totalSaved));
    }

    private void showMessage(String text, boolean success) {
        messageLabel.setStyle(success ? "-fx-text-fill: green;" : "-fx-text-fill: red;");
        messageLabel.setText(text);
    }
}
