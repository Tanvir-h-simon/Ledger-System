package com.ledgersystem.repository;

import com.ledgersystem.model.User;
import java.util.List;

public interface UserRepository {
    List<User> findAll();
    User findById(int userId);
    User findByEmail(String email);
    void save(User user);
    int getNextId();
}