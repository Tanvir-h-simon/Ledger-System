package com.ledgersystem.controller;

import com.ledgersystem.model.Transaction;
import com.ledgersystem.repository.csv.CsvTransactionRepository;
import com.ledgersystem.service.HistoryService;
import com.ledgersystem.util.CSVExporter;
import com.ledgersystem.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public class HistoryController {

    @FXML
    private ComboBox<String> monthFilterBox;

    @FXML
    private ComboBox<String> sortBox;

    @FXML
    private ListView<String> transactionList;

    @FXML
    private Label messageLabel;

    private final HistoryService historyService = new HistoryService(new CsvTransactionRepository());
    private List<Transaction> currentTransactions;

    @FXML
    public void initialize() {
        // Setup filter options (1 to 12 for months, ignoring year for simplicity in this assignment)
        ObservableList<String> months = FXCollections.observableArrayList(
                "All", "1 (Jan)", "2 (Feb)", "3 (Mar)", "4 (Apr)", "5 (May)", "6 (Jun)",
                "7 (Jul)", "8 (Aug)", "9 (Sep)", "10 (Oct)", "11 (Nov)", "12 (Dec)"
        );
        monthFilterBox.setItems(months);
        monthFilterBox.setValue("All");

        // Setup sort options
        ObservableList<String> sortOptions = FXCollections.observableArrayList(
                "Date (Newest First)", "Date (Oldest First)", "Amount (High to Low)", "Amount (Low to High)"
        );
        sortBox.setItems(sortOptions);
        sortBox.setValue("Date (Newest First)");

        loadAllTransactions();
    }

    private void loadAllTransactions() {
        int userId = SessionManager.getCurrentUser().getUserId();
        currentTransactions = historyService.getAll(userId);
        displayTransactions(currentTransactions);
    }

    @FXML
    private void handleApplyFilters(ActionEvent event) {
        int userId = SessionManager.getCurrentUser().getUserId();
        
        // Start with all transactions
        List<Transaction> filtered = historyService.getAll(userId);

        // Apply month filter
        String monthValue = monthFilterBox.getValue();
        if (monthValue != null && !monthValue.equals("All")) {
            // Extract the month number from "1 (Jan)" -> "1"
            int month = Integer.parseInt(monthValue.split(" ")[0]);
            
            // For this project, we assume current year to keep it simple, 
            // otherwise we'd need a year dropdown too.
            int currentYear = java.time.LocalDate.now().getYear();
            filtered = historyService.filterByMonth(filtered, currentYear, month);
        }

        // Apply sort
        String sortValue = sortBox.getValue();
        String sortOption = "date_desc"; // Default
        
        if (sortValue != null) {
            if (sortValue.equals("Date (Newest First)")) {
                sortOption = "date_desc";
            } else if (sortValue.equals("Date (Oldest First)")) {
                sortOption = "date_asc";
            } else if (sortValue.equals("Amount (High to Low)")) {
                sortOption = "amount_desc";
            } else if (sortValue.equals("Amount (Low to High)")) {
                sortOption = "amount_asc";
            }
        }
        
        filtered = historyService.sortBy(filtered, sortOption);
        
        // Update state and UI
        currentTransactions = filtered;
        displayTransactions(currentTransactions);
        showMessage("Filters applied", true);
    }

    @FXML
    private void handleReset(ActionEvent event) {
        monthFilterBox.setValue("All");
        sortBox.setValue("Date (Newest First)");
        loadAllTransactions();
        messageLabel.setText("");
    }

    @FXML
    private void handleExport(ActionEvent event) {
        if (currentTransactions == null || currentTransactions.isEmpty()) {
            showMessage("No transactions to export", false);
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Transactions CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        fileChooser.setInitialFileName("transactions_export.csv");

        Stage stage = (Stage) transactionList.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            boolean success = CSVExporter.exportTransactions(file.getAbsolutePath(), currentTransactions);
            if (success) {
                showMessage("Exported successfully to " + file.getName(), true);
            } else {
                showMessage("Failed to export", false);
            }
        }
    }

    private void displayTransactions(List<Transaction> transactions) {
        ObservableList<String> items = FXCollections.observableArrayList();

        for (Transaction transaction : transactions) {
            String line = String.format("%s  |  %-6s  |  %-12s  |  %.2f  |  %s",
                    transaction.getDate(),
                    transaction.getType(),
                    transaction.getCategory(),
                    transaction.getAmount(),
                    transaction.getDescription());
            items.add(line);
        }

        transactionList.setItems(items);
    }
    
    private void showMessage(String text, boolean success) {
        messageLabel.setStyle(success ? "-fx-text-fill: green;" : "-fx-text-fill: red;");
        messageLabel.setText(text);
    }
}
