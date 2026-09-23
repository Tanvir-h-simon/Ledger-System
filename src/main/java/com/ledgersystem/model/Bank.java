package com.ledgersystem.model;

public class Bank {
    private int bankId;
    private String bankName;
    private double interestRate;

    public Bank(int bankId, String bankName, double interestRate) {
        this.bankId = bankId;
        this.bankName = bankName;
        this.interestRate = interestRate;
    }

    // ----- Getters -----
    public int getBankId() {
        return bankId;
    }

    public String getBankName() {
        return bankName;
    }

    public double getInterestRate() {
        return interestRate;
    }

    // ----- No setters: banks are fixed reference data, seeded once and not edited by users -----
}