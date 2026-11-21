package com.expensetracker.services;

import com.expensetracker.models.CategorySummary;
import com.expensetracker.models.Expense;
import com.expensetracker.models.MonthlySummary;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Service contract defining business logic and persistence operations
 * for expense management and analytics.
 */
public interface ExpenseService {

    // CRUD operations
    void addExpense(Expense expense);
    List<Expense> getAllExpenses();
    Expense getExpenseById(int id);
    void updateExpense(Expense expense);
    void deleteExpense(int id);

    // Dashboard metrics
    double getTotalExpenses();
    double getCurrentMonthTotal();
    Expense getHighestExpense();
    List<Expense> getRecentExpenses(int limit);

    // Filter and search
    List<Expense> searchAndFilter(String query, String category, LocalDate fromDate, LocalDate toDate);

    // Analytics and reports
    Map<String, Double> getCategorySpending();
    List<CategorySummary> getCategorySummaries();
    List<MonthlySummary> getMonthlySummaries();
    List<String> getDistinctCategories();
}

