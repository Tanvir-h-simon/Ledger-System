package com.ledgersystem.model;

public class Savings {
    private int savingsId;
    private int userId;
    private boolean active;
    private int savingsPercentage;

    public Savings(int savingsId, int userId, boolean active, int savingsPercentage) {
        if (savingsPercentage < 0 || savingsPercentage > 100) {
            throw new IllegalArgumentException("Percentage must be between 0 and 100");
        }

        this.savingsId = savingsId;
        this.userId = userId;
        this.active = active;
        this.savingsPercentage = savingsPercentage;
    }

    // ----- Getters -----
    public int getSavingsId() {
        return savingsId;
    }

    public int getUserId() {
        return userId;
    }

    public boolean isActive() {
        return active;
    }

    public int getPercentage() {
        return savingsPercentage;
    }

    // ----- Setters: savings settings can be changed after creation -----
    public void setActive(boolean active) {
        this.active = active;
    }

    public void setPercentage(int percentage) {
        this.savingsPercentage = percentage;
    }
}