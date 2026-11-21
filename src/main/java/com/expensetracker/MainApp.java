package com.expensetracker;

import com.expensetracker.utilities.DBConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Main application entry point for the JavaFX Expense Insight Engine.
 */
public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Initialize SQLite database and perform automatic schema upgrade
        DBConnection.initializeDatabase();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/expensetracker/views/MainLayout.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, 1120, 760);

            primaryStage.setTitle("ExpenseInsight • Personal Finance Dashboard");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(960);
            primaryStage.setMinHeight(640);
            primaryStage.show();

        } catch (IOException e) {
            System.err.println("Failed to start application: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

