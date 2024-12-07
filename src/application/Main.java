package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene; 
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.Date;
import java.time.LocalDate;



public class Main extends Application {

    private Stage primaryStage;
    private DatabaseHelper dbHelper;
    private TextField accountNameField;
    private DatePicker openingDatePicker;
    private TextField openingBalanceField;
    private TableView<Account> accountTable;
    private TableView<ScheduledTransaction> scheduledTransactionsTable;
    private TableView<Transaction> transactionsTable;

    // Adnan added-modified-start transaction fields
    private ComboBox<String> accountComboBox;
    private ComboBox<String> transactionTypeComboBox;
    private DatePicker transactionDatePicker;
    private TextField transactionDescriptionField;
    private TextField paymentAmountField;
    private TextField depositAmountField;
    private TextField scheduledNameField;
    private ComboBox<String> frequencyComboBox;
    private TextField dueDateField;
    private TableView<ScheduledTransaction> dueTodayTransactionsTable;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        dbHelper = new DatabaseHelper();
        primaryStage.setTitle("Centsible Banking App");
        primaryStage.setScene(createHomeScene());
        primaryStage.show();
    }


// Adnan added-modified-start (12-03-2024)
private Scene createTransactionTypeReportScene() {
    VBox reportLayout = new VBox(20);
    reportLayout.setPadding(new Insets(20));
    reportLayout.setStyle("-fx-background-color: white;");

    Label reportLabel = new Label("Transaction Type Report");
    reportLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: #1e4b35; -fx-font-weight: bold;");

    ComboBox<String> typeComboBox = new ComboBox<>();
    typeComboBox.getItems().addAll(dbHelper.getAllTransactionTypes());
    typeComboBox.setPromptText("Select Transaction Type");
    typeComboBox.setStyle("-fx-background-color: #cbdfd6;");

    TableView<Transaction> reportTable = new TableView<>();
    setupTransactionsReportTable(reportTable, false, true);

    typeComboBox.setOnAction(e -> {
        String selectedType = typeComboBox.getValue();
        if (selectedType != null) {
            reportTable.getItems().clear();
            reportTable.getItems().addAll(dbHelper.getTransactionsByType(selectedType));
        }
    });

    reportTable.setOnMouseClicked(event -> {
        if (event.getClickCount() == 1) {
            Transaction selectedTransaction = reportTable.getSelectionModel().getSelectedItem();
            if (selectedTransaction != null) {
                primaryStage.setScene(createTransactionDetailsScene(selectedTransaction, 
                    () -> primaryStage.setScene(createTransactionTypeReportScene())));
            }
        }
    });

    Button backButton = createStyledButton("Back", () -> primaryStage.setScene(createHomeScene()));
    
    reportLayout.getChildren().addAll(backButton, reportLabel, typeComboBox, reportTable);
    return new Scene(reportLayout, 820, 640);
}

private Scene createAccountReportScene() {
    VBox reportLayout = new VBox(20);
    reportLayout.setPadding(new Insets(20));
    reportLayout.setStyle("-fx-background-color: white;");

    Label reportLabel = new Label("Account Transaction Report");
    reportLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: #1e4b35; -fx-font-weight: bold;");

    ComboBox<String> accountComboBox = new ComboBox<>();
    accountComboBox.getItems().addAll(dbHelper.getAllAccountNames());
    accountComboBox.setPromptText("Select Account");
    accountComboBox.setStyle("-fx-background-color: #cbdfd6;");

    TableView<Transaction> reportTable = new TableView<>();
    setupTransactionsReportTable(reportTable, true, false);

    accountComboBox.setOnAction(e -> {
        String selectedAccount = accountComboBox.getValue();
        if (selectedAccount != null) {
            reportTable.getItems().clear();
            reportTable.getItems().addAll(dbHelper.getTransactionsByAccount(selectedAccount));
        }
    });

    reportTable.setOnMouseClicked(event -> {
        if (event.getClickCount() == 1) {
            Transaction selectedTransaction = reportTable.getSelectionModel().getSelectedItem();
            if (selectedTransaction != null) {
                primaryStage.setScene(createTransactionDetailsScene(selectedTransaction, 
                    () -> primaryStage.setScene(createAccountReportScene())));
            }
        }
    });

    Button backButton = createStyledButton("Back", () -> primaryStage.setScene(createHomeScene()));
    
    reportLayout.getChildren().addAll(backButton, reportLabel, accountComboBox, reportTable);
    return new Scene(reportLayout, 820, 640);
}

private Scene createTransactionDetailsScene(Transaction transaction, Runnable onBack) {
    VBox detailsLayout = new VBox(20);
    detailsLayout.setPadding(new Insets(20));
    detailsLayout.setStyle("-fx-background-color: white;");

    Label detailsLabel = new Label("Transaction Details");
    detailsLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: #1e4b35; -fx-font-weight: bold;");

    GridPane detailsGrid = new GridPane();
    detailsGrid.setHgap(10);
    detailsGrid.setVgap(10);
    detailsGrid.setPadding(new Insets(20));

    // Add read-only fields
    addReadOnlyField(detailsGrid, "Account Name:", transaction.getAccountName(), 0);
    addReadOnlyField(detailsGrid, "Transaction Type:", transaction.getTransactionType(), 1);
    addReadOnlyField(detailsGrid, "Date:", transaction.getTransactionDate().toString(), 2);
    addReadOnlyField(detailsGrid, "Description:", transaction.getDescription(), 3);
    addReadOnlyField(detailsGrid, "Payment Amount:", String.valueOf(transaction.getPaymentAmount()), 4);
    addReadOnlyField(detailsGrid, "Deposit Amount:", String.valueOf(transaction.getDepositAmount()), 5);

    Button backButton = createStyledButton("Back", onBack);
    
    detailsLayout.getChildren().addAll(backButton, detailsLabel, detailsGrid);
    return new Scene(detailsLayout, 820, 640);
}

private void addReadOnlyField(GridPane grid, String label, String value, int row) {
    Label fieldLabel = new Label(label);
    TextField fieldValue = new TextField(value);
    fieldValue.setEditable(false);
    fieldValue.setStyle("-fx-background-color: #f0f0f0;");
    grid.add(fieldLabel, 0, row);
    grid.add(fieldValue, 1, row);
}

private Button createStyledButton(String text, Runnable action) {
    Button button = new Button(text);
    String buttonStyle = "-fx-background-color: #cbdfd6;";
    String hoverStyle = "-fx-background-color: #749485; -fx-text-fill: white;";
    button.setStyle(buttonStyle);
    button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
    button.setOnMouseExited(e -> button.setStyle(buttonStyle));
    button.setOnAction(e -> action.run());
    return button;
}

private void setupTransactionsReportTable(TableView<Transaction> table, boolean hideAccountName, boolean hideTransactionType) {
    if (!hideAccountName) {
        TableColumn<Transaction, String> accountNameColumn = new TableColumn<>("Account Name");
        accountNameColumn.setCellValueFactory(new PropertyValueFactory<>("accountName"));
        table.getColumns().add(accountNameColumn);
    }

    if (!hideTransactionType) {
        TableColumn<Transaction, String> transactionTypeColumn = new TableColumn<>("Transaction Type");
        transactionTypeColumn.setCellValueFactory(new PropertyValueFactory<>("transactionType"));
        table.getColumns().add(transactionTypeColumn);
    }
    
    TableColumn<Transaction, Date> dateColumn = new TableColumn<>("Date");
    dateColumn.setCellValueFactory(new PropertyValueFactory<>("transactionDate"));
    
    TableColumn<Transaction, String> descriptionColumn = new TableColumn<>("Description");
    descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
    
    TableColumn<Transaction, Double> paymentColumn = new TableColumn<>("Payment Amount");
    paymentColumn.setCellValueFactory(new PropertyValueFactory<>("paymentAmount"));
    
    TableColumn<Transaction, Double> depositColumn = new TableColumn<>("Deposit Amount");
    depositColumn.setCellValueFactory(new PropertyValueFactory<>("depositAmount"));

    table.getColumns().addAll(
        dateColumn, descriptionColumn,
        paymentColumn, depositColumn
    );
} // Adnan added-modified-end (12-03-2024)

    
    private Scene createHomeScene() {
        VBox homeLayout = new VBox(10);
        homeLayout.setPadding(new Insets(20));
        homeLayout.setStyle("-fx-background-color: white;");

        MenuBar menuBar = new MenuBar();
        menuBar.setStyle("-fx-background-color: #749485;");
        Menu pagesMenu = new Menu("Pages");
        Menu actionsMenu = new Menu("Actions");

        // Create MenuItems
        MenuItem viewTransactionsMenuItem = new MenuItem("View Transactions");
        viewTransactionsMenuItem.setOnAction(e -> primaryStage.setScene(createTransactionsScene()));

        MenuItem viewScheduledTransactionsMenuItem = new MenuItem("View Scheduled Transactions");
        viewScheduledTransactionsMenuItem.setOnAction(e -> primaryStage.setScene(createScheduledTransactionsScene()));

        MenuItem AddTransactionTypeMenuItem = new MenuItem("Add Transaction Type");
        AddTransactionTypeMenuItem.setOnAction(e -> primaryStage.setScene(createAddTransactionTypeScene()));

        MenuItem CreateNewTransactionMenuItem = new MenuItem("Create New Transaction");
        CreateNewTransactionMenuItem.setOnAction(e -> primaryStage.setScene(createEnterTransactionsScene()));

        MenuItem CreateNewScheduledTransactionMenuItem = new MenuItem("Create New Scheduled Transaction");
        CreateNewScheduledTransactionMenuItem.setOnAction(e -> primaryStage.setScene(createEnterScheduledTransactionsScene()));

        //Adnan added-modified-start
        // Search Transactions menu item
        MenuItem searchTransactionsMenuItem = new MenuItem("Search or Edit Transactions");
        searchTransactionsMenuItem.setOnAction(e -> primaryStage.setScene(createSearchTransactionsScene()));
        actionsMenu.getItems().add(searchTransactionsMenuItem);
        //Adnan added-modified-end

        //Adnan added-modified-start
        // Search Scheduled Transactions menu item
        MenuItem searchScheduledTransactionsMenuItem = new MenuItem("Search or Edit Scheduled Transactions");
        searchScheduledTransactionsMenuItem.setOnAction(e -> 
        primaryStage.setScene(createSearchScheduledTransactionsScene()));
        actionsMenu.getItems().add(searchScheduledTransactionsMenuItem);
        //Adnan added-modified-end

        
        //Adnan added-modified-start (12-03-2024)
        MenuItem viewTransactionTypeReportMenuItem = new MenuItem("Transaction Type Report");
        viewTransactionTypeReportMenuItem.setOnAction(e -> primaryStage.setScene(createTransactionTypeReportScene()));
        MenuItem viewAccountReportMenuItem = new MenuItem("Account Transaction Report");
        viewAccountReportMenuItem.setOnAction(e -> primaryStage.setScene(createAccountReportScene()));
        //Adnan added-modified-end (12-03-2024)

        
        //Adnan added-modified-start (12-03-2024)
        pagesMenu.getItems().addAll(viewTransactionTypeReportMenuItem, viewAccountReportMenuItem);
        //Adnan added-modified-end (12-03-2024)

        
        // Add MenuItems to the Menu
        pagesMenu.getItems().addAll(viewTransactionsMenuItem, viewScheduledTransactionsMenuItem);
        actionsMenu.getItems().addAll(AddTransactionTypeMenuItem, CreateNewTransactionMenuItem, CreateNewScheduledTransactionMenuItem);

        // Add the Menu to the MenuBar
        menuBar.getMenus().add(actionsMenu);
        menuBar.getMenus().add(pagesMenu);

        setupDueTodayTransactionsTable();
        refreshDueTodayTransactionsTable();

        Label homePageLabel = new Label("Home Page");
        homePageLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: #1e4b35 ; -fx-font-weight: bold;");

        Label dueTodayLabel = new Label("Scheduled Transactions Due Today:");
        dueTodayLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #1e4b35 ;");

        Label yourAccountsLabel = new Label("Your Accounts:");
        dueTodayLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #1e4b35 ;");

        accountTable = new TableView<>(); // Initialize the TableView
        setupAccountTable();
        refreshAccountTable(); // Populate the table with account details

        // Define button styles
        String buttonStyle = "-fx-background-color: #cbdfd6;";
        String hoverStyle = "-fx-background-color: #749485; -fx-text-fill: white;";

        Button createAccountButton = new Button("Create Account");
        createAccountButton.setStyle(buttonStyle);
        createAccountButton.setOnAction(e -> primaryStage.setScene(createCreateAccountScene()));
        createAccountButton.setOnMouseEntered(e -> createAccountButton.setStyle(hoverStyle));
        createAccountButton.setOnMouseExited(e -> createAccountButton.setStyle(buttonStyle));

        Button deleteAccountButton = new Button("Delete Selected Account");
        deleteAccountButton.setStyle(buttonStyle);
        deleteAccountButton.setOnMouseEntered(e -> deleteAccountButton.setStyle(hoverStyle));
        deleteAccountButton.setOnMouseExited(e -> deleteAccountButton.setStyle(buttonStyle));
        deleteAccountButton.setOnAction(e -> deleteSelectedAccount());

        // Place buttons in an HBox for horizontal layout
        HBox buttonLayout = new HBox(15);
        buttonLayout.setAlignment(Pos.CENTER);
        buttonLayout.getChildren().addAll(createAccountButton, deleteAccountButton);

        // Add a spacer to create space between the account table and buttons
        Region spacer = new Region();
        spacer.setMinHeight(20);


        homeLayout.getChildren().addAll(menuBar, homePageLabel, dueTodayLabel, dueTodayTransactionsTable, yourAccountsLabel,
                accountTable, spacer, buttonLayout);

        homeLayout.setAlignment(Pos.TOP_CENTER);

        return new Scene(homeLayout, 820, 640);
    }

    private void setupAccountTable() {
        TableColumn<Account, String> nameColumn = new TableColumn<>("Account Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Account, Date> dateColumn = new TableColumn<>("Opening Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("openingDate"));
        dateColumn.setStyle("-fx-text-fill: #1e4b35;");

        TableColumn<Account, Double> balanceColumn = new TableColumn<>("Opening Balance");
        balanceColumn.setCellValueFactory(new PropertyValueFactory<>("openingBalance"));
        balanceColumn.setStyle("-fx-text-fill: #1e4b35;");

        accountTable.getColumns().addAll(nameColumn, dateColumn, balanceColumn);
    }

    private void setupScheduledTransactionsTable() {
        scheduledTransactionsTable = new TableView<>();

        TableColumn<ScheduledTransaction, String> scheduleNameColumn = new TableColumn<>("Schedule Name");
        scheduleNameColumn.setCellValueFactory(new PropertyValueFactory<>("scheduleName"));

        TableColumn<ScheduledTransaction, String> accountNameColumn = new TableColumn<>("Account Name");
        accountNameColumn.setCellValueFactory(new PropertyValueFactory<>("accountName"));

        TableColumn<ScheduledTransaction, String> transactionTypeColumn = new TableColumn<>("Transaction Type");
        transactionTypeColumn.setCellValueFactory(new PropertyValueFactory<>("transactionType"));

        TableColumn<ScheduledTransaction, String> frequencyColumn = new TableColumn<>("Frequency");
        frequencyColumn.setCellValueFactory(new PropertyValueFactory<>("frequency"));

        TableColumn<ScheduledTransaction, Integer> dueDateColumn = new TableColumn<>("Due Date");
        dueDateColumn.setCellValueFactory(new PropertyValueFactory<>("dueDate"));

        TableColumn<ScheduledTransaction, Double> paymentAmountColumn = new TableColumn<>("Payment Amount");
        paymentAmountColumn.setCellValueFactory(new PropertyValueFactory<>("paymentAmount"));

        // Add columns to the TableView
        scheduledTransactionsTable.getColumns().addAll(scheduleNameColumn, accountNameColumn,
                transactionTypeColumn, frequencyColumn, dueDateColumn, paymentAmountColumn);
    }

    private void setupScheduledTransactionsSearchResultsTable(TableView<ScheduledTransaction> table) {
        TableColumn<ScheduledTransaction, String> scheduleNameColumn = new TableColumn<>("Schedule Name");
        scheduleNameColumn.setCellValueFactory(new PropertyValueFactory<>("scheduleName"));

        TableColumn<ScheduledTransaction, String> accountNameColumn = new TableColumn<>("Account Name");
        accountNameColumn.setCellValueFactory(new PropertyValueFactory<>("accountName"));

        TableColumn<ScheduledTransaction, String> transactionTypeColumn = new TableColumn<>("Transaction Type");
        transactionTypeColumn.setCellValueFactory(new PropertyValueFactory<>("transactionType"));

        TableColumn<ScheduledTransaction, String> frequencyColumn = new TableColumn<>("Frequency");
        frequencyColumn.setCellValueFactory(new PropertyValueFactory<>("frequency"));

        TableColumn<ScheduledTransaction, Integer> dueDateColumn = new TableColumn<>("Due Date");
        dueDateColumn.setCellValueFactory(new PropertyValueFactory<>("dueDate"));

        TableColumn<ScheduledTransaction, Double> paymentAmountColumn = new TableColumn<>("Payment Amount");
        paymentAmountColumn.setCellValueFactory(new PropertyValueFactory<>("paymentAmount"));

        // Add columns to the TableView
        table.getColumns().addAll(scheduleNameColumn, accountNameColumn,
                transactionTypeColumn, frequencyColumn, dueDateColumn, paymentAmountColumn);
    }

    private void setupTransactionsTable() {
        transactionsTable = new TableView<>();
        TableColumn<Transaction, String> accountNameColumn = new TableColumn<>("Account Name");
        accountNameColumn.setCellValueFactory(new PropertyValueFactory<>("accountName"));

        TableColumn<Transaction, String> transactionTypeColumn = new TableColumn<>("Transaction Type");
        transactionTypeColumn.setCellValueFactory(new PropertyValueFactory<>("transactionType"));

        TableColumn<Transaction, String> transactionDateColumn = new TableColumn<>("Transaction Date");
        transactionDateColumn.setCellValueFactory(new PropertyValueFactory<>("transactionDate"));

        TableColumn<Transaction, String> transactionDescriptionColumn = new TableColumn<>("Transaction Description");
        transactionDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<Transaction, Double> paymentAmountColumn = new TableColumn<>("Payment Amount");
        paymentAmountColumn.setCellValueFactory(new PropertyValueFactory<>("paymentAmount"));

        TableColumn<Transaction, Double> depositAmountColumn = new TableColumn<>("Deposit Amount");
        depositAmountColumn.setCellValueFactory(new PropertyValueFactory<>("depositAmount"));

        // Add columns to the TableView
        transactionsTable.getColumns().addAll(accountNameColumn,
                transactionTypeColumn, transactionDateColumn, transactionDescriptionColumn, paymentAmountColumn, depositAmountColumn);
    }

    private void setupTransactionsSearchResultsTable(TableView<Transaction> table) {
        TableColumn<Transaction, String> accountNameColumn = new TableColumn<>("Account Name");
        accountNameColumn.setCellValueFactory(new PropertyValueFactory<>("accountName"));

        TableColumn<Transaction, String> transactionTypeColumn = new TableColumn<>("Transaction Type");
        transactionTypeColumn.setCellValueFactory(new PropertyValueFactory<>("transactionType"));

        TableColumn<Transaction, String> transactionDateColumn = new TableColumn<>("Transaction Date");
        transactionDateColumn.setCellValueFactory(new PropertyValueFactory<>("transactionDate"));

        TableColumn<Transaction, String> transactionDescriptionColumn = new TableColumn<>("Transaction Description");
        transactionDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<Transaction, Double> paymentAmountColumn = new TableColumn<>("Payment Amount");
        paymentAmountColumn.setCellValueFactory(new PropertyValueFactory<>("paymentAmount"));

        TableColumn<Transaction, Double> depositAmountColumn = new TableColumn<>("Deposit Amount");
        depositAmountColumn.setCellValueFactory(new PropertyValueFactory<>("depositAmount"));

        table.getColumns().setAll(accountNameColumn, transactionTypeColumn, transactionDateColumn,
                transactionDescriptionColumn, paymentAmountColumn, depositAmountColumn);
    }


    private Scene createCreateAccountScene() {
        VBox enterAccountLayout = new VBox(20);
        enterAccountLayout.setPadding(new Insets(20));
        enterAccountLayout.setStyle("-fx-background-color: white;");

        Label enterNewAccountPageLabel = new Label("Create New Account");
        enterNewAccountPageLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: #1e4b35 ; -fx-font-weight: bold;");

        GridPane createAccountPane = new GridPane();
        createAccountPane.setPadding(new Insets(10));
        createAccountPane.setHgap(10);
        createAccountPane.setVgap(20);

        accountNameField = new TextField();
        openingDatePicker = new DatePicker(LocalDate.now());
        openingBalanceField = new TextField();

        // Define button styles
        String buttonStyle = "-fx-background-color: #cbdfd6;";
        String hoverStyle = "-fx-background-color: #749485; -fx-text-fill: white;";

        // Add a "Submit" button to save account details
        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> createAccount());
        submitButton.setStyle(buttonStyle);
        submitButton.setOnMouseEntered(e -> submitButton.setStyle(hoverStyle));
        submitButton.setOnMouseExited(e -> submitButton.setStyle(buttonStyle));

        // Add a "Back" button to return to the home page
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> primaryStage.setScene(createHomeScene()));
        backButton.setStyle(buttonStyle);
        backButton.setOnMouseEntered(e -> backButton.setStyle(hoverStyle));
        backButton.setOnMouseExited(e -> backButton.setStyle(buttonStyle));

        // Add components to the grid
        createAccountPane.add(backButton, 0, 0);
        createAccountPane.add(new Label("Account Name:"), 0, 1);
        createAccountPane.add(accountNameField, 1, 1);
        createAccountPane.add(new Label("Opening Date:"), 0, 2);
        createAccountPane.add(openingDatePicker, 1, 2);
        createAccountPane.add(new Label("Opening Balance:"), 0, 3);
        createAccountPane.add(openingBalanceField, 1, 3);
        createAccountPane.add(submitButton, 1, 4);

        enterAccountLayout.getChildren().addAll(enterNewAccountPageLabel, createAccountPane);
        enterAccountLayout.setAlignment(Pos.TOP_CENTER);
        createAccountPane.setAlignment(Pos.CENTER);

        return new Scene(enterAccountLayout, 820,640);
    }

    private void createAccount() {
        String accountName = accountNameField.getText();
        LocalDate openingDate = openingDatePicker.getValue();
        String openingBalanceText = openingBalanceField.getText();

        // Check if any field is blank
        if (accountName.isEmpty() || openingDate == null || openingBalanceText.isEmpty()) {
            showAlert("Error", "Please fill in all fields.");
            return;
        }

        double openingBalance;

        // Validate the opening balance input
        try {
            openingBalance = Double.parseDouble(openingBalanceText);
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter a valid opening balance.");
            return;
        }

        // Check if the account name already exists
        if (dbHelper.accountExists(accountName)) {
            showAlert("Error", "Account name already exists. Please choose a different name.");
            return;
        }

        // Create the account if the name does not exist
        if (dbHelper.createAccount(accountName, Date.valueOf(openingDate), openingBalance)) {
            showAlert("Success", "Account created successfully!");
            primaryStage.setScene(createHomeScene());
        } else {
            showAlert("Error", "Failed to create account.");
        }
    }

    private void refreshAccountTable() {
        accountTable.getItems().clear();
        // Get account details from the database
        for (Account accountDetail : dbHelper.getAllAccountDetails()) {
            accountTable.getItems().add(accountDetail);
        }
    }

    private void refreshScheduledTransactionsTable() {
        scheduledTransactionsTable.getItems().clear();
        for (ScheduledTransaction scheduledTransactionDetail : dbHelper.getScheduledTransactions()){
            scheduledTransactionsTable.getItems().add(scheduledTransactionDetail);
        }
    }

    private void refreshTransactionsTable() {
        transactionsTable.getItems().clear();
        for (Transaction transactionDetail : dbHelper.getTransactions()){
            transactionsTable.getItems().add(transactionDetail);
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private Scene createAddTransactionTypeScene() {
        VBox enterTransactionTypeLayout = new VBox(20);
        enterTransactionTypeLayout.setPadding(new Insets(20));
        enterTransactionTypeLayout.setStyle("-fx-background-color: white;");

        Label addTransactionTypePageLabel = new Label("Add Transaction Type");
        addTransactionTypePageLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: #1e4b35 ; -fx-font-weight: bold;");

        GridPane addTransactionTypePane = new GridPane();
        addTransactionTypePane.setPadding(new Insets(10));
        addTransactionTypePane.setHgap(10);
        addTransactionTypePane.setVgap(20);

        TextField transactionTypeNameField = new TextField();

        // Define button styles
        String buttonStyle = "-fx-background-color: #cbdfd6;";
        String hoverStyle = "-fx-background-color: #749485; -fx-text-fill: white;";

        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> {
            String transactionTypeName = transactionTypeNameField.getText().trim();

            if (transactionTypeName.isEmpty()) {
                showAlert("Error", "Transaction type name cannot be empty.");
                return;
            }

            if (dbHelper.transactionTypeExists(transactionTypeName)) {
                showAlert("Error", "Transaction type already exists. Please enter a unique name.");
                return;
            }

            if (dbHelper.addTransactionType(transactionTypeName)) {
                showAlert("Success", "Transaction type added successfully!");
                transactionTypeComboBox.getItems().add(transactionTypeName);
                primaryStage.setScene(createEnterTransactionsScene());
            } else {
                showAlert("Error", "Failed to add transaction type.");
            }
        });
        submitButton.setStyle(buttonStyle);
        submitButton.setOnMouseEntered(e -> submitButton.setStyle(hoverStyle));
        submitButton.setOnMouseExited(e -> submitButton.setStyle(buttonStyle));

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> primaryStage.setScene(createHomeScene()));
        backButton.setStyle(buttonStyle);
        backButton.setOnMouseEntered(e -> backButton.setStyle(hoverStyle));
        backButton.setOnMouseExited(e -> backButton.setStyle(buttonStyle));

        addTransactionTypePane.add(backButton, 0, 0);
        addTransactionTypePane.add(new Label("Transaction Type Name:"), 0, 1);
        addTransactionTypePane.add(transactionTypeNameField, 1, 1);
        addTransactionTypePane.add(submitButton, 1, 3);

        enterTransactionTypeLayout.getChildren().addAll(addTransactionTypePageLabel, addTransactionTypePane);
        enterTransactionTypeLayout.setAlignment(Pos.TOP_CENTER);
        addTransactionTypePane.setAlignment(Pos.CENTER);

        return new Scene(enterTransactionTypeLayout, 820, 640);
    }



// Adnan added-modified-start-(rubric #5)
private Scene createSearchTransactionsScene() {
    VBox searchLayout = new VBox(20);
    searchLayout.setPadding(new Insets(20));
    searchLayout.setStyle("-fx-background-color: white;");

    Label searchLabel = new Label("Search Transactions");
    searchLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: #1e4b35; -fx-font-weight: bold;");

    TextField searchField = new TextField();
    searchField.setPromptText("Enter description to search");
    
    Button searchButton = new Button("Search");

    Label instructionLabel = new Label("Single-click any transaction to edit it.");
    instructionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555555;");

    TableView<Transaction> searchResultsTable = new TableView<>();
    setupTransactionsSearchResultsTable(searchResultsTable);

    // Define button styles
    String buttonStyle = "-fx-background-color: #cbdfd6;";
    String hoverStyle = "-fx-background-color: #749485; -fx-text-fill: white;";
    
    searchButton.setOnAction(e -> {
        String searchTerm = searchField.getText().trim();
        searchResultsTable.getItems().clear();
        searchResultsTable.getItems().addAll(dbHelper.searchTransactions(searchTerm));
    });
    searchButton.setStyle(buttonStyle);
    searchButton.setOnMouseEntered(e -> searchButton.setStyle(hoverStyle));
    searchButton.setOnMouseExited(e -> searchButton.setStyle(buttonStyle));
    
    searchResultsTable.setOnMouseClicked(event -> {
        if (event.getClickCount() == 1) { // Single click
            Transaction selectedTransaction = searchResultsTable.getSelectionModel().getSelectedItem();
            if (selectedTransaction != null) {
                primaryStage.setScene(createEditTransactionScene(selectedTransaction));
            }
        }
    });

    Button backButton = new Button("Back");
    backButton.setStyle(buttonStyle);
    backButton.setOnMouseEntered(e -> backButton.setStyle(hoverStyle));
    backButton.setOnMouseExited(e -> backButton.setStyle(buttonStyle));
    backButton.setOnAction(e -> primaryStage.setScene(createHomeScene()));

    searchLayout.getChildren().addAll(backButton, searchLabel, searchField,
                                    searchButton, instructionLabel, searchResultsTable);
    
    return new Scene(searchLayout, 820, 640);
}

private Scene createEditTransactionScene(Transaction transaction) {
    BorderPane rootLayout = new BorderPane();
    rootLayout.setPadding(new Insets(20));
    rootLayout.setStyle("-fx-background-color: white;");

    // Title label
    Label editLabel = new Label("Edit Transaction");
    editLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: #1e4b35; -fx-font-weight: bold;");
    BorderPane.setAlignment(editLabel, Pos.CENTER);

    // Form layout
    GridPane editPane = new GridPane();
    editPane.setHgap(10);
    editPane.setVgap(10);
    editPane.setPadding(new Insets(50, 0, 0, 0));
    editPane.setAlignment(Pos.TOP_CENTER);

    // Define button styles
    String buttonStyle = "-fx-background-color: #cbdfd6;";
    String hoverStyle = "-fx-background-color: #749485; -fx-text-fill: white;";


    // Create and populate fields
    ComboBox<String> accountComboBox = new ComboBox<>();
    accountComboBox.getItems().addAll(dbHelper.getAllAccountNames());
    accountComboBox.setValue(transaction.getAccountName());
    accountComboBox.setStyle("-fx-background-color: #cbdfd6; -fx-text-fill: black;");

    ComboBox<String> typeComboBox = new ComboBox<>();
    typeComboBox.getItems().addAll(dbHelper.getAllTransactionTypes());
    typeComboBox.setValue(transaction.getTransactionType());
    typeComboBox.setStyle("-fx-background-color: #cbdfd6; -fx-text-fill: black;");

    DatePicker datePicker = new DatePicker();
    datePicker.setValue(transaction.getTransactionDate().toLocalDate());

    TextField descriptionField = new TextField(transaction.getDescription());

    TextField paymentField = new TextField(String.valueOf(transaction.getPaymentAmount()));

    TextField depositField = new TextField(String.valueOf(transaction.getDepositAmount()));

    // Add fields to the grid
    editPane.add(new Label("Account:"), 0, 0);
    editPane.add(accountComboBox, 1, 0);
    editPane.add(new Label("Type:"), 0, 1);
    editPane.add(typeComboBox, 1, 1);
    editPane.add(new Label("Date:"), 0, 2);
    editPane.add(datePicker, 1, 2);
    editPane.add(new Label("Description:"), 0, 3);
    editPane.add(descriptionField, 1, 3);
    editPane.add(new Label("Payment Amount:"), 0, 4);
    editPane.add(paymentField, 1, 4);
    editPane.add(new Label("Deposit Amount:"), 0, 5);
    editPane.add(depositField, 1, 5);

    Button saveButton = new Button("Save");
    saveButton.setStyle(buttonStyle);
    saveButton.setOnMouseEntered(e -> saveButton.setStyle(hoverStyle));
    saveButton.setOnMouseExited(e -> saveButton.setStyle(buttonStyle));
    saveButton.setOnAction(e -> {
        // Original values used to find the transaction
        String originalAccountName = transaction.getAccountName();
        Date originalTransactionDate = transaction.getTransactionDate();
        String originalDescription = transaction.getDescription();

        // Save the updated transaction
        if (dbHelper.updateTransaction(
                originalAccountName, // Use original account name for lookup
                originalTransactionDate, // Use original transaction date for lookup
                originalDescription, // Use original description for lookup
                accountComboBox.getValue(),
                typeComboBox.getValue(),
                Date.valueOf(datePicker.getValue()),
                descriptionField.getText(),
                Double.parseDouble(paymentField.getText()),
                Double.parseDouble(depositField.getText()))) {
            showAlert("Success", "Transaction updated successfully!");
            primaryStage.setScene(createSearchTransactionsScene());
        } else {
            showAlert("Error", "Failed to update transaction.");
        }
    });

    Button backButton = new Button("Back");
    backButton.setStyle(buttonStyle);
    backButton.setOnMouseEntered(e -> backButton.setStyle(hoverStyle));
    backButton.setOnMouseExited(e -> backButton.setStyle(buttonStyle));
    backButton.setOnAction(e -> primaryStage.setScene(createSearchTransactionsScene()));

    // Create a VBox to center the back button vertically
    VBox backButtonBox = new VBox(backButton);
    backButtonBox.setAlignment(Pos.TOP_LEFT);
    backButtonBox.setPadding(new Insets(0, 20, 0, 0)); // Padding on the right
    rootLayout.setLeft(backButtonBox);

    // Add components to the root layout
    rootLayout.setTop(editLabel);
    rootLayout.setCenter(editPane);
    rootLayout.setBottom(saveButton);
    BorderPane.setAlignment(saveButton, Pos.CENTER);

    return new Scene(rootLayout, 820, 640);
} //Adnan added-modified-end


    // Adnan added-modified-start-(rubric #6)
private Scene createSearchScheduledTransactionsScene() {
    VBox searchLayout = new VBox(20);
    searchLayout.setPadding(new Insets(20));
    searchLayout.setStyle("-fx-background-color: white;");

    Label searchLabel = new Label("Search Scheduled Transactions");
    searchLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: #1e4b35; -fx-font-weight: bold;");

    Label instructionLabel = new Label("Single-click any scheduled transaction to edit it.");
    instructionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555555;");

    TextField searchField = new TextField();
    searchField.setPromptText("Enter schedule name to search");

    // Define button styles
    String buttonStyle = "-fx-background-color: #cbdfd6;";
    String hoverStyle = "-fx-background-color: #749485; -fx-text-fill: white;";
    
    Button searchButton = new Button("Search");
    searchButton.setStyle("-fx-background-color: #cbdfd6;");
    
    TableView<ScheduledTransaction> searchResultsTable = new TableView<>();
    setupScheduledTransactionsSearchResultsTable(searchResultsTable);

    searchButton.setOnAction(e -> {
        String searchTerm = searchField.getText().trim();
        searchResultsTable.getItems().clear();
        searchResultsTable.getItems().addAll(dbHelper.searchScheduledTransactions(searchTerm));
    });
    searchButton.setStyle(buttonStyle);
    searchButton.setOnMouseEntered(e -> searchButton.setStyle(hoverStyle));
    searchButton.setOnMouseExited(e -> searchButton.setStyle(buttonStyle));

    searchResultsTable.setOnMouseClicked(event -> {
        if (event.getClickCount() == 1) {
            ScheduledTransaction selectedTransaction = 
                searchResultsTable.getSelectionModel().getSelectedItem();
            if (selectedTransaction != null) {
                primaryStage.setScene(createEditScheduledTransactionScene(selectedTransaction));
            }
        }
    });

    Button backButton = new Button("Back");
    backButton.setStyle(buttonStyle);
    backButton.setOnMouseEntered(e -> backButton.setStyle(hoverStyle));
    backButton.setOnMouseExited(e -> backButton.setStyle(buttonStyle));
    backButton.setOnAction(e -> primaryStage.setScene(createHomeScene()));

    searchLayout.getChildren().addAll(backButton, searchLabel, searchField, 
                                    searchButton, instructionLabel, searchResultsTable);
    
    return new Scene(searchLayout, 820, 640);
}

private Scene createEditScheduledTransactionScene(ScheduledTransaction transaction) {
    BorderPane rootLayout = new BorderPane();
    rootLayout.setPadding(new Insets(20));
    rootLayout.setStyle("-fx-background-color: white;");

    // Title label
    Label editLabel = new Label("Edit Scheduled Transaction");
    editLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: #1e4b35; -fx-font-weight: bold;");
    BorderPane.setAlignment(editLabel, Pos.CENTER);

    // Form layout
    GridPane editPane = new GridPane();
    editPane.setHgap(10);
    editPane.setVgap(10);
    editPane.setPadding(new Insets(50, 0, 0, 0));
    editPane.setAlignment(Pos.TOP_CENTER);

    // Define button styles
    String buttonStyle = "-fx-background-color: #cbdfd6;";
    String hoverStyle = "-fx-background-color: #749485; -fx-text-fill: white;";

    TextField scheduleNameField = new TextField(transaction.getScheduleName());
    ComboBox<String> accountComboBox = new ComboBox<>();
    accountComboBox.getItems().addAll(dbHelper.getAllAccountNames());
    accountComboBox.setValue(transaction.getAccountName());
    accountComboBox.setStyle("-fx-background-color: #cbdfd6; -fx-text-fill: black;");

    ComboBox<String> typeComboBox = new ComboBox<>();
    typeComboBox.getItems().addAll(dbHelper.getAllTransactionTypes());
    typeComboBox.setValue(transaction.getTransactionType());
    typeComboBox.setStyle("-fx-background-color: #cbdfd6; -fx-text-fill: black;");

    ComboBox<String> frequencyComboBox = new ComboBox<>();
    frequencyComboBox.getItems().addAll("Monthly", "Weekly", "Yearly");
    frequencyComboBox.setValue(transaction.getFrequency());
    frequencyComboBox.setStyle("-fx-background-color: #cbdfd6; -fx-text-fill: black;");

    TextField dueDateField = new TextField(String.valueOf(transaction.getDueDate()));
    TextField paymentAmountField = new TextField(String.valueOf(transaction.getPaymentAmount()));

    editPane.add(new Label("Schedule Name:"), 0, 0);
    editPane.add(scheduleNameField, 1, 0);
    editPane.add(new Label("Account:"), 0, 1);
    editPane.add(accountComboBox, 1, 1);
    editPane.add(new Label("Type:"), 0, 2);
    editPane.add(typeComboBox, 1, 2);
    editPane.add(new Label("Frequency:"), 0, 3);
    editPane.add(frequencyComboBox, 1, 3);
    editPane.add(new Label("Due Date:"), 0, 4);
    editPane.add(dueDateField, 1, 4);
    editPane.add(new Label("Payment Amount:"), 0, 5);
    editPane.add(paymentAmountField, 1, 5);


    Button saveButton = new Button("Save");
    saveButton.setStyle(buttonStyle);
    saveButton.setOnMouseEntered(e -> saveButton.setStyle(hoverStyle));
    saveButton.setOnMouseExited(e -> saveButton.setStyle(buttonStyle));
    saveButton.setOnAction(e -> {
        if (dbHelper.updateScheduledTransaction(
                transaction.getScheduleName(),
                scheduleNameField.getText(),
                accountComboBox.getValue(),
                typeComboBox.getValue(),
                frequencyComboBox.getValue(),
                dueDateField.getText(),
                Double.parseDouble(paymentAmountField.getText()))) {
            showAlert("Success", "Scheduled transaction updated successfully!");
            primaryStage.setScene(createSearchScheduledTransactionsScene());
        } else {
            showAlert("Error", "Failed to update scheduled transaction.");
        }
    });

    Button backButton = new Button("Back");
    backButton.setStyle(buttonStyle);
    backButton.setOnMouseEntered(e -> backButton.setStyle(hoverStyle));
    backButton.setOnMouseExited(e -> backButton.setStyle(buttonStyle));
    backButton.setOnAction(e -> primaryStage.setScene(createSearchScheduledTransactionsScene()));

    // Create a VBox to center the back button vertically
    VBox backButtonBox = new VBox(backButton);
    backButtonBox.setAlignment(Pos.TOP_LEFT);
    backButtonBox.setPadding(new Insets(0, 20, 0, 0)); // Padding on the right
    rootLayout.setLeft(backButtonBox);

    // Add components to the root layout
    rootLayout.setTop(editLabel);
    rootLayout.setCenter(editPane);
    rootLayout.setBottom(saveButton);
    BorderPane.setAlignment(saveButton, Pos.CENTER);
    
    return new Scene(rootLayout, 820, 640);
} // Adnan added-modified-end

    private Scene createEnterTransactionsScene() {
        VBox enterTransactionLayout = new VBox(20);
        enterTransactionLayout.setPadding(new Insets(20));
        enterTransactionLayout.setStyle("-fx-background-color: white;");

        Label createTransactionPageLabel = new Label("Create New Transaction");
        createTransactionPageLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: #1e4b35 ; -fx-font-weight: bold;");

        GridPane enterTransactionPane = new GridPane();
        enterTransactionPane.setPadding(new Insets(10));
        enterTransactionPane.setHgap(10);
        enterTransactionPane.setVgap(20);

        accountComboBox = new ComboBox<>();
        accountComboBox.getItems().addAll(dbHelper.getAllAccountNames());
        accountComboBox.setStyle("-fx-background-color: #cbdfd6; -fx-text-fill: black;");
        if (!accountComboBox.getItems().isEmpty()) {
            accountComboBox.setValue(accountComboBox.getItems().get(0));
        }

        transactionTypeComboBox = new ComboBox<>();
        transactionTypeComboBox.getItems().addAll(dbHelper.getAllTransactionTypes());
        transactionTypeComboBox.setStyle("-fx-background-color: #cbdfd6; -fx-text-fill: black;");
        transactionTypeComboBox.setValue(transactionTypeComboBox.getItems().isEmpty() ? null : transactionTypeComboBox.getItems().get(0));

        transactionDatePicker = new DatePicker(LocalDate.now());
        transactionDescriptionField = new TextField();
        paymentAmountField = new TextField();
        depositAmountField = new TextField();

        // Define button styles
        String buttonStyle = "-fx-background-color: #cbdfd6;";
        String hoverStyle = "-fx-background-color: #749485; -fx-text-fill: white;";

        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> saveTransaction());
        submitButton.setStyle(buttonStyle);
        submitButton.setOnMouseEntered(e -> submitButton.setStyle(hoverStyle));
        submitButton.setOnMouseExited(e -> submitButton.setStyle(buttonStyle));

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> primaryStage.setScene(createHomeScene()));
        backButton.setStyle(buttonStyle);
        backButton.setOnMouseEntered(e -> backButton.setStyle(hoverStyle));
        backButton.setOnMouseExited(e -> backButton.setStyle(buttonStyle));

        enterTransactionPane.add(backButton, 0, 0);
        enterTransactionPane.add(new Label("Account:"), 0, 1);
        enterTransactionPane.add(accountComboBox, 1, 1);
        enterTransactionPane.add(new Label("Transaction Type:"), 0, 2);
        enterTransactionPane.add(transactionTypeComboBox, 1, 2);
        enterTransactionPane.add(new Label("Transaction Date:"), 0, 3);
        enterTransactionPane.add(transactionDatePicker, 1, 3);
        enterTransactionPane.add(new Label("Transaction Description:"), 0, 4);
        enterTransactionPane.add(transactionDescriptionField, 1, 4);
        enterTransactionPane.add(new Label("Payment Amount:"), 0, 5);
        enterTransactionPane.add(paymentAmountField, 1, 5);
        enterTransactionPane.add(new Label("Deposit Amount:"), 0, 6);
        enterTransactionPane.add(depositAmountField, 1, 6);
        enterTransactionPane.add(submitButton, 1, 7);

        enterTransactionLayout.getChildren().addAll(createTransactionPageLabel, enterTransactionPane);
        enterTransactionLayout.setAlignment(Pos.TOP_CENTER);
        enterTransactionPane.setAlignment(Pos.CENTER);

        return new Scene(enterTransactionLayout, 820, 640);
    } // Adnan added-modified-end-(commenting out)

    
    private void saveTransaction() {
        String accountName = accountComboBox.getValue();
        String transactionType = transactionTypeComboBox.getValue();
        LocalDate transactionDate = transactionDatePicker.getValue();
        String transactionDescription = transactionDescriptionField.getText();
        String paymentAmountText = paymentAmountField.getText();
        String depositAmountText = depositAmountField.getText();

        // Validate required fields
        if (accountName == null || transactionType == null ||
                transactionDate == null || transactionDescription.isEmpty()) {
            showAlert("Error", "Please fill in all required fields.");
            return;
        }

        // Validate amounts
        if (paymentAmountText.isEmpty() && depositAmountText.isEmpty()) {
            showAlert("Error", "Please enter either a payment or deposit amount.");
            return;
        }

        double paymentAmount = 0;
        double depositAmount = 0;

        try {
            if (!paymentAmountText.isEmpty()) {
                paymentAmount = Double.parseDouble(paymentAmountText);
            }
            if (!depositAmountText.isEmpty()) {
                depositAmount = Double.parseDouble(depositAmountText);
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter valid numbers for amounts.");
            return;
        }

        // Save transaction to database
        if (dbHelper.saveTransaction(accountName, transactionType, Date.valueOf(transactionDate),
                transactionDescription, paymentAmount, depositAmount)) {
            showAlert("Success", "Transaction saved successfully!");
            primaryStage.setScene(createHomeScene());
        } else {
            showAlert("Error", "Failed to save transaction.");
        }
    }

    private Scene createTransactionsScene() {
        VBox TransactionsLayout = new VBox(20);
        TransactionsLayout.setPadding(new Insets(20));
        TransactionsLayout.setAlignment(Pos.CENTER);

        Label homePageLabel = new Label("Transactions");
        homePageLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: #1e4b35; -fx-font-weight: bold;");

        GridPane enterTransactionPane = new GridPane();
        enterTransactionPane.setPadding(new Insets(10));
        enterTransactionPane.setHgap(10);
        enterTransactionPane.setVgap(20);

        String buttonStyle = "-fx-background-color: #cbdfd6;";
        String hoverStyle = "-fx-background-color: #749485; -fx-text-fill: white;";
        Button backButton = new Button("Back");

        backButton.setOnAction(e -> primaryStage.setScene(createHomeScene()));
        backButton.setStyle(buttonStyle);
        backButton.setOnMouseEntered(e -> backButton.setStyle(hoverStyle));
        backButton.setOnMouseExited(e -> backButton.setStyle(buttonStyle));

        // Add back button to the GridPane
        enterTransactionPane.add(backButton, 0, 0);

        // Initialize and set up the TableView
        transactionsTable = new TableView<>();
        setupTransactionsTable();
        refreshTransactionsTable();

        Button deleteTransactionButton = new Button("Delete Selected Transaction");
        deleteTransactionButton.setStyle(buttonStyle);
        deleteTransactionButton.setOnMouseEntered(e -> deleteTransactionButton.setStyle(hoverStyle));
        deleteTransactionButton.setOnMouseExited(e -> deleteTransactionButton.setStyle(buttonStyle));
        deleteTransactionButton.setOnAction(e -> deleteSelectedTransaction());

        // Add components to the layout, including enterTransactionPane
        TransactionsLayout.getChildren().addAll(enterTransactionPane, homePageLabel, transactionsTable, deleteTransactionButton);

        return new Scene(TransactionsLayout, 820, 640);
    }

    private Scene createEnterScheduledTransactionsScene() {
        VBox enterScheduledTransactionLayout = new VBox(20);
        enterScheduledTransactionLayout.setPadding(new Insets(20));
        enterScheduledTransactionLayout.setStyle("-fx-background-color: white;");

        Label createTransactionPageLabel = new Label("Create New Scheduled Transaction");
        createTransactionPageLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: #1e4b35 ; -fx-font-weight: bold;");

        GridPane enterTransactionPane = new GridPane();
        enterTransactionPane.setPadding(new Insets(10));
        enterTransactionPane.setHgap(10);
        enterTransactionPane.setVgap(20);

        scheduledNameField = new TextField();
        scheduledNameField.setPromptText("Enter schedule's name");
        accountComboBox = new ComboBox<>();
        accountComboBox.getItems().addAll(dbHelper.getAllAccountNames());
        accountComboBox.setStyle("-fx-background-color: #cbdfd6; -fx-text-fill: black;");
        if (!accountComboBox.getItems().isEmpty()) {
            accountComboBox.setValue(accountComboBox.getItems().get(0));
        }

        transactionTypeComboBox = new ComboBox<>();
        transactionTypeComboBox.getItems().addAll(dbHelper.getAllTransactionTypes());
        transactionTypeComboBox.setStyle("-fx-background-color: #cbdfd6; -fx-text-fill: black;");
        transactionTypeComboBox.setValue(transactionTypeComboBox.getItems().isEmpty() ? null : transactionTypeComboBox.getItems().get(0));

        transactionDatePicker = new DatePicker(LocalDate.now());
        transactionDescriptionField = new TextField();
        paymentAmountField = new TextField();
        depositAmountField = new TextField();

        frequencyComboBox = new ComboBox<>();
        frequencyComboBox.setStyle("-fx-background-color: #cbdfd6; -fx-text-fill: black;");
        frequencyComboBox.getItems().add("Monthly");
        frequencyComboBox.setValue("Monthly");

        dueDateField = new TextField();
        dueDateField.setPromptText("Enter day of the month");

        // Define button styles
        String buttonStyle = "-fx-background-color: #cbdfd6;";
        String hoverStyle = "-fx-background-color: #749485; -fx-text-fill: white;";

        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> saveScheduledTransaction());
        submitButton.setStyle(buttonStyle);
        submitButton.setOnMouseEntered(e -> submitButton.setStyle(hoverStyle));
        submitButton.setOnMouseExited(e -> submitButton.setStyle(buttonStyle));

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> primaryStage.setScene(createHomeScene()));
        backButton.setStyle(buttonStyle);
        backButton.setOnMouseEntered(e -> backButton.setStyle(hoverStyle));
        backButton.setOnMouseExited(e -> backButton.setStyle(buttonStyle));

        enterTransactionPane.add(backButton, 0, 0);
        enterTransactionPane.add(new Label("Schedule's Name:"), 1, 0);
        enterTransactionPane.add(scheduledNameField, 1, 0);
        enterTransactionPane.add(new Label("Account:"), 0, 1);
        enterTransactionPane.add(accountComboBox, 1, 1);
        enterTransactionPane.add(new Label("Transaction Type:"), 0, 2);
        enterTransactionPane.add(transactionTypeComboBox, 1, 2);
        enterTransactionPane.add(new Label("Frequency:"), 0, 3);
        enterTransactionPane.add(frequencyComboBox, 1, 3);
        enterTransactionPane.add(new Label("Due Date:"), 0, 4);
        enterTransactionPane.add(dueDateField, 1, 4);
        enterTransactionPane.add(new Label("Payment Amount:"), 0, 5);
        enterTransactionPane.add(paymentAmountField, 1, 5);
        enterTransactionPane.add(submitButton, 1, 7);

        enterScheduledTransactionLayout.getChildren().addAll(createTransactionPageLabel, enterTransactionPane);
        enterScheduledTransactionLayout.setAlignment(Pos.TOP_CENTER);
        enterTransactionPane.setAlignment(Pos.CENTER);

        return new Scene(enterScheduledTransactionLayout, 820, 640);
    }

    private void saveScheduledTransaction() {
        String scheduleName = scheduledNameField.getText();
        String accountName = accountComboBox.getValue();
        String transactionType = transactionTypeComboBox.getValue();
        String frequency = frequencyComboBox.getValue();
        String dueDate = dueDateField.getText();
        String paymentAmountText = paymentAmountField.getText();

        // Validate required fields
        if (scheduleName.isEmpty() || accountName == null || transactionType == null || frequency == null || dueDate.isEmpty()) {

            showAlert("Error", "Please fill in all required fields.");
            return;
        }

        // Check if a transaction with the same schedule name already exists
        if (dbHelper.scheduleNameExists(scheduleName)) {
            showAlert("Error", "A scheduled transaction with this name already exists.");
            return;
        }


        // Validate amounts
        if (paymentAmountText.isEmpty()) {
            showAlert("Error", "Please enter either a payment amount.");
            return;
        }

        double paymentAmount = 0;

        try {
            if (!paymentAmountText.isEmpty()) {
                paymentAmount = Double.parseDouble(paymentAmountText);
            }

        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter valid numbers for amounts.");
            return;
        }

        // Save transaction to database
        if (dbHelper.saveScheduledTransaction(scheduleName, accountName, transactionType, frequency, dueDate, paymentAmount)){
            showAlert("Success", "Transaction saved successfully!");
            primaryStage.setScene(createScheduledTransactionsScene());
        }
        else {
            showAlert("Error", "Failed to save transaction.");
        }
    }

    private Scene createScheduledTransactionsScene() {
        VBox scheduledTransactionsLayout = new VBox(20);
        scheduledTransactionsLayout.setPadding(new Insets(20));
        scheduledTransactionsLayout.setAlignment(Pos.CENTER);

        Label homePageLabel = new Label("Scheduled Transactions");
        homePageLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: #1e4b35; -fx-font-weight: bold;");

        GridPane enterTransactionPane = new GridPane();
        enterTransactionPane.setPadding(new Insets(10));
        enterTransactionPane.setHgap(10);
        enterTransactionPane.setVgap(20);

        String buttonStyle = "-fx-background-color: #cbdfd6;";
        String hoverStyle = "-fx-background-color: #749485; -fx-text-fill: white;";
        Button backButton = new Button("Back");

        backButton.setOnAction(e -> primaryStage.setScene(createHomeScene()));
        backButton.setStyle(buttonStyle);
        backButton.setOnMouseEntered(e -> backButton.setStyle(hoverStyle));
        backButton.setOnMouseExited(e -> backButton.setStyle(buttonStyle));

        // Add back button to the GridPane
        enterTransactionPane.add(backButton, 0, 0);

        // Initialize and set up the TableView
        scheduledTransactionsTable = new TableView<>();
        setupScheduledTransactionsTable();
        refreshScheduledTransactionsTable();

        Button deletescheduledTransactionButton = new Button("Delete Selected Scheduled Transaction");
        deletescheduledTransactionButton.setStyle(buttonStyle);
        deletescheduledTransactionButton.setOnMouseEntered(e -> deletescheduledTransactionButton.setStyle(hoverStyle));
        deletescheduledTransactionButton.setOnMouseExited(e -> deletescheduledTransactionButton.setStyle(buttonStyle));
        deletescheduledTransactionButton.setOnAction(e -> deleteSelectedScheduledTransaction());

        // Add components to the layout, including enterTransactionPane
        scheduledTransactionsLayout.getChildren().addAll(enterTransactionPane, homePageLabel, scheduledTransactionsTable, deletescheduledTransactionButton);

        return new Scene(scheduledTransactionsLayout, 820, 640);
    }

    private void deleteSelectedAccount() {
        Account selectedAccount = accountTable.getSelectionModel().getSelectedItem();
        if (selectedAccount == null) {
            showAlert("Error", "No account selected.");
            return;
        }

        boolean isDeleted = dbHelper.deleteAccount(selectedAccount.getName());
        if (isDeleted) {
            showAlert("Success", "Account deleted successfully.");
            refreshAccountTable(); // Refresh the table to show updated data
        } else {
            showAlert("Error", "Failed to delete account.");
        }
    }

    private void deleteSelectedScheduledTransaction() {
        ScheduledTransaction selectedScheduledTransaction = scheduledTransactionsTable.getSelectionModel().getSelectedItem();
        if (selectedScheduledTransaction == null) {
            showAlert("Error", "No transaction selected.");
            return;
        }

        boolean isDeleted = dbHelper.deleteScheduledTransaction(selectedScheduledTransaction.getScheduleName());
        if (isDeleted) {
            showAlert("Success", "Transaction deleted successfully.");
            refreshScheduledTransactionsTable(); // Refresh the table to show updated data
        } else {
            showAlert("Error", "Failed to delete transaction.");
        }
    }

    private void deleteSelectedTransaction() {
        Transaction selectedTransaction = transactionsTable.getSelectionModel().getSelectedItem();
        if (selectedTransaction == null) {
            showAlert("Error", "No transaction selected.");
            return;
        }

        boolean isDeleted = dbHelper.deleteTransaction(selectedTransaction.getDescription());
        if (isDeleted) {
            showAlert("Success", "Transaction deleted successfully.");
            refreshTransactionsTable(); // Refresh the table to show updated data
        } else {
            showAlert("Error", "Failed to delete transaction.");
        }
    }

    private void setupDueTodayTransactionsTable() {
        dueTodayTransactionsTable = new TableView<>();

        TableColumn<ScheduledTransaction, String> scheduleNameColumn = new TableColumn<>("Schedule Name");
        scheduleNameColumn.setCellValueFactory(new PropertyValueFactory<>("scheduleName"));

        TableColumn<ScheduledTransaction, String> accountNameColumn = new TableColumn<>("Account Name");
        accountNameColumn.setCellValueFactory(new PropertyValueFactory<>("accountName"));

        TableColumn<ScheduledTransaction, String> transactionTypeColumn = new TableColumn<>("Transaction Type");
        transactionTypeColumn.setCellValueFactory(new PropertyValueFactory<>("transactionType"));

        TableColumn<ScheduledTransaction, Integer> dueDateColumn = new TableColumn<>("Due Date");
        dueDateColumn.setCellValueFactory(new PropertyValueFactory<>("dueDate"));

        TableColumn<ScheduledTransaction, Double> paymentAmountColumn = new TableColumn<>("Payment Amount");
        paymentAmountColumn.setCellValueFactory(new PropertyValueFactory<>("paymentAmount"));

        dueTodayTransactionsTable.getColumns().addAll(scheduleNameColumn, accountNameColumn, transactionTypeColumn, dueDateColumn, paymentAmountColumn);

        dueTodayTransactionsTable.setPlaceholder(new Label("No Scheduled Transactions Due Today!"));
    }

    private void refreshDueTodayTransactionsTable() {
        dueTodayTransactionsTable.getItems().clear();
        dueTodayTransactionsTable.getItems().addAll(dbHelper.getScheduledTransactionsDueToday());
    }

    public static void main(String[] args) {
        launch(args);
    }
}

