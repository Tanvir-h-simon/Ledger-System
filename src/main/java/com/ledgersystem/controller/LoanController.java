package com.ledgersystem.controller;

import com.ledgersystem.model.Bank;
import com.ledgersystem.model.Loan;
import com.ledgersystem.repository.csv.CsvBankRepository;
import com.ledgersystem.repository.csv.CsvLoanRepository;
import com.ledgersystem.service.LoanService;
import com.ledgersystem.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.List;

public class LoanController {

    @FXML
    private Label loanStatusLabel;

    @FXML
    private Label loanBankLabel;

    @FXML
    private Label loanPrincipalLabel;

    @FXML
    private Label loanBalanceLabel;

    @FXML
    private Label loanDueDateLabel;

    @FXML
    private ComboBox<String> bankBox;

    @FXML
    private TextField principalField;

    @FXML
    private TextField monthsField;

    @FXML
    private TextField repayAmountField;

    @FXML
    private Label messageLabel;

    private final LoanService loanService =
            new LoanService(new CsvLoanRepository(), new CsvBankRepository());

    // Keeps the loaded banks so we can look up the bankId by name when the user picks one
    private List<Bank> banks;

    @FXML
    public void initialize() {
        loadBankDropdown();
        refreshLoanInfo();
    }

    // Populates the bank ComboBox with "BankName (X.XX%)" entries
    private void loadBankDropdown() {
        banks = loanService.getAllBanks();

        ObservableList<String> bankNames = FXCollections.observableArrayList();
        for (Bank bank : banks) {
            bankNames.add(bank.getBankName() + " (" + bank.getInterestRate() + "%)");
        }

        bankBox.setItems(bankNames);
    }

    @FXML
    private void handleApplyLoan(ActionEvent event) {
        // Make sure the user has selected a bank
        int selectedIndex = bankBox.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            showMessage("Please select a bank", false);
            return;
        }

        double principal;
        try {
            principal = Double.parseDouble(principalField.getText().trim());
        } catch (NumberFormatException e) {
            showMessage("Enter a valid principal amount", false);
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
            int userId = SessionManager.getCurrentUser().getUserId();
            int bankId = banks.get(selectedIndex).getBankId();
            loanService.applyLoan(userId, bankId, principal, months);
            refreshLoanInfo();
            clearApplyForm();
            showMessage("Loan approved successfully", true);
        } catch (IllegalArgumentException e) {
            showMessage(e.getMessage(), false);
        }
    }

    @FXML
    private void handleRepay(ActionEvent event) {
        double amount;
        try {
            amount = Double.parseDouble(repayAmountField.getText().trim());
        } catch (NumberFormatException e) {
            showMessage("Enter a valid repayment amount", false);
            return;
        }

        try {
            int userId = SessionManager.getCurrentUser().getUserId();
            loanService.repayLoan(userId, amount);
            refreshLoanInfo();
            repayAmountField.clear();
            showMessage("Repayment recorded", true);
        } catch (IllegalArgumentException e) {
            showMessage(e.getMessage(), false);
        }
    }

    // Reloads the loan summary labels from the service
    private void refreshLoanInfo() {
        int userId = SessionManager.getCurrentUser().getUserId();
        Loan loan = loanService.getLoan(userId);

        if (loan == null) {
            loanStatusLabel.setText("Status: No loan");
            loanBankLabel.setText("");
            loanPrincipalLabel.setText("");
            loanBalanceLabel.setText("");
            loanDueDateLabel.setText("");
            return;
        }

        loanStatusLabel.setText("Status: " + loan.getStatus());
        loanPrincipalLabel.setText(String.format("Principal: %.2f", loan.getPrincipalAmount()));
        loanBalanceLabel.setText(String.format("Outstanding balance: %.2f", loan.getOutstandingBalance()));

        // Look up the bank name using the interest rate stored on the loan
        loanBankLabel.setText(String.format("Interest rate: %.2f%%", loan.getInterestRate()));

        String dueDate = loan.getCreatedAt().plusMonths(loan.getRepaymentPeriodMonths()).toString();
        loanDueDateLabel.setText("Due date: " + dueDate);
    }

    private void clearApplyForm() {
        bankBox.getSelectionModel().clearSelection();
        principalField.clear();
        monthsField.clear();
    }

    private void showMessage(String text, boolean success) {
        messageLabel.setStyle(success ? "-fx-text-fill: green;" : "-fx-text-fill: red;");
        messageLabel.setText(text);
    }
}
