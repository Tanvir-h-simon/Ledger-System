package com.ledgersystem.service;

import com.ledgersystem.model.User;
import com.ledgersystem.repository.UserRepository;
import com.ledgersystem.util.PasswordHasher;
import com.ledgersystem.util.SessionManager;
import com.ledgersystem.util.Validator;

public class AuthService {
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Registers a new user. Throws IllegalArgumentException if any check fails.
    public User register(String name, String email, String password, String confirmPassword) {
        if (!Validator.isValidName(name)) {
            throw new IllegalArgumentException("Name must contain only letters, numbers, and spaces");
        }
        if (!Validator.isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (!Validator.isValidPassword(password)) {
            throw new IllegalArgumentException(
                    "Password must be at least 8 characters and include a letter, a digit, and a special character");
        }
        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        if (userRepository.findByEmail(email) != null) {
            throw new IllegalArgumentException("An account with this email already exists");
        }

        String passwordHash = PasswordHasher.hash(password);
        int userId = userRepository.getNextId();
        User user = new User(userId, name, email, passwordHash);

        userRepository.save(user);
        return user;
    }

    // Logs a user in. Returns the User on success, null if the email or password is wrong.
    public User login(String email, String password) {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            return null;
        }

        if (!PasswordHasher.verify(password, user.getHashedPassword())) {
            return null;
        }

        SessionManager.setCurrentUser(user);
        return user;
    }

    public void logout() {
        SessionManager.clear();
    }
}