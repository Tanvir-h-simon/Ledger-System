package com.ledgersystem.controller;

import com.ledgersystem.model.Transaction;
import com.ledgersystem.repository.csv.CsvLoanRepository;
import com.ledgersystem.repository.csv.CsvSavingsRepository;
import com.ledgersystem.repository.csv.CsvTransactionRepository;
import com.ledgersystem.service.LedgerService;
import com.ledgersystem.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class TransactionController {

    @FXML
    private Label balanceLabel;

    @FXML
    private TextField amountField;

    @FXML
    private ComboBox<String> categoryBox;

    @FXML
    private TextField descriptionField;

    @FXML
    private Label messageLabel;

    private final LedgerService ledgerService =
            new LedgerService(new CsvTransactionRepository(), new CsvLoanRepository(), new CsvSavingsRepository());

    @FXML
    public void initialize() {
        categoryBox.setItems(FXCollections.observableArrayList("Food", "Rent", "Transport", "Other"));
        refreshBalance();
    }

    @FXML
    private void handleDebit(ActionEvent event) {
        recordTransaction(true);
    }

    @FXML
    private void handleCredit(ActionEvent event) {
        recordTransaction(false);
    }

    private void recordTransaction(boolean isDebit) {
        int userId = SessionManager.getCurrentUser().getUserId();
        String category = categoryBox.getValue();
        String description = descriptionField.getText();

        double amount;
        try {
            amount = Double.parseDouble(amountField.getText());
        } catch (NumberFormatException e) {
            showMessage("Enter a valid amount", false);
            return;
        }

        try {
            if (isDebit) {
                ledgerService.recordDebit(userId, amount, category, description);
            } else {
                ledgerService.recordCredit(userId, amount, category, description);
            }

            showMessage(isDebit ? "Debit recorded" : "Credit recorded", true);
            clearForm();
            refreshBalance();

        } catch (IllegalArgumentException | IllegalStateException e) {
            showMessage(e.getMessage(), false);
        }
    }

    private void refreshBalance() {
        int userId = SessionManager.getCurrentUser().getUserId();
        double balance = ledgerService.getBalance(userId);
        balanceLabel.setText(String.format("Balance: %.2f", balance));
    }

    private void clearForm() {
        amountField.clear();
        categoryBox.setValue(null);
        descriptionField.clear();
    }

    private void showMessage(String text, boolean success) {
        messageLabel.setStyle(success ? "-fx-text-fill: green;" : "-fx-text-fill: red;");
        messageLabel.setText(text);
    }
}
