package com.ledgersystem.repository.csv;

import com.ledgersystem.model.Loan;
import com.ledgersystem.repository.LoanRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CsvLoanRepository implements LoanRepository {
    private static final String FILE_PATH = "data/loans.csv";
    private static final String HEADER = "loan_id,user_id,principal_amount,interest_rate,repayment_period_months,outstanding_balance,status,created_at";

    private final CSVManager csvManager = new CSVManager();

    @Override
    public Loan findByUserId(int userId) {
        for (Loan loan : findAll()) {
            if (loan.getUserId() == userId) {
                return loan;
            }
        }
        return null;
    }

    @Override
    public void save(Loan loan) {
        List<Loan> all = findAll();
        boolean updated = false;

        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getLoanId() == loan.getLoanId()) {
                all.set(i, loan);
                updated = true;
                break;
            }
        }

        if (!updated) {
            all.add(loan);
        }

        writeAll(all);
    }

    @Override
    public int getNextId() {
        int maxId = 0;
        for (Loan loan : findAll()) {
            if (loan.getLoanId() > maxId) {
                maxId = loan.getLoanId();
            }
        }
        return maxId + 1;
    }

    private List<Loan> findAll() {
        List<Loan> all = new ArrayList<>();
        List<String[]> rows = csvManager.readRows(FILE_PATH);

        for (String[] row : rows) {
            all.add(toLoan(row));
        }

        return all;
    }

    private Loan toLoan(String[] row) {
        int loanId = Integer.parseInt(row[0]);
        int userId = Integer.parseInt(row[1]);
        double principalAmount = Double.parseDouble(row[2]);
        double interestRate = Double.parseDouble(row[3]);
        int repaymentPeriodMonths = Integer.parseInt(row[4]);
        double outstandingBalance = Double.parseDouble(row[5]);
        String status = row[6];
        LocalDate createdAt = LocalDate.parse(row[7]);

        return new Loan(loanId, userId, principalAmount, interestRate,
                repaymentPeriodMonths, outstandingBalance, status, createdAt);
    }

    private String[] toRow(Loan loan) {
        return new String[] {
                String.valueOf(loan.getLoanId()),
                String.valueOf(loan.getUserId()),
                String.valueOf(loan.getPrincipalAmount()),
                String.valueOf(loan.getInterestRate()),
                String.valueOf(loan.getRepaymentPeriodMonths()),
                String.valueOf(loan.getOutstandingBalance()),
                loan.getStatus(),
                loan.getCreatedAt().toString()
        };
    }

    private void writeAll(List<Loan> all) {
        List<String[]> rows = new ArrayList<>();
        for (Loan loan : all) {
            rows.add(toRow(loan));
        }
        csvManager.writeRows(FILE_PATH, HEADER, rows);
    }
}