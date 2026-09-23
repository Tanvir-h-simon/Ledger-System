package com.ledgersystem.util;

import com.ledgersystem.model.Transaction;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CSVExporter {

    // Exports a list of transactions to a CSV file at the given absolute file path
    public static boolean exportTransactions(String filePath, List<Transaction> transactions) {
        File file = new File(filePath);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            // Write the header
            writer.write("date,type,category,amount,description");
            writer.newLine();

            // Write each transaction
            for (Transaction t : transactions) {
                String desc = t.getDescription();
                if (desc != null && (desc.contains(",") || desc.contains("\"") || desc.contains("\n"))) {
                    desc = "\"" + desc.replace("\"", "\"\"") + "\"";
                }

                String line = String.format("%s,%s,%s,%.2f,%s",
                        t.getDate(),
                        t.getType(),
                        t.getCategory(),
                        t.getAmount(),
                        desc);
                
                writer.write(line);
                writer.newLine();
            }
            return true;

        } catch (IOException e) {
            System.out.println("Failed to export to CSV: " + e.getMessage());
            return false;
        }
    }
}
