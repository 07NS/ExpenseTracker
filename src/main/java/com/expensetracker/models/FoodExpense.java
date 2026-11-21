package com.expensetracker.models;

public class FoodExpense extends Expense {
    public FoodExpense(int id, String title, double amount, String date, String description) {
        super(id, title, amount, "Food", date, description);
    }

    public FoodExpense(String title, double amount, String date, String description) {
        super(0, title, amount, "Food", date, description);
    }
}

