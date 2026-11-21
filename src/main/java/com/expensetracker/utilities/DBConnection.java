package com.expensetracker.utilities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Manages database connection and table initialization for SQLite.
 */
public class DBConnection {
    private static final String URL = "jdbc:sqlite:expenses.db";

    /**
     * Initializes the expenses table in SQLite if it does not exist.
     */
    public static void initializeDatabase() {
        String sql = "CREATE TABLE IF NOT EXISTS expenses (" +
                     "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                     "title TEXT, " +
                     "amount REAL, " +
                     "category TEXT, " +
                     "description TEXT, " +
                     "date TEXT)";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Database initialization error: " + e.getMessage());
        }
    }

    /**
     * Returns a database connection to SQLite.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}
