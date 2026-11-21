package com.expensetracker.models;

public class TravelExpense extends Expense {
    public TravelExpense(int id, String title, double amount, String date, String description) {
        super(id, title, amount, "Travel", date, description);
    }

    public TravelExpense(String title, double amount, String date, String description) {
        super(0, title, amount, "Travel", date, description);
    }
}

