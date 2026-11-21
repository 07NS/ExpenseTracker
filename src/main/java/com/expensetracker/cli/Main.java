package com.expensetracker.cli;

import com.expensetracker.models.Expense;
import com.expensetracker.models.FoodExpense;
import com.expensetracker.models.TravelExpense;
import com.expensetracker.services.ExpenseService;
import com.expensetracker.services.ExpenseServiceImpl;
import com.expensetracker.utilities.CurrencyFormatter;
import com.expensetracker.utilities.DBConnection;

import java.util.List;
import java.util.Scanner;

/**
 * Command Line Interface entry point for Expense Tracker.
 * Preserved for headless or terminal-based usage.
 */
public class Main {
    public static void main(String[] args) {
        DBConnection.initializeDatabase();
        ExpenseService service = new ExpenseServiceImpl();
        Scanner sc = new Scanner(System.in);
        boolean running = true;

        System.out.println("🚀 Expense Insight Engine (CLI Mode)");

        while (running) {
            System.out.println("\n--- MAIN MENU ---");
            System.out.println("1. ➕ Add New Expense");
            System.out.println("2. 📄 View All Expenses");
            System.out.println("3. 📊 View Category Breakdown");
            System.out.println("4. 🗑️ Delete Expense");
            System.out.println("5. ❌ Exit");
            System.out.print("Select an option: ");

            int choice = -1;
            try {
                if (sc.hasNextInt()) {
                    choice = sc.nextInt();
                }
                sc.nextLine();
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a number.");
                sc.nextLine();
                continue;
            }

            switch (choice) {
                case 1:
                    System.out.print("Title: ");
                    String title = sc.nextLine();
                    System.out.print("Amount (₹): ");
                    double amt = 0.0;
                    try {
                        amt = CurrencyFormatter.parseAmount(sc.nextLine());
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid amount format.");
                        break;
                    }
                    System.out.print("Category (Food/Travel/Other): ");
                    String cat = sc.nextLine();
                    System.out.print("Description: ");
                    String desc = sc.nextLine();
                    System.out.print("Date (YYYY-MM-DD): ");
                    String date = sc.nextLine();

                    if (cat.equalsIgnoreCase("Food")) {
                        service.addExpense(new FoodExpense(title, amt, date, desc));
                    } else if (cat.equalsIgnoreCase("Travel")) {
                        service.addExpense(new TravelExpense(title, amt, date, desc));
                    } else {
                        service.addExpense(new Expense(title, amt, cat, date, desc));
                    }
                    System.out.println("✅ Expense recorded successfully!");
                    break;

                case 2:
                    List<Expense> all = service.getAllExpenses();
                    System.out.println("\n--- Your Saved Expenses ---");
                    for (Expense exp : all) {
                        System.out.printf("ID: %d | Title: %s | Amount: %s | Category: %s | Date: %s\n",
                                exp.getId(), exp.getTitle(), CurrencyFormatter.format(exp.getAmount()), exp.getCategory(), exp.getDate());
                    }
                    System.out.println("---------------------------\n");
                    break;

                case 3:
                    System.out.println("\n--- 📊 CATEGORY ANALYSIS ---");
                    service.getCategorySummaries().forEach(cs ->
                            System.out.printf("🔹 %-12s: %-12s (%s)\n", cs.getCategory(), cs.getFormattedTotal(), cs.getFormattedPercentage())
                    );
                    System.out.println("----------------------------\n");
                    break;

                case 4:
                    System.out.print("Enter ID of expense to delete: ");
                    try {
                        int id = Integer.parseInt(sc.nextLine());
                        service.deleteExpense(id);
                        System.out.println("✅ Delete operation completed.");
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid ID.");
                    }
                    break;

                case 5:
                    running = false;
                    System.out.println("Closing Expense Tracker CLI...");
                    break;

                default:
                    System.out.println("Invalid choice. Please select 1-5.");
            }
        }
        sc.close();
    }
}

