package com.ledgersystem.model;

import java.time.LocalDate;

public class Transaction {
    private int transactionId;
    private int userId;
    private String transactionType; // debit or credit
    private double amount;
    private String category;
    private String description;
    private LocalDate date;

    public Transaction(int transactionId, int userId, String transactionType, double amount,
                       String category, String description, LocalDate date) {
        if (!transactionType.equalsIgnoreCase("debit") && !transactionType.equalsIgnoreCase("credit")) {
            throw new IllegalArgumentException("Type must be debit or credit");
        }

        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Description is required");
        }

        if (description.length() > 100) {
            throw new IllegalArgumentException("Description cannot exceed 100 characters");
        }

        this.transactionId = transactionId;
        this.userId = userId;
        this.transactionType = transactionType.toLowerCase();
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.date = date;
    }

    // ----- Getters -----
    public int getTransactionId() {
        return transactionId;
    }

    public int getUserId() {
        return userId;
    }

    public String getType() {
        return transactionType;
    }

    public double getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDate() {
        return date;
    }

    // ----- No setters: once a transaction is recorded it should not change -----
}