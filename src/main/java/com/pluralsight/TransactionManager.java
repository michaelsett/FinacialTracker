package com.pluralsight;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class TransactionManager {
    private static final String FILE_NAME = "transactions.csv";

    public static List<Transaction> loadTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                LocalDate date = LocalDate.parse(parts[0]);
                LocalTime time = LocalTime.parse(parts[1]);
                String description = parts[2];
                String vendor = parts[3];
                double amount = Double.parseDouble(parts[4]);
                transactions.add(new Transaction(date, time, description, vendor, amount));
            }
        } catch (IOException e) {
            System.out.println("No existing transaction file found. Starting fresh.");
        }
        return transactions;
    }

    public static void saveTransaction(Transaction transaction) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
            writer.write(transaction.getDate() + "|" + transaction.getTime() + "|" +
                    transaction.getDescription() + "|" + transaction.getVendor() + "|" +
                    transaction.getAmount());
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error writing transaction to file: " + e.getMessage());
        }
    }
}