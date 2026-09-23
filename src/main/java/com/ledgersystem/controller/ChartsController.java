package com.ledgersystem.controller;

import com.ledgersystem.repository.csv.CsvLoanRepository;
import com.ledgersystem.repository.csv.CsvTransactionRepository;
import com.ledgersystem.service.ChartService;
import com.ledgersystem.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;

import java.util.Map;

public class ChartsController {

    @FXML
    private PieChart categoryChart;

    @FXML
    private LineChart<String, Number> balanceChart;

    @FXML
    private BarChart<String, Number> loanChart;

    private final ChartService chartService =
            new ChartService(new CsvTransactionRepository(), new CsvLoanRepository());

    // Called automatically after FXML fields are set. Populates all three charts.
    @FXML
    public void initialize() {
        int userId = SessionManager.getCurrentUser().getUserId();
        loadCategoryChart(userId);
        loadBalanceChart(userId);
        loadLoanChart(userId);
    }

    // ----- Pie chart: spending by category -----

    private void loadCategoryChart(int userId) {
        Map<String, Double> totals = chartService.getCategoryTotals(userId);

        // PieChart.Data takes a label and a value
        for (Map.Entry<String, Double> entry : totals.entrySet()) {
            PieChart.Data slice = new PieChart.Data(entry.getKey(), entry.getValue());
            categoryChart.getData().add(slice);
        }

        if (totals.isEmpty()) {
            categoryChart.setTitle("Spending by Category (no data yet)");
        }
    }

    // ----- Line chart: balance over time (one data point per month) -----

    private void loadBalanceChart(int userId) {
        Map<String, Double> monthlyBalances = chartService.getMonthlyBalances(userId);

        // XYChart.Series holds a named set of (x, y) data points
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Balance");

        for (Map.Entry<String, Double> entry : monthlyBalances.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        balanceChart.getData().add(series);

        if (monthlyBalances.isEmpty()) {
            balanceChart.setTitle("Balance Over Time (no data yet)");
        }
    }

    // ----- Bar chart: loan repayment progress -----

    private void loadLoanChart(int userId) {
        double[] progress = chartService.getLoanProgress(userId);

        if (progress == null) {
            loanChart.setTitle("Loan Repayment Progress (no loan)");
            return;
        }

        // One series with two bars: total repayment amount and remaining outstanding balance
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Loan");
        series.getData().add(new XYChart.Data<>("Total Repayment", progress[0]));
        series.getData().add(new XYChart.Data<>("Outstanding", progress[1]));

        loanChart.getData().add(series);
    }
}
