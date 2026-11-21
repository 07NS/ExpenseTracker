package com.expensetracker.services;

import com.expensetracker.models.CategorySummary;
import com.expensetracker.models.Expense;
import com.expensetracker.models.MonthlySummary;
import com.expensetracker.utilities.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Service implementation managing expense data persistence via SQLite JDBC.
 * Uses PreparedStatements for all parameterized queries.
 */
public class ExpenseServiceImpl implements ExpenseService {

    @Override
    public void addExpense(Expense expense) {
        String sql = "INSERT INTO expenses(title, amount, category, description, date) VALUES(?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, expense.getTitle());
            pstmt.setDouble(2, expense.getAmount());
            pstmt.setString(3, expense.getCategory());
            pstmt.setString(4, expense.getDescription());
            pstmt.setString(5, expense.getDate());
            pstmt.executeUpdate();

            try (ResultSet keys = pstmt.getGeneratedKeys()) {
                if (keys.next()) {
                    expense.setId(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding expense: " + e.getMessage());
        }
    }

    @Override
    public List<Expense> getAllExpenses() {
        List<Expense> list = new ArrayList<>();
        String sql = "SELECT * FROM expenses ORDER BY date DESC, id DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching expenses: " + e.getMessage());
        }
        return list;
    }

    @Override
    public Expense getExpenseById(int id) {
        String sql = "SELECT * FROM expenses WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching expense by ID: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void updateExpense(Expense expense) {
        String sql = "UPDATE expenses SET title = ?, amount = ?, category = ?, description = ?, date = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, expense.getTitle());
            pstmt.setDouble(2, expense.getAmount());
            pstmt.setString(3, expense.getCategory());
            pstmt.setString(4, expense.getDescription());
            pstmt.setString(5, expense.getDate());
            pstmt.setInt(6, expense.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating expense: " + e.getMessage());
        }
    }

    @Override
    public void deleteExpense(int id) {
        String sql = "DELETE FROM expenses WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting expense: " + e.getMessage());
        }
    }

    @Override
    public double getTotalExpenses() {
        String sql = "SELECT COALESCE(SUM(amount), 0.0) FROM expenses";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("Error calculating total: " + e.getMessage());
        }
        return 0.0;
    }

    @Override
    public double getCurrentMonthTotal() {
        String currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        String sql = "SELECT COALESCE(SUM(amount), 0.0) FROM expenses WHERE substr(date, 1, 7) = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, currentMonth);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error calculating month total: " + e.getMessage());
        }
        return 0.0;
    }

    @Override
    public Expense getHighestExpense() {
        String sql = "SELECT * FROM expenses ORDER BY amount DESC LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting highest expense: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Expense> getRecentExpenses(int limit) {
        List<Expense> list = new ArrayList<>();
        String sql = "SELECT * FROM expenses ORDER BY date DESC, id DESC LIMIT ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, limit);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching recent expenses: " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<Expense> searchAndFilter(String query, String category, LocalDate fromDate, LocalDate toDate) {
        List<Expense> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM expenses WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (query != null && !query.trim().isEmpty()) {
            sql.append("AND (LOWER(title) LIKE ? OR LOWER(description) LIKE ?) ");
            String keyword = "%" + query.trim().toLowerCase() + "%";
            params.add(keyword);
            params.add(keyword);
        }

        if (category != null && !category.trim().isEmpty() && !category.equalsIgnoreCase("All") && !category.equalsIgnoreCase("All Categories")) {
            sql.append("AND category = ? ");
            params.add(category.trim());
        }

        if (fromDate != null) {
            sql.append("AND date >= ? ");
            params.add(fromDate.toString());
        }

        if (toDate != null) {
            sql.append("AND date <= ? ");
            params.add(toDate.toString());
        }

        sql.append("ORDER BY date DESC, id DESC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error filtering expenses: " + e.getMessage());
        }
        return list;
    }

    @Override
    public Map<String, Double> getCategorySpending() {
        Map<String, Double> map = new LinkedHashMap<>();
        String sql = "SELECT category, SUM(amount) FROM expenses GROUP BY category ORDER BY SUM(amount) DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                map.put(rs.getString(1), rs.getDouble(2));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving category spending: " + e.getMessage());
        }
        return map;
    }

    @Override
    public List<CategorySummary> getCategorySummaries() {
        List<CategorySummary> summaries = new ArrayList<>();
        double total = getTotalExpenses();
        String sql = "SELECT category, SUM(amount) as cat_total FROM expenses GROUP BY category ORDER BY cat_total DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String cat = rs.getString("category");
                double catTotal = rs.getDouble("cat_total");
                double percentage = (total > 0) ? (catTotal / total) * 100.0 : 0.0;
                summaries.add(new CategorySummary(cat, catTotal, percentage));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving category summaries: " + e.getMessage());
        }
        return summaries;
    }

    @Override
    public List<MonthlySummary> getMonthlySummaries() {
        List<MonthlySummary> summaries = new ArrayList<>();
        String sql = "SELECT substr(date, 1, 7) as month_year, SUM(amount) as month_total " +
                     "FROM expenses " +
                     "WHERE date IS NOT NULL AND length(date) >= 7 " +
                     "GROUP BY month_year " +
                     "ORDER BY month_year ASC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                summaries.add(new MonthlySummary(rs.getString("month_year"), rs.getDouble("month_total")));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving monthly summaries: " + e.getMessage());
        }
        return summaries;
    }

    @Override
    public List<String> getDistinctCategories() {
        List<String> categories = new ArrayList<>();
        String sql = "SELECT DISTINCT category FROM expenses WHERE category IS NOT NULL AND trim(category) != '' ORDER BY category ASC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                categories.add(rs.getString(1));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching distinct categories: " + e.getMessage());
        }

        // Add standard categories if list is empty or missing them
        String[] defaults = {"Food", "Travel", "Housing", "Utilities", "Entertainment", "Health", "Shopping", "Other"};
        for (String def : defaults) {
            if (!categories.contains(def)) {
                categories.add(def);
            }
        }
        return categories;
    }

    /**
     * Helper method to map a database ResultSet row to an Expense object.
     */
    private Expense mapRow(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String title = rs.getString("title");
        double amount = rs.getDouble("amount");
        String category = rs.getString("category");
        String description = rs.getString("description");
        String date = rs.getString("date");

        if (title == null || title.trim().isEmpty()) {
            title = (description != null && !description.trim().isEmpty()) ? description : (category + " Expense");
        }

        return new Expense(id, title, amount, category, date, description);
    }
}
