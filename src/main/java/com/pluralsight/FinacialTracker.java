package com.pluralsight;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

public class FinacialTracker {

    private static ArrayList<Transaction> transactions = new ArrayList<>();
    private static final String FILE_NAME = "transactions.csv";
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String TIME_FORMAT = "HH:mm:ss";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(TIME_FORMAT);

    public static void main(String[] args) {
        loadTransactions(FILE_NAME);
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("Welcome to TransactionApp");
            System.out.println("Choose an option:");
            System.out.println("D) Add Deposit");
            System.out.println("P) Make Payment (Debit)");
            System.out.println("L) Ledger");
            System.out.println("X) Exit");

            String input = scanner.nextLine().trim();

            switch (input.toUpperCase()) {
                case "D":
                    addDeposit(scanner);
                    break;
                case "P":
                    addPayment(scanner);
                    break;
                case "L":
                    ledgerMenu(scanner);
                    break;
                case "X":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option");
                    break;
            }
        }

        scanner.close();
    }

    public static void loadTransactions(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.out.println("Error creating file: " + e.getMessage());
                return;
            }
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length != 5) continue;
                LocalDate date = LocalDate.parse(parts[0], DATE_FORMATTER);
                LocalTime time = LocalTime.parse(parts[1], TIME_FORMATTER);
                String description = parts[2];
                String vendor = parts[3];
                double amount = Double.parseDouble(parts[4]);
                transactions.add(new Transaction(date, time, description, vendor, amount));
            }
        } catch (IOException e) {
            System.out.println("Error reading transactions: " + e.getMessage());
        }
    }

    private static void addDeposit(Scanner scanner) {
        try {
            System.out.println("Enter date and time (yyyy-MM-dd HH:mm:ss):");
            String dateTimeStr = scanner.nextLine();
            String[] dt = dateTimeStr.split(" ");
            LocalDate date = LocalDate.parse(dt[0], DATE_FORMATTER);
            LocalTime time = LocalTime.parse(dt[1], TIME_FORMATTER);

            System.out.println("Enter description:");
            String description = scanner.nextLine();

            System.out.println("Enter vendor:");
            String vendor = scanner.nextLine();

            System.out.println("Enter amount:");
            double amount = Double.parseDouble(scanner.nextLine());

            if (amount <= 0) throw new NumberFormatException("Amount must be positive");

            Transaction t = new Transaction(date, time, description, vendor, amount);
            transactions.add(t);
            saveTransaction(t);
        } catch (Exception e) {
            System.out.println("Invalid input: " + e.getMessage());
        }
    }

    private static void addPayment(Scanner scanner) {
        try {
            System.out.println("Enter date and time (yyyy-MM-dd HH:mm:ss):");
            String dateTimeStr = scanner.nextLine();
            String[] dt = dateTimeStr.split(" ");
            LocalDate date = LocalDate.parse(dt[0], DATE_FORMATTER);
            LocalTime time = LocalTime.parse(dt[1], TIME_FORMATTER);

            System.out.println("Enter description:");
            String description = scanner.nextLine();

            System.out.println("Enter vendor:");
            String vendor = scanner.nextLine();

            System.out.println("Enter amount:");
            double amount = -Math.abs(Double.parseDouble(scanner.nextLine()));

            Transaction t = new Transaction(date, time, description, vendor, amount);
            transactions.add(t);
            saveTransaction(t);
        } catch (Exception e) {
            System.out.println("Invalid input: " + e.getMessage());
        }
    }

    private static void saveTransaction(Transaction t) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
            writer.write(String.format("%s|%s|%s|%s|%.2f\n",
                    t.getDate().format(DATE_FORMATTER),
                    t.getTime().format(TIME_FORMATTER),
                    t.getDescription(),
                    t.getVendor(),
                    t.getAmount()));
        } catch (IOException e) {
            System.out.println("Error saving transaction: " + e.getMessage());
        }
    }

    private static void ledgerMenu(Scanner scanner) {
        boolean running = true;
        while (running) {
            System.out.println("Ledger");
            System.out.println("Choose an option:");
            System.out.println("A) All");
            System.out.println("D) Deposits");
            System.out.println("P) Payments");
            System.out.println("R) Reports");
            System.out.println("H) Home");

            String input = scanner.nextLine().trim();

            switch (input.toUpperCase()) {
                case "A":
                    displayLedger();
                    break;
                case "D":
                    displayDeposits();
                    break;
                case "P":
                    displayPayments();
                    break;
                case "R":
                    reportsMenu(scanner);
                    break;
                case "H":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option");
                    break;
            }
        }
    }

    private static void displayLedger() {
        for (Transaction t : transactions) {
            System.out.printf("%s %s | %s | %s | %.2f\n",
                    t.getDate(), t.getTime(), t.getDescription(), t.getVendor(), t.getAmount());
        }
    }

    private static void displayDeposits() {
        for (Transaction t : transactions) {
            if (t.getAmount() > 0) {
                System.out.printf("%s %s | %s | %s | %.2f\n",
                        t.getDate(), t.getTime(), t.getDescription(), t.getVendor(), t.getAmount());
            }
        }
    }

    private static void displayPayments() {
        for (Transaction t : transactions) {
            if (t.getAmount() < 0) {
                System.out.printf("%s %s | %s | %s | %.2f\n",
                        t.getDate(), t.getTime(), t.getDescription(), t.getVendor(), t.getAmount());
            }
        }
    }

    private static void reportsMenu(Scanner scanner) {
        boolean running = true;
        while (running) {
            System.out.println("Reports");
            System.out.println("Choose an option:");
            System.out.println("1) Month To Date");
            System.out.println("2) Previous Month");
            System.out.println("3) Year To Date");
            System.out.println("4) Previous Year");
            System.out.println("5) Search by Vendor");
            System.out.println("0) Back");

            String input = scanner.nextLine().trim();
            LocalDate now = LocalDate.now();

            switch (input) {
                case "1":
                    filterTransactionsByDate(now.withDayOfMonth(1), now);
                    break;
                case "2":
                    LocalDate firstDayPrevMonth = now.minusMonths(1).withDayOfMonth(1);
                    LocalDate lastDayPrevMonth = firstDayPrevMonth.withDayOfMonth(firstDayPrevMonth.lengthOfMonth());
                    filterTransactionsByDate(firstDayPrevMonth, lastDayPrevMonth);
                    break;
                case "3":
                    filterTransactionsByDate(now.withDayOfYear(1), now);
                    break;
                case "4":
                    LocalDate firstDayPrevYear = now.minusYears(1).withDayOfYear(1);
                    LocalDate lastDayPrevYear = firstDayPrevYear.withDayOfYear(firstDayPrevYear.lengthOfYear());
                    filterTransactionsByDate(firstDayPrevYear, lastDayPrevYear);
                    break;
                case "5":
                    System.out.println("Enter vendor name:");
                    String vendor = scanner.nextLine();
                    filterTransactionsByVendor(vendor);
                    break;
                case "0":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option");
                    break;
            }
        }
    }

    private static void filterTransactionsByDate(LocalDate startDate, LocalDate endDate) {
        boolean found = false;
        for (Transaction t : transactions) {
            if (!t.getDate().isBefore(startDate) && !t.getDate().isAfter(endDate)) {
                System.out.printf("%s %s | %s | %s | %.2f\n",
                        t.getDate(), t.getTime(), t.getDescription(), t.getVendor(), t.getAmount());
                found = true;
            }
        }
        if (!found) System.out.println("No transactions found in that date range.");
    }

    private static void filterTransactionsByVendor(String vendor) {
        boolean found = false;
        for (Transaction t : transactions) {
            if (t.getVendor().equalsIgnoreCase(vendor)) {
                System.out.printf("%s %s | %s | %s | %.2f\n",
                        t.getDate(), t.getTime(), t.getDescription(), t.getVendor(), t.getAmount());
                found = true;
            }
        }
        if (!found) System.out.println("No transactions found for that vendor.");
    }
}



