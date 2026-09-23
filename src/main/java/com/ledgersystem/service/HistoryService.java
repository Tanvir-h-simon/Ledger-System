package com.ledgersystem.service;

import com.ledgersystem.model.Transaction;
import com.ledgersystem.repository.TransactionRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HistoryService {
    private final TransactionRepository transactionRepository;

    public HistoryService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    // Returns all transactions for the user, newest first
    public List<Transaction> getAll(int userId) {
        List<Transaction> all = transactionRepository.findByUserId(userId);

        List<Transaction> reversed = new ArrayList<>();
        for (int i = all.size() - 1; i >= 0; i--) {
            reversed.add(all.get(i));
        }

        return reversed;
    }

    // Returns only transactions that fall within the given month and year
    public List<Transaction> filterByMonth(List<Transaction> transactions, int year, int month) {
        List<Transaction> result = new ArrayList<>();

        for (Transaction t : transactions) {
            if (t.getDate().getYear() == year && t.getDate().getMonthValue() == month) {
                result.add(t);
            }
        }

        return result;
    }

    // Sorts the list by the chosen option and returns the sorted copy.
    // The original list is not modified.
    public List<Transaction> sortBy(List<Transaction> transactions, String sortOption) {
        List<Transaction> sorted = new ArrayList<>(transactions);

        if (sortOption.equals("date_asc")) {
            Collections.sort(sorted, new Comparator<Transaction>() {
                @Override
                public int compare(Transaction a, Transaction b) {
                    return a.getDate().compareTo(b.getDate());
                }
            });

        } else if (sortOption.equals("date_desc")) {
            Collections.sort(sorted, new Comparator<Transaction>() {
                @Override
                public int compare(Transaction a, Transaction b) {
                    return b.getDate().compareTo(a.getDate());
                }
            });

        } else if (sortOption.equals("amount_asc")) {
            Collections.sort(sorted, new Comparator<Transaction>() {
                @Override
                public int compare(Transaction a, Transaction b) {
                    return Double.compare(a.getAmount(), b.getAmount());
                }
            });

        } else if (sortOption.equals("amount_desc")) {
            Collections.sort(sorted, new Comparator<Transaction>() {
                @Override
                public int compare(Transaction a, Transaction b) {
                    return Double.compare(b.getAmount(), a.getAmount());
                }
            });
        }

        return sorted;
    }
}
