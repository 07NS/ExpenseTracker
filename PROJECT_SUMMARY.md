# Project Specification & Comprehensive Summary: JavaFX Expense Tracker (ExpenseInsight Engine)

> **Target Audience / Purpose**: This document provides an exhaustive, structured blueprint of the **Java Expense Insight Engine** application. It is engineered specifically so that downstream AI agents, LLMs, and automated documentation generators can ingest it to produce:
> 1. A formal college **Academic Project Report** (Title page, Abstract, System Architecture, Database Schema, Implementation, Testing, and Conclusion).
> 2. A 10–12 slide **PowerPoint Presentation (PPT)** complete with slide outlines, bullet points, talking points, and speaker notes.

---

## 1. Executive Summary & Project Metadata

* **Project Title**: ExpenseInsight — Personal Finance & Expense Analytics System
* **Academic Level**: Undergraduate / College Java & Software Engineering Project
* **Language & Runtime**: Java 21 LTS (OpenJDK 21.0.8)
* **GUI Framework**: JavaFX 21.0.2 (Declarative FXML + CSS styling)
* **Database Engine**: SQLite 3 (Driver: `sqlite-jdbc` 3.45.1.0)
* **Build & Dependency Tool**: Apache Maven (configured with `mvnw.cmd` wrapper)
* **Repository**: [https://github.com/07NS/ExpenseTracker](https://github.com/07NS/ExpenseTracker)
* **Architecture Style**: 3-Tier Layered Architecture (MVC with Service & DAO abstraction)
* **Domain / Currency**: Indian Rupee (INR - ₹) with Indian numbering grouping (Lakhs and Crores: `₹X,XX,XXX.XX`)

---

## 2. Problem Statement & Objectives

### Problem Statement
Managing personal daily finances with spreadsheets or manual paper ledgers leads to data entry friction, lack of data validation, difficulty in calculating category-wise trends, and zero automated visual insights into monthly spending.

### Objectives
1. **Persistent Local Storage**: Maintain all transactional data locally and securely using an embedded SQLite database.
2. **Interactive Desktop GUI**: Provide an ergonomic, dashboard-driven user interface replacing legacy CLI interactions with JavaFX.
3. **Real-time Analytics**: Automatically calculate metrics (Total Expenses, Current Month Spending, Largest Single Outlay) and render interactive visual charts (Pie Charts, Bar Charts).
4. **Data Integrity & Security**: Prevent SQL injection via parameterized PreparedStatements and enforce strict input validation for amounts, categories, and dates.
5. **Academic Elegance**: Demonstrate core Object-Oriented Programming (OOP) concepts (Inheritance, Polymorphism, Encapsulation, Abstraction) and standard software engineering practices.

---

## 3. System Architecture & Design Pattern

The application is engineered using a clean **3-Tier Layered Architecture** ensuring complete separation of concerns:

```
+-------------------------------------------------------------+
|        1. Presentation Layer (JavaFX + FXML + CSS)          |
|  - MainLayout.fxml / Sidebar Navigation                     |
|  - DashboardController / Dashboard.fxml (KPIs & Pie Chart)  |
|  - TransactionsController / Transactions.fxml (Ledger)      |
|  - ReportsController / Reports.fxml (Bar Chart & Tables)    |
|  - ExpenseDialogController / ExpenseDialog.fxml (Modal)     |
+------------------------------+------------------------------+
                               | calls
+------------------------------v------------------------------+
|             2. Service & Business Logic Layer               |
|  - ExpenseService (Interface defining contract)             |
|  - ExpenseServiceImpl (JDBC implementation & business logic)|
|  - CurrencyFormatter (Indian Lakhs/Crores grouping engine)  |
+------------------------------+------------------------------+
                               | interacts with
+------------------------------v------------------------------+
|                3. Data Model Layer (Entities)               |
|  - Expense (Core JavaBean domain model)                     |
|  - FoodExpense & TravelExpense (OOP Subclasses)             |
|  - CategorySummary & MonthlySummary (DTOs for Reports)      |
+------------------------------+------------------------------+
                               | persists to
+------------------------------v------------------------------+
|             4. Database & Persistence Layer                 |
|  - DBConnection (JDBC Connection Manager & Initializer)     |
|  - SQLite Database (`expenses.db`)                          |
+-------------------------------------------------------------+
```

---

## 4. Database Schema & Data Dictionary

### SQLite Database File: `expenses.db`

#### Table Definition: `expenses`
```sql
CREATE TABLE IF NOT EXISTS expenses (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    amount REAL NOT NULL,
    category TEXT NOT NULL,
    description TEXT,
    date TEXT NOT NULL
);
```

#### Field Specifications:
| Column Name | Data Type | Constraint | Description |
| :--- | :--- | :--- | :--- |
| `id` | `INTEGER` | `PRIMARY KEY AUTOINCREMENT` | Unique identifier for each expense record. |
| `title` | `TEXT` | `NOT NULL` | Short descriptive title of the expense (e.g., "Grocery Shopping"). |
| `amount` | `REAL` | `NOT NULL` | Expense value in INR (stored as double precision floating point). |
| `category` | `TEXT` | `NOT NULL` | Spending classification (Food, Travel, Housing, Entertainment, etc.). |
| `description` | `TEXT` | `NULLABLE` | Optional detailed remarks or receipt notes. |
| `date` | `TEXT` | `NOT NULL` | ISO-8601 formatted date string (`YYYY-MM-DD`). |

---

## 5. Core Features & Module Walkthrough

### Module A: Executive Dashboard
* **Dynamic Metric Cards (KPIs)**:
  * *Total Expenses*: Calculated using `SELECT SUM(amount) FROM expenses`.
  * *Current Month Spending*: Filters expenses where `substr(date, 1, 7) = CURRENT_MONTH`.
  * *Highest Expense*: Identified via `ORDER BY amount DESC LIMIT 1`.
* **Recent Activity Feed**: JavaFX `TableView` presenting the latest 6 transactions with custom formatted currency cells.
* **Category Distribution Chart**: Interactive JavaFX `PieChart` showing percentage breakdown of expenditures by category with mouse-hover tooltips.

### Module B: Transaction Ledger & Management
* **Full Data Table**: Sortable `TableView` with columns: ID, Title, Category, Amount (₹), Date, and Description.
* **Live Dynamic Filtering**:
  * Text Search: Instant keyword matching across title and description as the user types.
  * Category Dropdown: Filter by specific category or "All Categories".
  * Date-Range Filter: Two `DatePicker` controls (`From Date` and `To Date`) filtering records chronologically.
* **CRUD Capabilities**:
  * *Add Expense*: Button invoking a modal pop-up.
  * *Edit Expense*: Select row and edit in-place with existing values prefilled.
  * *Delete Expense*: Removes selected transaction after confirming with a native JavaFX alert modal.

### Module C: Reusable Modal Pop-Up Dialog (`ExpenseDialog`)
* Operates as an `APPLICATION_MODAL` window blocking background interaction until saved or cancelled.
* Dual-purpose: Blank form for insertions; pre-populated fields for updates.
* Validation safeguards:
  * Title is mandatory and non-empty.
  * Amount must be positive numeric (`> 0.00`).
  * Category is mandatory.
  * Date must be selected.
  * In-line error message banner displayed upon invalid input.

### Module D: Financial Reports & Analytics
* **Category Breakdown Table**: Tabular view of categories, cumulative amount spent, and percentage share.
* **Monthly Spending Comparison**: Bar chart (`BarChart<String, Number>`) visually comparing historical monthly totals.
* **High-Level Statistics**: Total spend, number of distinct categories tracked, and active billing cycles.

### Module E: Dual-Mode Execution (GUI + CLI)
* **Primary GUI**: Launched via `MainApp.java` (`javafx:run`).
* **Terminal CLI**: Standalone interactive console menu preserved in `cli.Main.java` for headless servers or viva demonstration.

---

## 6. Object-Oriented Principles & Academic Concepts

| Concept | Implementation in Project | Examination Talking Point |
| :--- | :--- | :--- |
| **Encapsulation** | `Expense.java` fields (`id`, `title`, `amount`, etc.) are `private`, exposed via standard public getters/setters. | Ensures data hiding; properties can be bound directly to JavaFX UI. |
| **Inheritance** | `FoodExpense` and `TravelExpense` extend base class `Expense` with specialized constructors calling `super()`. | Code reusability and hierarchical class modeling. |
| **Polymorphism & Abstraction** | Interface `ExpenseService` decoupled from its implementation `ExpenseServiceImpl`. | Loose coupling; allows swapping database drivers or mocking data for tests. |
| **Exception Handling** | Robust try-with-resources (`AutoCloseable` JDBC connections) and `NumberFormatException` catching. | Prevents memory and connection leaks; handles graceful user recovery. |
| **Parameterized Queries** | `PreparedStatement` used in 100% of SQL executions with `?` placeholders. | Complete defense against SQL injection attacks. |
| **Collections Framework** | `ArrayList` for record sets, `LinkedHashMap` for ordered category metrics, `ObservableList` for JavaFX views. | Efficient in-memory data processing and UI synchronization. |

---

## 7. Slide-by-Slide Presentation Blueprint (PPT Guide)

Downstream AI agents can directly transform the following 10-slide outline into PowerPoint presentation slides:

```
Slide 1: Title & Introduction
  - Title: ExpenseInsight • Modern Personal Finance Dashboard
  - Subtitle: A Desktop Financial Management System built with Java 21, JavaFX, and SQLite
  - Presenter: [Student Name / College Details]
  - Visual Note: Clean dark indigo title layout with app logo.

Slide 2: Problem Statement & Motivation
  - Manual financial tracking is prone to calculation errors and data loss.
  - Traditional spreadsheets lack data validation and automated visual reporting.
  - Goal: Develop a secure, local-first personal finance engine with real-time analytics.

Slide 3: Key Objectives
  - Local persistence with zero-configuration SQLite.
  - High-performance, responsive desktop UI built with JavaFX & CSS.
  - Complete transactional CRUD capabilities with multi-criteria search and filters.
  - Visual data reporting via interactive charts (Pie & Bar charts).
  - Indian currency (INR - ₹) formatting using Lakhs & Crores grouping.

Slide 4: System Architecture (3-Tier Design)
  - Presentation Tier: JavaFX FXML Views, CSS theme, Controller handlers.
  - Business Logic Tier: ExpenseService interface and Lakhs/Crores Formatter.
  - Persistence Tier: SQLite embedded database with JDBC PreparedStatement queries.
  - Visual: Include 3-tier architecture diagram.

Slide 5: Database Design & Schema
  - Relational table `expenses` (id, title, amount, category, description, date).
  - Primary Key with auto-increment.
  - Parameterized PreparedStatement usage ensuring 100% SQL injection security.

Slide 6: Core Features Walkthrough - Dashboard & Transactions
  - Executive Dashboard: Real-time KPI summary cards (Total, Monthly, Largest).
  - Transaction Ledger: Full TableView with dynamic search and date filtering.
  - Reusable Modal Dialog: Context-aware Add / Edit expense workflows with validation.

Slide 7: Core Features Walkthrough - Visual Analytics & Reports
  - Category Distribution: JavaFX PieChart with interactive hover tooltips.
  - Monthly Spending Comparison: JavaFX BarChart comparing expenditures across months.
  - Tabular breakdowns of category sums and spending percentages.

Slide 8: Technical Highlights & OOP Implementation
  - Encapsulation: Strict JavaBeans structure for TableView data binding.
  - Inheritance: Base `Expense` extended by `FoodExpense` and `TravelExpense`.
  - Abstraction: Clean separation between Service interfaces and JDBC DAO code.
  - Memory Management: Try-with-resources eliminating database connection leaks.

Slide 9: Testing & Quality Assurance
  - Unit & Integration Verification: Automated test suites in `ServiceTest.java`.
  - FXML View Binding Validation: Headless testing with `FxmlTest.java`.
  - Zero compiler warnings; clean build via Maven wrapper (`mvnw.cmd`).

Slide 10: Conclusion & Future Scope
  - Summary: Built a production-grade, college-friendly Java desktop application.
  - Future Enhancements:
    - PDF / Excel export of financial statements.
    - Monthly budget limits and threshold alert notifications.
    - Cloud synchronization or multi-user authentication.
  - Q&A / Live Demonstration.
```

---

## 8. College Project Report Structure (Document Blueprint)

Downstream AI agents generating a formal thesis or report can follow this standard academic structure:

1. **Chapter 1: Introduction**
   - 1.1 Background & Motivation
   - 1.2 Problem Statement
   - 1.3 Project Scope & Objectives
2. **Chapter 2: Literature Survey / Existing Systems vs Proposed System**
   - 2.1 Limitations of CLI & Manual Spreadsheets
   - 2.2 Advantages of Desktop GUI with Local Relational Persistence
3. **Chapter 3: System Requirements & Technologies**
   - 3.1 Hardware & Operating System Specifications
   - 3.2 Software Environment (Java 21, JavaFX 21, SQLite JDBC, Maven)
4. **Chapter 4: System Design & Architecture**
   - 4.1 Layered Architecture Overview
   - 4.2 Class Hierarchy & UML Class Diagrams
   - 4.3 Database Schema & Entity-Relationship (ER) Design
   - 4.4 Data Flow Diagrams (DFD Level 0 & Level 1)
5. **Chapter 5: Implementation Details**
   - 5.1 SQLite Connection Management (`DBConnection`)
   - 5.2 CRUD & Aggregation Logic (`ExpenseServiceImpl`)
   - 5.3 Indian Numbering System Formatter Algorithm (`CurrencyFormatter`)
   - 5.4 JavaFX UI Layouts and Controller Event Binding
6. **Chapter 6: Testing, Results & Screenshots**
   - 6.1 Test Case Matrix (Unit, Integration, and GUI Tests)
   - 6.2 Test Execution Output (`ServiceTest` & `FxmlTest`)
   - 6.3 Application Screenshots (Dashboard, Ledger, Dialog, Analytics)
7. **Chapter 7: Conclusion & Future Work**
   - 7.1 Summary of Contributions
   - 7.2 Prospective Enhancements
8. **References & Appendix (Source Code Listings)**

---

## 9. Verification & Execution Reference

```powershell
# 1. Run Automated Backend & JDBC Tests
.\mvnw.cmd test-compile exec:java -Dexec.mainClass="com.expensetracker.ServiceTest" -Dexec.classpathScope=test

# 2. Run Headless FXML View Validation Tests
.\mvnw.cmd test-compile exec:java -Dexec.mainClass="com.expensetracker.FxmlTest" -Dexec.classpathScope=test

# 3. Launch Full JavaFX Graphical Interface
.\mvnw.cmd javafx:run

# 4. Launch Standalone Command-Line Terminal Interface
.\mvnw.cmd compile exec:java -Dexec.mainClass="com.expensetracker.cli.Main"
```
