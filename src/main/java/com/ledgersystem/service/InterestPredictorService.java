package com.ledgersystem.service;

import com.ledgersystem.model.Bank;
import com.ledgersystem.repository.BankRepository;

import java.util.ArrayList;
import java.util.List;

public class InterestPredictorService {
    private final BankRepository bankRepository;

    public InterestPredictorService(BankRepository bankRepository) {
        this.bankRepository = bankRepository;
    }

    // Returns one formatted result string per bank showing the predicted interest earned.
    // Formula (simple interest): interest = principal x rate/100 x months/12
    public List<String> predict(double principal, int months) {
        if (principal <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }

        if (months <= 0) {
            throw new IllegalArgumentException("Duration must be at least 1 month");
        }

        List<Bank> banks = bankRepository.findAll();
        List<String> results = new ArrayList<>();

        for (Bank bank : banks) {
            double interest = principal * bank.getInterestRate() / 100.0 * months / 12.0;
            double total = principal + interest;

            String line = String.format("%-20s  |  Rate: %.2f%%  |  Interest: %.2f  |  Total: %.2f",
                    bank.getBankName(), bank.getInterestRate(), interest, total);
            results.add(line);
        }

        return results;
    }
}
