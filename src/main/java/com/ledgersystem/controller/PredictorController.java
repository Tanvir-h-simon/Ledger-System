package com.ledgersystem.controller;

import com.ledgersystem.repository.csv.CsvBankRepository;
import com.ledgersystem.service.InterestPredictorService;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.util.List;

public class PredictorController {

    @FXML
    private TextField amountField;

    @FXML
    private TextField monthsField;

    @FXML
    private ListView<String> resultList;

    @FXML
    private Label messageLabel;

    private final InterestPredictorService predictorService =
            new InterestPredictorService(new CsvBankRepository());

    @FXML
    private void handleCalculate(ActionEvent event) {
        double amount;
        try {
            amount = Double.parseDouble(amountField.getText().trim());
        } catch (NumberFormatException e) {
            showMessage("Enter a valid deposit amount", false);
            return;
        }

        int months;
        try {
            months = Integer.parseInt(monthsField.getText().trim());
        } catch (NumberFormatException e) {
            showMessage("Enter a valid number of months", false);
            return;
        }

        try {
            List<String> results = predictorService.predict(amount, months);
            resultList.setItems(FXCollections.observableArrayList(results));
            messageLabel.setText("");
        } catch (IllegalArgumentException e) {
            showMessage(e.getMessage(), false);
        }
    }

    private void showMessage(String text, boolean success) {
        messageLabel.setStyle(success ? "-fx-text-fill: green;" : "-fx-text-fill: red;");
        messageLabel.setText(text);
    }
}
