package com.ledgersystem.repository;

import com.ledgersystem.model.Bank;
import java.util.List;

public interface BankRepository {
    List<Bank> findAll();
    Bank findById(int bankId);
}