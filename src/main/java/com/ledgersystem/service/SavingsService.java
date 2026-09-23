package com.ledgersystem.service;

import com.ledgersystem.model.Savings;
import com.ledgersystem.model.Transaction;
import com.ledgersystem.repository.SavingsRepository;
import com.ledgersystem.repository.TransactionRepository;

import java.util.List;

public class SavingsService {
    private final SavingsRepository savingsRepository;
    private final TransactionRepository transactionRepository;

    public SavingsService(SavingsRepository savingsRepository, TransactionRepository transactionRepository) {
        this.savingsRepository = savingsRepository;
        this.transactionRepository = transactionRepository;
    }

    // Returns the savings record for this user, or null if they have never set one up
    public Savings getSavings(int userId) {
        return savingsRepository.findByUserId(userId);
    }

    // Enables savings at the given percentage. Creates the record if it does not exist yet.
    public void activate(int userId, int percentage) {
        if (percentage < 1 || percentage > 100) {
            throw new IllegalArgumentException("Percentage must be between 1 and 100");
        }

        Savings existing = savingsRepository.findByUserId(userId);

        if (existing == null) {
            // First time setting up savings for this user
            int id = savingsRepository.getNextId();
            Savings newSavings = new Savings(id, userId, true, percentage);
            savingsRepository.save(newSavings);
        } else {
            // Update the existing record
            existing.setActive(true);
            existing.setPercentage(percentage);
            savingsRepository.save(existing);
        }
    }

    // Disables savings. The percentage is kept so the user does not have to re-enter it later.
    public void deactivate(int userId) {
        Savings existing = savingsRepository.findByUserId(userId);

        if (existing == null) {
            throw new IllegalArgumentException("No savings record found");
        }

        existing.setActive(false);
        savingsRepository.save(existing);
    }

    // Calculates total amount saved by summing all credits in the "Savings" category
    public double getTotalSaved(int userId) {
        List<Transaction> transactions = transactionRepository.findByUserId(userId);
        double total = 0;

        for (Transaction t : transactions) {
            if (t.getCategory().equals("Savings")) {
                total += t.getAmount();
            }
        }

        return total;
    }
}
