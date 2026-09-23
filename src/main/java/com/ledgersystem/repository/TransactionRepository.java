package com.ledgersystem.repository;

import com.ledgersystem.model.Transaction;
import java.util.List;

public interface TransactionRepository {
    List<Transaction> findAll();
    List<Transaction> findByUserId(int userId);
    void save(Transaction transaction);
    int getNextId();
}