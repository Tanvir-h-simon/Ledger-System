package com.ledgersystem.repository.csv;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CSVManager {
    public List<String[]> readRows(String filePath) {
        List<String[]> rows = new ArrayList<>();
        File file = new File(filePath);

        // Returns an empty list if the file doesn't exist yet.
        if (!file.exists()) {
            return rows;
        }

        // Reads all data rows from a CSV file, skipping the header row.
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine(); // skip header
            line = reader.readLine();

            while (line != null) {
                if (!line.isBlank()) {
                    rows.add(parseCsvLine(line));
                }
                line = reader.readLine();
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + filePath);
        }

        return rows;
    }

    // Safely parses a CSV line, respecting quotes so commas inside descriptions don't break columns
    private String[] parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder currentToken = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '\"') {
                inQuotes = !inQuotes; // Toggle state
            } else if (c == ',' && !inQuotes) {
                result.add(currentToken.toString());
                currentToken.setLength(0); // Clear builder
            } else {
                currentToken.append(c);
            }
        }
        result.add(currentToken.toString());
        return result.toArray(new String[0]);
    }

    // Writes a header line followed by all rows, overwriting the file's previous content.
    public void writeRows(String filePath, String header, List<String[]> rows) {
        try {
            File file = new File(filePath);
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(header);
                writer.newLine();

                for (String[] row : rows) {
                    // Escape each field before joining
                    for (int i = 0; i < row.length; i++) {
                        row[i] = escapeCsvField(row[i]);
                    }
                    writer.write(String.join(",", row));
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Error writing file: " + filePath);
        }
    }

    // Wraps the field in quotes if it contains a comma or quote
    private String escapeCsvField(String field) {
        if (field == null) return "";
        if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            // Escape existing quotes by doubling them
            field = field.replace("\"", "\"\"");
            return "\"" + field + "\"";
        }
        return field;
    }
}