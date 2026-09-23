package com.ledgersystem.controller;

import com.ledgersystem.model.Savings;
import com.ledgersystem.model.Transaction;
import com.ledgersystem.repository.csv.CsvLoanRepository;
import com.ledgersystem.repository.csv.CsvSavingsRepository;
import com.ledgersystem.repository.csv.CsvTransactionRepository;
import com.ledgersystem.service.ChartService;
import com.ledgersystem.service.LedgerService;
import com.ledgersystem.service.SavingsService;
import com.ledgersystem.util.SessionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn.CellDataFeatures;

import java.util.List;
import java.util.Map;

public class HomeController {

    @FXML private Label welcomeLabel;
    @FXML private Label balanceLabel;
    @FXML private Label savingsLabel;
    @FXML private Label savingsPillLabel;
    @FXML private Label savingsInfoLabel;
    @FXML private PieChart spendingChart;

    @FXML private TableView<String[]> transactionTable;
    @FXML private TableColumn<String[], String> dateColumn;
    @FXML private TableColumn<String[], String> descColumn;
    @FXML private TableColumn<String[], String> amountColumn;
    @FXML private TableColumn<String[], String> typeColumn;

    private final LedgerService ledgerService =
            new LedgerService(new CsvTransactionRepository(), new CsvLoanRepository(), new CsvSavingsRepository());

    private final SavingsService savingsService =
            new SavingsService(new CsvSavingsRepository(), new CsvTransactionRepository());

    private final ChartService chartService =
            new ChartService(new CsvTransactionRepository(), new CsvLoanRepository());

    @FXML
    public void initialize() {
        int userId = SessionManager.getCurrentUser().getUserId();

        welcomeLabel.setText("Welcome, " + SessionManager.getCurrentUser().getName());

        loadBalanceCard(userId);
        loadSavingsCard(userId);
        loadSpendingChart(userId);
        loadRecentTransactions(userId);
    }

    // ----- Balance Card -----

    private void loadBalanceCard(int userId) {
        double balance = ledgerService.getBalance(userId);
        balanceLabel.setText(String.format("%.2f", balance));
    }

    // ----- Savings Card -----

    private void loadSavingsCard(int userId) {
        Savings savings = savingsService.getSavings(userId);
        double totalSaved = savingsService.getTotalSaved(userId);

        savingsLabel.setText(String.format("%.2f", totalSaved));

        if (savings == null || !savings.isActive()) {
            // Hide the green Active pill and show inactive state
            savingsPillLabel.setText("● Inactive");
            savingsPillLabel.setStyle(
                    "-fx-background-color: #fef2f2; -fx-text-fill: #ef4444;" +
                    "-fx-font-weight: bold; -fx-font-size: 11px;" +
                    "-fx-background-radius: 20; -fx-padding: 3 10;");
            savingsInfoLabel.setText("Savings not active");
            savingsLabel.setStyle("-fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: #a3abb5;");
        } else {
            savingsInfoLabel.setText("Auto-saving " + savings.getPercentage() + "% of each debit");
        }
    }

    // ----- Spending Pie Chart -----

    private void loadSpendingChart(int userId) {
        Map<String, Double> categoryTotals = chartService.getCategoryTotals(userId);

        if (categoryTotals.isEmpty()) {
            spendingChart.setTitle("No spending data yet");
            return;
        }

        // Build one PieChart.Data slice per spending category
        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            PieChart.Data slice = new PieChart.Data(entry.getKey(), entry.getValue());
            spendingChart.getData().add(slice);
        }
    }

    // ----- Recent Transactions TableView -----

    @SuppressWarnings("deprecation")
    private void loadRecentTransactions(int userId) {
        // Constrained resize: columns fill the full table width, no horizontal scrollbar
        transactionTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Wire up each column to the correct index of the String[] row
        dateColumn.setCellValueFactory(new Callback<CellDataFeatures<String[], String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(CellDataFeatures<String[], String> param) {
                return new SimpleStringProperty(param.getValue()[0]);
            }
        });

        descColumn.setCellValueFactory(new Callback<CellDataFeatures<String[], String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(CellDataFeatures<String[], String> param) {
                return new SimpleStringProperty(param.getValue()[1]);
            }
        });

        amountColumn.setCellValueFactory(new Callback<CellDataFeatures<String[], String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(CellDataFeatures<String[], String> param) {
                return new SimpleStringProperty(param.getValue()[2]);
            }
        });

        typeColumn.setCellValueFactory(new Callback<CellDataFeatures<String[], String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(CellDataFeatures<String[], String> param) {
                return new SimpleStringProperty(param.getValue()[3]);
            }
        });

        // Get all transactions (newest first) and take only the last 5
        List<Transaction> all = ledgerService.getTransactions(userId);

        ObservableList<String[]> rows = FXCollections.observableArrayList();

        int count = 0;
        for (Transaction t : all) {
            if (count >= 5) {
                break;
            }

            String[] row = {
                t.getDate().toString(),
                t.getDescription(),
                String.format("%.2f", t.getAmount()),
                t.getType()
            };
            rows.add(row);
            count++;
        }

        transactionTable.setItems(rows);
    }
}
