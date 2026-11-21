package com.expensetracker.controllers;

import com.expensetracker.models.CategorySummary;
import com.expensetracker.models.MonthlySummary;
import com.expensetracker.services.ExpenseService;
import com.expensetracker.utilities.CurrencyFormatter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

/**
 * Controller for generating and displaying spending reports, charts, and summaries.
 */
public class ReportsController {

    @FXML private Label lblTotalSpending;
    @FXML private Label lblCategoryCount;
    @FXML private Label lblMonthCount;

    @FXML private PieChart chartCategoryPie;
    @FXML private BarChart<String, Number> chartMonthlyBar;

    @FXML private TableView<CategorySummary> tableCategory;
    @FXML private TableColumn<CategorySummary, String> colCatName;
    @FXML private TableColumn<CategorySummary, String> colCatTotal;
    @FXML private TableColumn<CategorySummary, String> colCatPercentage;

    @FXML private TableView<MonthlySummary> tableMonthly;
    @FXML private TableColumn<MonthlySummary, String> colMonthName;
    @FXML private TableColumn<MonthlySummary, String> colMonthTotal;

    private ExpenseService expenseService;

    @FXML
    public void initialize() {
        // Category table columns
        colCatName.setCellValueFactory(new PropertyValueFactory<>("category"));
        colCatTotal.setCellValueFactory(new PropertyValueFactory<>("formattedTotal"));
        colCatPercentage.setCellValueFactory(new PropertyValueFactory<>("formattedPercentage"));

        // Monthly table columns
        colMonthName.setCellValueFactory(new PropertyValueFactory<>("month"));
        colMonthTotal.setCellValueFactory(new PropertyValueFactory<>("formattedTotal"));

        // Disable animations to prevent glitching on tab change
        chartCategoryPie.setAnimated(false);
        chartCategoryPie.setLabelsVisible(false);
        chartCategoryPie.setLegendVisible(true);
        chartMonthlyBar.setAnimated(false);
    }

    public void setExpenseService(ExpenseService service) {
        this.expenseService = service;
        refreshData();
    }

    public void refreshData() {
        if (expenseService == null) return;

        // 1. KPI Metrics
        double total = expenseService.getTotalExpenses();
        lblTotalSpending.setText(CurrencyFormatter.format(total));

        List<CategorySummary> categorySummaries = expenseService.getCategorySummaries();
        lblCategoryCount.setText(String.valueOf(categorySummaries.size()));

        List<MonthlySummary> monthlySummaries = expenseService.getMonthlySummaries();
        lblMonthCount.setText(String.valueOf(monthlySummaries.size()));

        // 2. Category table
        tableCategory.setItems(FXCollections.observableArrayList(categorySummaries));

        // 3. Monthly table
        tableMonthly.setItems(FXCollections.observableArrayList(monthlySummaries));

        // 4. Category Pie Chart
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        for (CategorySummary cs : categorySummaries) {
            String label = String.format("%s: %s (%.1f%%)", cs.getCategory(), CurrencyFormatter.formatCompact(cs.getTotal()), cs.getPercentage());
            pieData.add(new PieChart.Data(label, cs.getTotal()));
        }
        chartCategoryPie.setData(pieData);

        for (PieChart.Data data : chartCategoryPie.getData()) {
            if (data.getNode() != null) {
                Tooltip.install(data.getNode(), new Tooltip(data.getName()));
            }
        }

        // 5. Monthly Bar Chart
        chartMonthlyBar.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Monthly Spending (₹)");

        for (MonthlySummary ms : monthlySummaries) {
            series.getData().add(new XYChart.Data<>(ms.getMonth(), ms.getTotal()));
        }
        chartMonthlyBar.getData().add(series);
    }
}
