package com.pluralsight;

import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;




public class LedgerScreen {
    public static void showLedger(List<Transaction> transactions, Scanner scanner) {
        boolean running = true;
        while (running) {
            System.out.println("\nLedger Options:");
            System.out.println("A) All");
            System.out.println("D) Deposits");
            System.out.println("P) Payments");
            System.out.println("R) Reports");
            System.out.println("H) Home");
            System.out.print("Choose an option ");
            String choice = scanner.nextLine().toUpperCase();

            switch (choice) {
                case "A":
                    display(transactions);
                    break;
                case "D":
                    display(transactions.stream()
                            .filter(t -> t.getAmount() > 0)
                            .collect(Collectors.toList()));
                    break;
                case "P":
                    display(transactions.stream()
                            .filter(t -> t.getAmount() < 0)
                            .collect(Collectors.toList()));
                    break;
                case "R":
                    ReportScreen.showReports(transactions, scanner);
                    break;
                case "H":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
    private static void display(List<Transaction> transactions) {
        Collections.reverse(transactions);
        for (Transaction t : transactions) {
            System.out.println(t);
        }
        Collections.reverse(transactions);
    }
}
