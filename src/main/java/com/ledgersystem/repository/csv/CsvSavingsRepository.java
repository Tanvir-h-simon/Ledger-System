package com.ledgersystem.repository.csv;

import com.ledgersystem.model.Savings;
import com.ledgersystem.repository.SavingsRepository;

import java.util.ArrayList;
import java.util.List;

public class CsvSavingsRepository implements SavingsRepository {
    private static final String FILE_PATH = "data/savings.csv";
    private static final String HEADER = "savings_id,user_id,active,percentage";

    private final CSVManager csvManager = new CSVManager();

    @Override
    public Savings findByUserId(int userId) {
        for (Savings savings : findAll()) {
            if (savings.getUserId() == userId) {
                return savings;
            }
        }
        return null;
    }

    @Override
    public void save(Savings savings) {
        List<Savings> all = findAll();
        boolean updated = false;

        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getUserId() == savings.getUserId()) {
                all.set(i, savings);
                updated = true;
                break;
            }
        }

        if (!updated) {
            all.add(savings);
        }

        writeAll(all);
    }

    @Override
    public int getNextId() {
        int maxId = 0;
        for (Savings savings : findAll()) {
            if (savings.getSavingsId() > maxId) {
                maxId = savings.getSavingsId();
            }
        }
        return maxId + 1;
    }

    private List<Savings> findAll() {
        List<Savings> all = new ArrayList<>();
        List<String[]> rows = csvManager.readRows(FILE_PATH);

        for (String[] row : rows) {
            all.add(toSavings(row));
        }

        return all;
    }

    private Savings toSavings(String[] row) {
        int savingsId = Integer.parseInt(row[0]);
        int userId = Integer.parseInt(row[1]);
        boolean active = Boolean.parseBoolean(row[2]);
        int percentage = Integer.parseInt(row[3]);

        return new Savings(savingsId, userId, active, percentage);
    }

    private String[] toRow(Savings savings) {
        return new String[] {
                String.valueOf(savings.getSavingsId()),
                String.valueOf(savings.getUserId()),
                String.valueOf(savings.isActive()),
                String.valueOf(savings.getPercentage())
        };
    }

    private void writeAll(List<Savings> all) {
        List<String[]> rows = new ArrayList<>();
        for (Savings savings : all) {
            rows.add(toRow(savings));
        }
        csvManager.writeRows(FILE_PATH, HEADER, rows);
    }
}