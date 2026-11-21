package com.expensetracker.controllers;

import com.expensetracker.models.Expense;
import com.expensetracker.services.ExpenseService;
import com.expensetracker.utilities.CurrencyFormatter;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller for the Add / Edit Expense modal dialog.
 */
public class ExpenseDialogController {

    @FXML private Label lblDialogTitle;
    @FXML private TextField txtTitle;
    @FXML private TextField txtAmount;
    @FXML private ComboBox<String> comboCategory;
    @FXML private DatePicker dpDate;
    @FXML private TextArea txtDescription;
    @FXML private Label lblError;
    @FXML private Button btnSave;
    @FXML private Button btnCancel;

    private ExpenseService expenseService;
    private Expense existingExpense;
    private boolean saved = false;

    @FXML
    public void initialize() {
        dpDate.setValue(LocalDate.now());
    }

    public void setExpenseService(ExpenseService service) {
        this.expenseService = service;
        loadCategories();
    }

    private void loadCategories() {
        if (expenseService != null) {
            List<String> categories = expenseService.getDistinctCategories();
            comboCategory.setItems(FXCollections.observableArrayList(categories));
        } else {
            comboCategory.setItems(FXCollections.observableArrayList(
                    "Food", "Travel", "Housing", "Utilities", "Entertainment", "Health", "Shopping", "Other"
            ));
        }
        comboCategory.setEditable(true);
    }

    public void setExpenseForEdit(Expense expense) {
        this.existingExpense = expense;
        lblDialogTitle.setText("Edit Expense");
        btnSave.setText("Update Expense");

        txtTitle.setText(expense.getTitle());
        txtAmount.setText(String.format("%.2f", expense.getAmount()));
        comboCategory.setValue(expense.getCategory());
        txtDescription.setText(expense.getDescription());

        if (expense.getDate() != null && !expense.getDate().isEmpty()) {
            try {
                dpDate.setValue(LocalDate.parse(expense.getDate()));
            } catch (Exception e) {
                dpDate.setValue(LocalDate.now());
            }
        }
    }

    @FXML
    private void handleSave() {
        lblError.setVisible(false);

        String title = txtTitle.getText();
        if (title == null || title.trim().isEmpty()) {
            showError("Please enter an expense title.");
            return;
        }

        String amountText = txtAmount.getText();
        double amount;
        try {
            amount = CurrencyFormatter.parseAmount(amountText);
            if (amount <= 0) {
                showError("Amount must be greater than zero.");
                return;
            }
        } catch (Exception e) {
            showError("Invalid amount. Please enter a valid number (e.g. 500 or 1,500.50).");
            return;
        }

        String category = comboCategory.getValue();
        if (category == null || category.trim().isEmpty()) {
            showError("Please select or enter a category.");
            return;
        }

        LocalDate date = dpDate.getValue();
        if (date == null) {
            showError("Please select a date.");
            return;
        }

        String description = (txtDescription.getText() != null) ? txtDescription.getText().trim() : "";

        if (existingExpense == null) {
            // Add new expense
            Expense newExpense = new Expense(title.trim(), amount, category.trim(), date.toString(), description);
            expenseService.addExpense(newExpense);
        } else {
            // Update existing expense
            existingExpense.setTitle(title.trim());
            existingExpense.setAmount(amount);
            existingExpense.setCategory(category.trim());
            existingExpense.setDate(date.toString());
            existingExpense.setDescription(description);
            expenseService.updateExpense(existingExpense);
        }

        saved = true;
        closeDialog();
    }

    @FXML
    private void handleCancel() {
        closeDialog();
    }

    private void showError(String msg) {
        lblError.setText(msg);
        lblError.setVisible(true);
    }

    private void closeDialog() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    public boolean isSaved() {
        return saved;
    }
}
