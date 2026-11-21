package com.expensetracker.models;

import com.expensetracker.utilities.CurrencyFormatter;

public class MonthlySummary {
    private String month;
    private double total;

    public MonthlySummary(String month, double total) {
        this.month = month;
        this.total = total;
    }

    public String getMonth() {
        return month;
    }

    public double getTotal() {
        return total;
    }

    public String getFormattedTotal() {
        return CurrencyFormatter.format(total);
    }
}

