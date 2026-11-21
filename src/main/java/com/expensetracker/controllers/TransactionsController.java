package com.expensetracker.controllers;

import com.expensetracker.models.Expense;
import com.expensetracker.services.ExpenseService;
import com.expensetracker.utilities.CurrencyFormatter;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controller for managing, searching, filtering, and performing CRUD on transactions.
 */
public class TransactionsController {

    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> comboFilterCategory;
    @FXML private DatePicker dpFromDate;
    @FXML private DatePicker dpToDate;
    @FXML private Button btnResetFilter;
    @FXML private Button btnAddExpense;

    @FXML private TableView<Expense> tableTransactions;
    @FXML private TableColumn<Expense, Integer> colId;
    @FXML private TableColumn<Expense, String> colTitle;
    @FXML private TableColumn<Expense, String> colCategory;
    @FXML private TableColumn<Expense, Double> colAmount;
    @FXML private TableColumn<Expense, String> colDate;
    @FXML private TableColumn<Expense, String> colDescription;

    @FXML private Label lblRecordCount;
    @FXML private Button btnEdit;
    @FXML private Button btnDelete;

    private ExpenseService expenseService;
    private Runnable onDataChanged;

    @FXML
    public void initialize() {
        // Table columns binding
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));

        // Format amount column
        colAmount.setCellFactory(col -> new TableCell<Expense, Double>() {
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

        // Filter event listeners
        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        comboFilterCategory.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        dpFromDate.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        dpToDate.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
    }

    public void setExpenseService(ExpenseService service, Runnable dataChangedCallback) {
        this.expenseService = service;
        this.onDataChanged = dataChangedCallback;
        loadCategoryFilter();
        refreshData();
    }

    private void loadCategoryFilter() {
        if (expenseService == null) return;
        List<String> list = new ArrayList<>();
        list.add("All Categories");
        list.addAll(expenseService.getDistinctCategories());
        comboFilterCategory.setItems(FXCollections.observableArrayList(list));
        comboFilterCategory.getSelectionModel().selectFirst();
    }

    public void refreshData() {
        applyFilters();
    }

    private void applyFilters() {
        if (expenseService == null) return;

        String query = txtSearch.getText();
        String selectedCategory = comboFilterCategory.getValue();
        if ("All Categories".equalsIgnoreCase(selectedCategory)) {
            selectedCategory = null;
        }

        LocalDate from = dpFromDate.getValue();
        LocalDate to = dpToDate.getValue();

        List<Expense> results = expenseService.searchAndFilter(query, selectedCategory, from, to);
        tableTransactions.setItems(FXCollections.observableArrayList(results));
        lblRecordCount.setText(String.format("Showing %d expense%s", results.size(), results.size() == 1 ? "" : "s"));
    }

    @FXML
    private void handleResetFilters() {
        txtSearch.clear();
        comboFilterCategory.getSelectionModel().selectFirst();
        dpFromDate.setValue(null);
        dpToDate.setValue(null);
        applyFilters();
    }

    @FXML
    private void handleOpenAddDialog() {
        openModal(null);
    }

    @FXML
    private void handleEditSelected() {
        Expense selected = tableTransactions.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an expense from the table to edit.");
            return;
        }
        openModal(selected);
    }

    @FXML
    private void handleDeleteSelected() {
        Expense selected = tableTransactions.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an expense from the table to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Deletion");
        confirm.setHeaderText("Delete Expense #" + selected.getId() + "?");
        confirm.setContentText(String.format("Are you sure you want to delete \"%s\" (%s)?",
                selected.getTitle(), CurrencyFormatter.format(selected.getAmount())));

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            expenseService.deleteExpense(selected.getId());
            refreshData();
            if (onDataChanged != null) {
                onDataChanged.run();
            }
        }
    }

    private void openModal(Expense expenseToEdit) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/expensetracker/views/ExpenseDialog.fxml"));
            Parent root = loader.load();

            ExpenseDialogController controller = loader.getController();
            controller.setExpenseService(expenseService);

            if (expenseToEdit != null) {
                controller.setExpenseForEdit(expenseToEdit);
            }

            Stage dialogStage = new Stage();
            dialogStage.setTitle(expenseToEdit == null ? "Add New Expense" : "Edit Expense");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(btnAddExpense.getScene().getWindow());
            dialogStage.setScene(new Scene(root));
            dialogStage.setResizable(false);
            dialogStage.showAndWait();

            if (controller.isSaved()) {
                refreshData();
                loadCategoryFilter();
                if (onDataChanged != null) {
                    onDataChanged.run();
                }
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open dialog: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
