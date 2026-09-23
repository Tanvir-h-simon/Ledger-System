package com.ledgersystem.repository.csv;

import com.ledgersystem.model.User;
import com.ledgersystem.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

public class CsvUserRepository implements UserRepository {
    private static final String FILE_PATH = "data/users.csv";
    private static final String HEADER = "user_id,name,email,password_hash";

    private final CSVManager csvManager = new CSVManager();

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        List<String[]> rows = csvManager.readRows(FILE_PATH);

        for (String[] row : rows) {
            users.add(toUser(row));
        }

        return users;
    }

    @Override
    public User findById(int userId) {
        for (User user : findAll()) {
            if (user.getUserId() == userId) {
                return user;
            }
        }
        return null;
    }

    @Override
    public User findByEmail(String email) {
        for (User user : findAll()) {
            if (user.getEmail().equalsIgnoreCase(email)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public void save(User user) {
        List<User> users = findAll();
        boolean updated = false;

        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUserId() == user.getUserId()) {
                users.set(i, user);
                updated = true;
                break;
            }
        }

        if (!updated) {
            users.add(user);
        }

        writeAll(users);
    }

    @Override
    public int getNextId() {
        int maxId = 0;
        for (User user : findAll()) {
            if (user.getUserId() > maxId) {
                maxId = user.getUserId();
            }
        }
        return maxId + 1;
    }

    // Converts one CSV row into a User object
    private User toUser(String[] row) {
        int userId = Integer.parseInt(row[0]);
        String name = row[1];
        String email = row[2];
        String passwordHash = row[3];
        return new User(userId, name, email, passwordHash);
    }

    // Converts a User object into a CSV row
    private String[] toRow(User user) {
        return new String[] {
                String.valueOf(user.getUserId()),
                user.getName(),
                user.getEmail(),
                user.getHashedPassword()
        };
    }

    private void writeAll(List<User> users) {
        List<String[]> rows = new ArrayList<>();
        for (User user : users) {
            rows.add(toRow(user));
        }
        csvManager.writeRows(FILE_PATH, HEADER, rows);
    }
}