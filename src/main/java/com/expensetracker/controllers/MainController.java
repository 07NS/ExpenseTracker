package com.expensetracker.controllers;

import com.expensetracker.services.ExpenseService;
import com.expensetracker.services.ExpenseServiceImpl;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

import java.io.IOException;

/**
 * Controller for the main application shell and sidebar navigation.
 */
public class MainController {

    @FXML private Button btnDashboard;
    @FXML private Button btnTransactions;
    @FXML private Button btnReports;
    @FXML private StackPane contentArea;

    private ExpenseService expenseService;

    private Parent dashboardView;
    private DashboardController dashboardController;

    private Parent transactionsView;
    private TransactionsController transactionsController;

    private Parent reportsView;
    private ReportsController reportsController;

    @FXML
    public void initialize() {
        this.expenseService = new ExpenseServiceImpl();
        loadViews();
        handleNavDashboard();
    }

    private void loadViews() {
        try {
            // Dashboard
            FXMLLoader dashLoader = new FXMLLoader(getClass().getResource("/com/expensetracker/views/Dashboard.fxml"));
            dashboardView = dashLoader.load();
            dashboardController = dashLoader.getController();
            dashboardController.setExpenseService(expenseService);

            // Transactions
            FXMLLoader txLoader = new FXMLLoader(getClass().getResource("/com/expensetracker/views/Transactions.fxml"));
            transactionsView = txLoader.load();
            transactionsController = txLoader.getController();
            transactionsController.setExpenseService(expenseService, this::onDataChanged);

            // Reports
            FXMLLoader repLoader = new FXMLLoader(getClass().getResource("/com/expensetracker/views/Reports.fxml"));
            reportsView = repLoader.load();
            reportsController = repLoader.getController();
            reportsController.setExpenseService(expenseService);

        } catch (IOException e) {
            System.err.println("Error loading views: " + e.getMessage());
        }
    }

    /**
     * Called when transactions are added, edited, or deleted.
     */
    private void onDataChanged() {
        if (dashboardController != null) dashboardController.refreshData();
        if (reportsController != null) reportsController.refreshData();
    }

    @FXML
    private void handleNavDashboard() {
        switchView(btnDashboard, dashboardView);
        if (dashboardController != null) dashboardController.refreshData();
    }

    @FXML
    private void handleNavTransactions() {
        switchView(btnTransactions, transactionsView);
        if (transactionsController != null) transactionsController.refreshData();
    }

    @FXML
    private void handleNavReports() {
        switchView(btnReports, reportsView);
        if (reportsController != null) reportsController.refreshData();
    }

    private void switchView(Button activeBtn, Parent view) {
        btnDashboard.getStyleClass().remove("active");
        btnTransactions.getStyleClass().remove("active");
        btnReports.getStyleClass().remove("active");

        if (!activeBtn.getStyleClass().contains("active")) {
            activeBtn.getStyleClass().add("active");
        }

        if (view != null) {
            contentArea.getChildren().setAll(view);
        }
    }
}
