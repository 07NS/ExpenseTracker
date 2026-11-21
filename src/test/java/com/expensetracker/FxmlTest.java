package com.expensetracker;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class FxmlTest {
    public static void main(String[] args) throws Exception {
        System.out.println("Starting JavaFX FXML verification...");

        // Ensure JavaFX toolkit is initialized
        CountDownLatch latch = new CountDownLatch(1);
        try {
            Platform.startup(latch::countDown);
        } catch (IllegalStateException e) {
            // Toolkit already initialized
            latch.countDown();
        }

        if (!latch.await(5, TimeUnit.SECONDS)) {
            System.err.println("❌ Failed to start JavaFX platform.");
            System.exit(1);
        }

        String[] fxmlFiles = {
                "/com/expensetracker/views/MainLayout.fxml",
                "/com/expensetracker/views/Dashboard.fxml",
                "/com/expensetracker/views/Transactions.fxml",
                "/com/expensetracker/views/ExpenseDialog.fxml",
                "/com/expensetracker/views/Reports.fxml"
        };

        for (String fxml : fxmlFiles) {
            try {
                FXMLLoader loader = new FXMLLoader(FxmlTest.class.getResource(fxml));
                Object root = loader.load();
                assert root != null : "Root node is null for " + fxml;
                System.out.println("✅ Successfully loaded: " + fxml);
            } catch (Exception e) {
                System.err.println("❌ FXML parse error in " + fxml + ": " + e.getMessage());
                e.printStackTrace();
                System.exit(1);
            }
        }

        System.out.println("\n🎉 ALL 5 FXML VIEWS LOADED AND BOUND WITH ZERO ERRORS!");
        Platform.exit();
        System.exit(0);
    }
}

