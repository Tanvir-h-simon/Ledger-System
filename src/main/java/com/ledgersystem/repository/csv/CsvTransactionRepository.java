package com.ledgersystem.repository.csv;

import com.ledgersystem.model.Transaction;
import com.ledgersystem.repository.TransactionRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CsvTransactionRepository implements TransactionRepository {
    private static final String FILE_PATH = "data/transactions.csv";
    private static final String HEADER = "transaction_id,user_id,type,amount,category,description,date";

    private final CSVManager csvManager = new CSVManager();

    @Override
    public List<Transaction> findAll() {
        List<Transaction> transactions = new ArrayList<>();
        List<String[]> rows = csvManager.readRows(FILE_PATH);

        for (String[] row : rows) {
            transactions.add(toTransaction(row));
        }

        return transactions;
    }

    @Override
    public List<Transaction> findByUserId(int userId) {
        List<Transaction> result = new ArrayList<>();

        for (Transaction transaction : findAll()) {
            if (transaction.getUserId() == userId) {
                result.add(transaction);
            }
        }

        return result;
    }

    @Override
    public void save(Transaction transaction) {
        // Transactions are never edited, only ever added
        List<Transaction> transactions = findAll();
        transactions.add(transaction);

        writeAll(transactions);
    }

    @Override
    public int getNextId() {
        int maxId = 0;
        for (Transaction transaction : findAll()) {
            if (transaction.getTransactionId() > maxId) {
                maxId = transaction.getTransactionId();
            }
        }
        return maxId + 1;
    }

    private Transaction toTransaction(String[] row) {
        int transactionId = Integer.parseInt(row[0]);
        int userId = Integer.parseInt(row[1]);
        String type = row[2];
        double amount = Double.parseDouble(row[3]);
        String category = row[4];
        String description = row[5];
        LocalDate date = LocalDate.parse(row[6]);

        return new Transaction(transactionId, userId, type, amount, category, description, date);
    }

    private String[] toRow(Transaction transaction) {
        return new String[] {
                String.valueOf(transaction.getTransactionId()),
                String.valueOf(transaction.getUserId()),
                transaction.getType(),
                String.valueOf(transaction.getAmount()),
                transaction.getCategory(),
                transaction.getDescription(),
                transaction.getDate().toString()
        };
    }

    private void writeAll(List<Transaction> transactions) {
        List<String[]> rows = new ArrayList<>();
        for (Transaction transaction : transactions) {
            rows.add(toRow(transaction));
        }
        csvManager.writeRows(FILE_PATH, HEADER, rows);
    }
}