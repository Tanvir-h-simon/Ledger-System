package com.ledgersystem.model;

import java.time.LocalDate;

public class Loan {
    private int loanId;
    private int userId;
    private double principalAmount;
    private double interestRate;
    private int repaymentPeriodMonths;
    private double outstandingBalance;
    private String status; // active or repaid
    private LocalDate createdAt;

    public Loan(int loanId, int userId, double principalAmount, double interestRate, int repaymentPeriodMonths,
                double outstandingBalance, String status, LocalDate createdAt) {
        if (principalAmount <= 0) {
            throw new IllegalArgumentException("Principal amount must be positive");
        }

        if (interestRate < 0) {
            throw new IllegalArgumentException("Interest rate cannot be negative");
        }

        if (repaymentPeriodMonths <= 0) {
            throw new IllegalArgumentException("Repayment period must be positive");
        }

        this.loanId = loanId;
        this.userId = userId;
        this.principalAmount = principalAmount;
        this.interestRate = interestRate;
        this.repaymentPeriodMonths = repaymentPeriodMonths;
        this.outstandingBalance = outstandingBalance;
        this.status = status;
        this.createdAt = createdAt;
    }

    // ----- Getters -----
    public int getLoanId() {
        return loanId;
    }

    public int getUserId() {
        return userId;
    }

    public double getPrincipalAmount() {
        return principalAmount;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public int getRepaymentPeriodMonths() {
        return repaymentPeriodMonths;
    }

    public double getOutstandingBalance() {
        return outstandingBalance;
    }

    public String getStatus() {
        return status;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    // ----- Setters: balance and status change as repayments happen -----
    public void setOutstandingBalance(double outstandingBalance) {
        this.outstandingBalance = outstandingBalance;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Loan Status
    public boolean isActive() {
        return status.equalsIgnoreCase("active") && outstandingBalance > 0;
    }
}