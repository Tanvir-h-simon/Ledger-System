package com.ledgersystem.repository;

import com.ledgersystem.model.Savings;

public interface SavingsRepository {
    Savings findByUserId(int userId);
    void save(Savings savings);
    int getNextId();
}