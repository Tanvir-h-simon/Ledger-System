package com.ledgersystem.repository.csv;

import com.ledgersystem.model.Bank;
import com.ledgersystem.repository.BankRepository;

import java.util.ArrayList;
import java.util.List;

public class CsvBankRepository implements BankRepository {
    private static final String FILE_PATH = "data/banks.csv";

    private final CSVManager csvManager = new CSVManager();

    @Override
    public List<Bank> findAll() {
        List<Bank> banks = new ArrayList<>();
        List<String[]> rows = csvManager.readRows(FILE_PATH);

        for (String[] row : rows) {
            banks.add(toBank(row));
        }

        return banks;
    }

    @Override
    public Bank findById(int bankId) {
        for (Bank bank : findAll()) {
            if (bank.getBankId() == bankId) {
                return bank;
            }
        }
        return null;
    }

    private Bank toBank(String[] row) {
        int bankId = Integer.parseInt(row[0]);
        String bankName = row[1];
        double interestRate = Double.parseDouble(row[2]);

        return new Bank(bankId, bankName, interestRate);
    }
}