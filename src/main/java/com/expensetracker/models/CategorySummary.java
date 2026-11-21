package com.expensetracker.models;

import com.expensetracker.utilities.CurrencyFormatter;

public class CategorySummary {
    private String category;
    private double total;
    private double percentage;

    public CategorySummary(String category, double total, double percentage) {
        this.category = category;
        this.total = total;
        this.percentage = percentage;
    }

    public String getCategory() {
        return category;
    }

    public double getTotal() {
        return total;
    }

    public double getPercentage() {
        return percentage;
    }

    public String getFormattedTotal() {
        return CurrencyFormatter.format(total);
    }

    public String getFormattedPercentage() {
        return String.format("%.1f%%", percentage);
    }
}

