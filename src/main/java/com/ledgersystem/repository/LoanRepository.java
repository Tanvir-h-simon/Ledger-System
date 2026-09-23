package com.ledgersystem.repository;

import com.ledgersystem.model.Loan;

public interface LoanRepository {
    Loan findByUserId(int userId);
    void save(Loan loan);
    int getNextId();
}