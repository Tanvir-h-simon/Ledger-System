package com.ledgersystem.model;

public class User {
    private final int userId;
    private final String name;
    private String email;
    private String hashedPassword;

    public User(int userId, String name, String email, String hashedPassword) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.hashedPassword = hashedPassword;
    }

    // ----- Getters -----
    public int getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    // ----- Setter: Change email and password -----
    public void changeEmail(String newEmail) {
        this.email = newEmail;
    }

    public void changePassword(String newHashedPassword) {
        this.hashedPassword = newHashedPassword;
    }
}