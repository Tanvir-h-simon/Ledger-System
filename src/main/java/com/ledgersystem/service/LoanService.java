package com.ledgersystem.service;

import com.ledgersystem.model.Bank;
import com.ledgersystem.model.Loan;
import com.ledgersystem.repository.BankRepository;
import com.ledgersystem.repository.LoanRepository;

import java.time.LocalDate;
import java.util.List;

public class LoanService {
    private final LoanRepository loanRepository;
    private final BankRepository bankRepository;

    public LoanService(LoanRepository loanRepository, BankRepository bankRepository) {
        this.loanRepository = loanRepository;
        this.bankRepository = bankRepository;
    }

    // Returns all available banks (used to populate the bank dropdown)
    public List<Bank> getAllBanks() {
        return bankRepository.findAll();
    }

    // Returns the user's current loan, or null if they have none
    public Loan getLoan(int userId) {
        return loanRepository.findByUserId(userId);
    }

    // Creates a new loan for the user.
    // Throws IllegalArgumentException if they already have an active loan, or if input is invalid.
    public void applyLoan(int userId, int bankId, double principal, int months) {
        Loan existing = loanRepository.findByUserId(userId);

        if (existing != null && existing.isActive()) {
            throw new IllegalArgumentException("You already have an active loan. Repay it before applying for another.");
        }

        if (principal <= 0) {
            throw new IllegalArgumentException("Principal amount must be positive");
        }

        if (months <= 0) {
            throw new IllegalArgumentException("Repayment period must be at least 1 month");
        }

        Bank bank = bankRepository.findById(bankId);

        if (bank == null) {
            throw new IllegalArgumentException("Selected bank not found");
        }

        double interestRate = bank.getInterestRate();

        // Total repayment = principal + simple interest for the given period
        double totalRepayment = principal + (principal * interestRate / 100.0 * months / 12.0);

        int loanId = loanRepository.getNextId();
        Loan loan = new Loan(loanId, userId, principal, interestRate, months, totalRepayment, "active", LocalDate.now());
        loanRepository.save(loan);
    }

    // Reduces the outstanding balance by the given amount.
    // If the amount covers the full balance, the loan is marked as repaid.
    public void repayLoan(int userId, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Repayment amount must be positive");
        }

        Loan loan = loanRepository.findByUserId(userId);

        if (loan == null || !loan.isActive()) {
            throw new IllegalArgumentException("No active loan found to repay");
        }

        double newBalance = loan.getOutstandingBalance() - amount;

        if (newBalance <= 0) {
            // Fully paid off
            loan.setOutstandingBalance(0);
            loan.setStatus("repaid");
        } else {
            loan.setOutstandingBalance(newBalance);
        }

        loanRepository.save(loan);
    }

    // Checks if the user's loan is overdue (past its due date and still active)
    public boolean isLoanOverdue(int userId) {
        Loan loan = loanRepository.findByUserId(userId);

        if (loan == null || !loan.isActive()) {
            return false;
        }

        LocalDate dueDate = loan.getCreatedAt().plusMonths(loan.getRepaymentPeriodMonths());
        return LocalDate.now().isAfter(dueDate);
    }
}
