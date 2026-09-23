package com.ledgersystem.service;

import com.ledgersystem.model.Loan;
import com.ledgersystem.model.Savings;
import com.ledgersystem.model.Transaction;
import com.ledgersystem.repository.LoanRepository;
import com.ledgersystem.repository.SavingsRepository;
import com.ledgersystem.repository.TransactionRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LedgerService {
    private final TransactionRepository transactionRepository;
    private final LoanRepository loanRepository;
    private final SavingsRepository savingsRepository;

    public LedgerService(TransactionRepository transactionRepository, LoanRepository loanRepository,
                         SavingsRepository savingsRepository) {
        this.transactionRepository = transactionRepository;
        this.loanRepository = loanRepository;
        this.savingsRepository = savingsRepository;
    }

    // Records a debit (money in) for the given user
    public void recordDebit(int userId, double amount, String category, String description) {
        checkLoanNotBlocking(userId);

        int transactionId = transactionRepository.getNextId();
        Transaction transaction = new Transaction(transactionId, userId, "debit", amount, category, description, LocalDate.now());
        transactionRepository.save(transaction);

        // If savings is active, automatically set aside the savings portion as a credit
        autoDeductSavings(userId, amount);
    }

    // Records a credit (money out) for the given user
    public void recordCredit(int userId, double amount, String category, String description) {
        checkLoanNotBlocking(userId);

        int transactionId = transactionRepository.getNextId();
        Transaction transaction = new Transaction(transactionId, userId, "credit", amount, category, description, LocalDate.now());
        transactionRepository.save(transaction);
    }

    // Balance is the sum of debits minus the sum of credits
    public double getBalance(int userId) {
        List<Transaction> transactions = transactionRepository.findByUserId(userId);
        double balance = 0.0;

        for (Transaction transaction : transactions) {
            if (transaction.getType().equals("debit")) {
                balance += transaction.getAmount();
            } else {
                balance -= transaction.getAmount();
            }
        }

        return balance;
    }

    // Returns the user's transactions, most recent first
    public List<Transaction> getTransactions(int userId) {
        List<Transaction> transactions = transactionRepository.findByUserId(userId);

        // Reverse the list so the newest transaction appears at the top
        List<Transaction> reversed = new ArrayList<>();
        for (int i = transactions.size() - 1; i >= 0; i--) {
            reversed.add(transactions.get(i));
        }

        return reversed;
    }

    // Blocks debit/credit if the user has an active loan past its repayment period
    private void checkLoanNotBlocking(int userId) {
        Loan loan = loanRepository.findByUserId(userId);

        if (loan == null || !loan.isActive()) {
            return;
        }

        LocalDate dueDate = loan.getCreatedAt().plusMonths(loan.getRepaymentPeriodMonths());

        if (LocalDate.now().isAfter(dueDate)) {
            throw new IllegalStateException("Loan repayment is overdue. Repay your loan before recording new transactions.");
        }
    }

    // If the user has savings enabled, record a credit with category "Savings" for the saved portion
    private void autoDeductSavings(int userId, double debitAmount) {
        Savings savings = savingsRepository.findByUserId(userId);

        if (savings == null || !savings.isActive()) {
            return;
        }

        double savingsAmount = debitAmount * savings.getPercentage() / 100.0;
        String description = "Auto-savings (" + savings.getPercentage() + "% of debit)";

        int savingsTransactionId = transactionRepository.getNextId();
        Transaction savingsTransaction = new Transaction(
                savingsTransactionId, userId, "credit", savingsAmount, "Savings", description, LocalDate.now());
        transactionRepository.save(savingsTransaction);
    }
}