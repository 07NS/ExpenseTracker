# 📊 Java Expense Insight Engine (v2.0)

A modern desktop personal finance management system and executive analytics dashboard built with **Java 21**, **JavaFX**, and **SQLite**. 

The system follows a clean **3-Tier Service-Oriented Architecture** (Presentation / FXML Controllers $\rightarrow$ Service Layer $\rightarrow$ Persistence) and adheres to SOLID design principles.

---

## ✨ Features

### 1. 📊 Executive Dashboard
* **KPI Metrics**: Real-time cards showing **Total Expenses**, **Current Month Spending**, and **Highest Expense** calculated directly from SQLite records.
* **Recent Transactions**: Instant snapshot table of the latest transactions.
* **Interactive Pie Chart**: Visual breakdown of expenses across categories.

### 2. 💳 Transaction Management
* **Comprehensive TableView**: Sortable table displaying ID, Title, Category, Amount (₹), Date, and Description.
* **Search & Multi-Filter**: Real-time search by title or description keyword, category dropdown filter, and date-range pickers (`From` / `To`).
* **CRUD Operations**:
  * **➕ Add Expense**: Opens a reusable modal pop-up dialog.
  * **✏️ Edit Expense**: Select an expense and update any field via the modal dialog.
  * **🗑️ Delete Expense**: Remove an expense with a built-in safety confirmation dialog.

### 3. 📝 Reusable Modal Dialog
* Reusable form modal for both **Add** and **Edit** flows.
* Input validation for required fields, positive amounts, category selection, and valid ISO dates.
* Seamlessly updates both the database and active UI views upon saving.

### 4. 📈 Reports & Analytics
* **Category Breakdown Table**: Category totals with dynamic percentage share calculations.
* **Monthly Spending History**: Aggregate spending grouped chronologically by month (`YYYY-MM`).
* **Charts**:
  * **Pie Chart**: Visual distribution of category spending (with Indian currency formatting).
  * **Bar Chart**: Month-by-month spending comparison in ₹ (Lakhs & Crores grouping).

---

## 🏗️ Architecture & Project Structure

```
├── pom.xml                               # Maven project configuration & JavaFX dependencies
├── mvnw.cmd                              # Maven wrapper executable
├── expenses.db                           # Embedded SQLite database
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/expensetracker/
    │   │       ├── MainApp.java          # JavaFX Application entry point
    │   │       ├── cli/
    │   │       │   └── Main.java         # Optional CLI terminal mode
    │   │       ├── controllers/
    │   │       │   ├── MainController.java
    │   │       │   ├── DashboardController.java
    │   │       │   ├── TransactionsController.java
    │   │       │   ├── ExpenseDialogController.java
    │   │       │   └── ReportsController.java
    │   │       ├── models/
    │   │       │   ├── Expense.java
    │   │       │   ├── FoodExpense.java
    │   │       │   ├── TravelExpense.java
    │   │       │   ├── CategorySummary.java
    │   │       │   └── MonthlySummary.java
    │   │       ├── services/
    │   │       │   ├── ExpenseService.java
    │   │       │   └── ExpenseServiceImpl.java
    │   │       └── utilities/
    │   │           └── DBConnection.java
    │   └── resources/
    │       └── com/expensetracker/
    │           ├── views/
    │           │   ├── MainLayout.fxml
    │           │   ├── Dashboard.fxml
    │           │   ├── Transactions.fxml
    │           │   ├── ExpenseDialog.fxml
    │           │   └── Reports.fxml
    │           └── styles/
    │               └── style.css         # Modern slate & indigo theme stylesheet
    └── test/
        └── java/com/expensetracker/
            ├── ServiceTest.java          # Database & CRUD integration tests
            └── FxmlTest.java             # Headless FXML view binding verification
```

---

## 🚀 How to Run

### Prerequisites
* Java JDK 21 (or 17+) installed.

### 1. Launch the JavaFX GUI Dashboard (Recommended)
Run using the included Maven wrapper from the project directory:

```powershell
.\mvnw.cmd javafx:run
```

*(Or if you have `mvn` installed globally, simply run `mvn javafx:run`)*

### 2. Run in CLI Terminal Mode (Optional)
If you wish to use the terminal interface without a window:

```powershell
.\mvnw.cmd exec:java "-Dexec.mainClass=com.expensetracker.cli.Main"
```

### 3. Run Automated Tests
```powershell
.\mvnw.cmd test
```
