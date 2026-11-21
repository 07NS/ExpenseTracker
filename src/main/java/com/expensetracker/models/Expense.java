package com.expensetracker.models;

import com.expensetracker.utilities.CurrencyFormatter;

/**
 * Model class representing an individual expense entry.
 */
public class Expense {
    private int id;
    private String title;
    private double amount;
    private String category;
    private String date; // Format: YYYY-MM-DD
    private String description;

    public Expense() {
    }

    public Expense(int id, String title, double amount, String category, String date, String description) {
        this.id = id;
        this.title = (title != null && !title.trim().isEmpty()) ? title.trim() : category;
        this.amount = amount;
        this.category = (category != null) ? category.trim() : "General";
        this.date = date;
        this.description = (description != null) ? description.trim() : "";
    }

    public Expense(String title, double amount, String category, String date, String description) {
        this(0, title, amount, category, date, description);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFormattedAmount() {
        return CurrencyFormatter.format(amount);
    }

    @Override
    public String toString() {
        return String.format("#%d | %s | %s | %s | %s",
                id, title, CurrencyFormatter.format(amount), category, date);
    }
}
