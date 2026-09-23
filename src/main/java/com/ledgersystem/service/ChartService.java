package com.ledgersystem.service;

import com.ledgersystem.model.Loan;
import com.ledgersystem.model.Transaction;
import com.ledgersystem.repository.LoanRepository;
import com.ledgersystem.repository.TransactionRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ChartService {
    private final TransactionRepository transactionRepository;
    private final LoanRepository loanRepository;

    public ChartService(TransactionRepository transactionRepository, LoanRepository loanRepository) {
        this.transactionRepository = transactionRepository;
        this.loanRepository = loanRepository;
    }

    // Returns total spending per category (credits only).
    // Used for the pie chart: "Spending Distribution by Category"
    public Map<String, Double> getCategoryTotals(int userId) {
        List<Transaction> transactions = transactionRepository.findByUserId(userId);

        // LinkedHashMap keeps the categories in the order they first appeared
        Map<String, Double> totals = new LinkedHashMap<>();

        for (Transaction t : transactions) {
            if (t.getType().equals("credit")) {
                String category = t.getCategory();
                double current = 0;

                if (totals.containsKey(category)) {
                    current = totals.get(category);
                }

                totals.put(category, current + t.getAmount());
            }
        }

        return totals;
    }

    // Returns the running balance after each month, sorted oldest to newest.
    // Used for the line chart: "Balance Over Time"
    // Key = "YYYY-MM", Value = balance at the end of that month
    public Map<String, Double> getMonthlyBalances(int userId) {
        List<Transaction> transactions = new ArrayList<>(transactionRepository.findByUserId(userId));

        // Sort transactions oldest-first so the running balance is calculated in order
        Collections.sort(transactions, new Comparator<Transaction>() {
            @Override
            public int compare(Transaction a, Transaction b) {
                return a.getDate().compareTo(b.getDate());
            }
        });

        // LinkedHashMap preserves insertion order, so the chart months stay in chronological order
        Map<String, Double> monthlyBalances = new LinkedHashMap<>();
        double running = 0;

        for (Transaction t : transactions) {
            if (t.getType().equals("debit")) {
                running += t.getAmount();
            } else {
                running -= t.getAmount();
            }

            // Build the month key, e.g. "2026-09"
            String month = t.getDate().getYear() + "-"
                    + String.format("%02d", t.getDate().getMonthValue());

            // Overwriting the same month key means we end up with the balance at the END of each month
            monthlyBalances.put(month, running);
        }

        return monthlyBalances;
    }

    // Returns {principal, outstandingBalance} for the user's current loan.
    // Returns null if the user has no loan at all.
    // Used for the bar chart: "Loan Repayment Progress"
    public double[] getLoanProgress(int userId) {
        Loan loan = loanRepository.findByUserId(userId);

        if (loan == null) {
            return null;
        }

        return new double[]{loan.getPrincipalAmount(), loan.getOutstandingBalance()};
    }
}
