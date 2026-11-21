package com.expensetracker;

import com.expensetracker.models.CategorySummary;
import com.expensetracker.models.Expense;
import com.expensetracker.models.MonthlySummary;
import com.expensetracker.services.ExpenseService;
import com.expensetracker.services.ExpenseServiceImpl;
import com.expensetracker.utilities.CurrencyFormatter;
import com.expensetracker.utilities.DBConnection;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class ServiceTest {
    public static void main(String[] args) {
        System.out.println("Starting backend & database integration verification...");

        // 1. Initialize Database
        DBConnection.initializeDatabase();
        System.out.println("✅ Database initialized successfully.");

        // 2. Service instance
        ExpenseService service = new ExpenseServiceImpl();

        // 3. Read existing records
        List<Expense> initialList = service.getAllExpenses();
        System.out.println("Initial expense count in DB: " + initialList.size());
        for (Expense e : initialList) {
            System.out.println("  " + e);
        }

        // 4. Test Create (Add Expense)
        Expense testExpense = new Expense("Test Grocery", 85.50, "Food", LocalDate.now().toString(), "Organic vegetables");
        service.addExpense(testExpense);
        System.out.println("✅ Added test expense with ID: " + testExpense.getId());

        // 5. Test Read & Verify
        Expense retrieved = service.getExpenseById(testExpense.getId());
        assert retrieved != null : "Retrieved expense should not be null";
        System.out.println("✅ Verified created expense: " + retrieved);

        // 6. Test Update
        retrieved.setAmount(95.00);
        retrieved.setTitle("Updated Test Grocery");
        service.updateExpense(retrieved);
        Expense updated = service.getExpenseById(testExpense.getId());
        assert updated.getAmount() == 95.00 : "Amount should be updated to 95.00";
        System.out.println("✅ Verified updated expense: " + updated);

        // 7. Test Metrics
        double total = service.getTotalExpenses();
        double monthTotal = service.getCurrentMonthTotal();
        Expense highest = service.getHighestExpense();
        System.out.printf("✅ Metrics: Total=%s, Month Total=%s, Highest=%s\n",
                CurrencyFormatter.format(total), CurrencyFormatter.format(monthTotal), (highest != null ? highest.getTitle() : "None"));

        // Verify Indian numbering format
        String indianFormatted = CurrencyFormatter.format(1250000.75);
        assert indianFormatted.contains("12,50,000.75") : "Should match Indian Lakhs format: " + indianFormatted;
        System.out.println("✅ Indian currency format verified: " + indianFormatted);

        // 8. Test Search & Filter
        List<Expense> filtered = service.searchAndFilter("Updated", "Food", null, null);
        assert !filtered.isEmpty() : "Search should return the updated expense";
        System.out.println("✅ Filter verification passed. Found: " + filtered.size());

        // 9. Test Category & Monthly Summaries
        List<CategorySummary> catSummaries = service.getCategorySummaries();
        System.out.println("✅ Category Summaries: " + catSummaries.size());
        for (CategorySummary cs : catSummaries) {
            System.out.println("  " + cs.getCategory() + " => " + cs.getFormattedTotal() + " (" + cs.getFormattedPercentage() + ")");
        }

        List<MonthlySummary> monthSummaries = service.getMonthlySummaries();
        System.out.println("✅ Monthly Summaries: " + monthSummaries.size());
        for (MonthlySummary ms : monthSummaries) {
            System.out.println("  " + ms.getMonth() + " => " + ms.getFormattedTotal());
        }

        // 10. Clean up test record
        service.deleteExpense(testExpense.getId());
        assert service.getExpenseById(testExpense.getId()) == null : "Expense should be deleted";
        System.out.println("✅ Delete verification passed. Cleaned up test record.");

        System.out.println("\n🎉 ALL BACKEND, DATABASE, AND CRUD TESTS PASSED SUCCESSFULLY!");
    }
}

