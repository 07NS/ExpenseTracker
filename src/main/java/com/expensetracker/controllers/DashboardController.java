package com.expensetracker.controllers;

import com.expensetracker.models.Expense;
import com.expensetracker.services.ExpenseService;
import com.expensetracker.utilities.CurrencyFormatter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Controller for the main dashboard view displaying KPIs, recent expenses, and spending charts.
 */
public class DashboardController {

    @FXML private Label lblTotalExpenses;
    @FXML private Label lblMonthlyExpenses;
    @FXML private Label lblCurrentMonthLabel;
    @FXML private Label lblHighestExpense;
    @FXML private Label lblHighestExpenseTitle;

    @FXML private TableView<Expense> tableRecent;
    @FXML private TableColumn<Expense, String> colRecentTitle;
    @FXML private TableColumn<Expense, String> colRecentCategory;
    @FXML private TableColumn<Expense, String> colRecentDate;
    @FXML private TableColumn<Expense, Double> colRecentAmount;

    @FXML private PieChart chartCategory;

    private ExpenseService expenseService;

    @FXML
    public void initialize() {
        // Bind table columns to Expense properties
        colRecentTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colRecentCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colRecentDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colRecentAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));

        // Format currency in amount column
        colRecentAmount.setCellFactory(col -> new TableCell<Expense, Double>() {
            @Override
            protected void updateItem(Double amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) {
                    setText(null);
                } else {
                    setText(CurrencyFormatter.format(amount));
                    setStyle("-fx-font-weight: bold; -fx-alignment: CENTER_RIGHT;");
                }
            }
        });

        // Disable animation to avoid tab-switch glitching
        chartCategory.setAnimated(false);
        chartCategory.setLabelsVisible(false);
        chartCategory.setLegendVisible(true);
    }

    public void setExpenseService(ExpenseService service) {
        this.expenseService = service;
        refreshData();
    }

    /**
     * Refreshes dashboard metrics, recent transactions table, and category chart.
     */
    public void refreshData() {
        if (expenseService == null) return;

        // 1. Total expenses
        double total = expenseService.getTotalExpenses();
        lblTotalExpenses.setText(CurrencyFormatter.format(total));

        // 2. Current month total
        double monthTotal = expenseService.getCurrentMonthTotal();
        lblMonthlyExpenses.setText(CurrencyFormatter.format(monthTotal));
        String monthName = LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM yyyy"));
        lblCurrentMonthLabel.setText("Spending in " + monthName);

        // 3. Highest expense
        Expense highest = expenseService.getHighestExpense();
        if (highest != null) {
            lblHighestExpense.setText(CurrencyFormatter.format(highest.getAmount()));
            lblHighestExpenseTitle.setText(highest.getTitle() + " (" + highest.getCategory() + ")");
        } else {
            lblHighestExpense.setText("₹0.00");
            lblHighestExpenseTitle.setText("No recorded expenses");
        }

        // 4. Recent transactions
        List<Expense> recent = expenseService.getRecentExpenses(6);
        tableRecent.setItems(FXCollections.observableArrayList(recent));

        // 5. Category pie chart
        Map<String, Double> spending = expenseService.getCategorySpending();
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        for (Map.Entry<String, Double> entry : spending.entrySet()) {
            String cat = entry.getKey();
            double amt = entry.getValue();
            double pct = (total > 0) ? (amt / total) * 100.0 : 0.0;
            String label = String.format("%s: %s (%.1f%%)", cat, CurrencyFormatter.formatCompact(amt), pct);
            pieData.add(new PieChart.Data(label, amt));
        }
        chartCategory.setData(pieData);

        // Add tooltips to chart slices
        for (PieChart.Data data : chartCategory.getData()) {
            if (data.getNode() != null) {
                Tooltip.install(data.getNode(), new Tooltip(data.getName()));
            }
        }
    }
}
