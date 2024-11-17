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
    // Adnan added-modified-end

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        dbHelper = new DatabaseHelper();
        primaryStage.setTitle("Centsible Banking App");
        primaryStage.setScene(createHomeScene());
        primaryStage.show();
    }

    private Scene createHomeScene() {
        VBox homeLayout = new VBox(10);
        homeLayout.setPadding(new Insets(20));
        homeLayout.setStyle("-fx-background-color: white;");

        MenuBar menuBar = new MenuBar();
        menuBar.setStyle("-fx-background-color: #749485;");
        Menu actionsMenu = new Menu("Pages");

        // Create MenuItems
        MenuItem viewTransactionsMenuItem = new MenuItem("View Transactions");
        viewTransactionsMenuItem.setOnAction(e -> primaryStage.setScene(createTransactionsScene()));

        MenuItem viewScheduledTransactionsMenuItem = new MenuItem("View Scheduled Transactions");
        viewScheduledTransactionsMenuItem.setOnAction(e -> primaryStage.setScene(createScheduledTransactionsScene()));

        // Add MenuItems to the Menu
        actionsMenu.getItems().addAll(viewTransactionsMenuItem, viewScheduledTransactionsMenuItem);

        // Add the Menu to the MenuBar
        menuBar.getMenus().add(actionsMenu);

        Label homePageLabel = new Label("Home Page");
        homePageLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: #1e4b35 ; -fx-font-weight: bold;");

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

        Button addTransactionTypeButton = new Button("Add Transaction Type");
        addTransactionTypeButton.setOnAction(e -> primaryStage.setScene(createAddTransactionTypeScene()));
        addTransactionTypeButton.setStyle(buttonStyle);
        addTransactionTypeButton.setOnMouseEntered(e -> addTransactionTypeButton.setStyle(hoverStyle));
        addTransactionTypeButton.setOnMouseExited(e -> addTransactionTypeButton.setStyle(buttonStyle));

        // Adnan added-modified-start
        Button enterTransactionsButton = new Button("Create New Transaction");
        enterTransactionsButton.setOnAction(e -> primaryStage.setScene(createEnterTransactionsScene()));
        enterTransactionsButton.setStyle(buttonStyle);
        enterTransactionsButton.setOnMouseEntered(e -> enterTransactionsButton.setStyle(hoverStyle));
        enterTransactionsButton.setOnMouseExited(e -> enterTransactionsButton.setStyle(buttonStyle));
        // Adnan added-modified-end

        Button enterScheduledTransactionsButton = new Button("Create New Scheduled Transaction");
        enterScheduledTransactionsButton.setOnAction(e -> primaryStage.setScene(createEnterScheduledTransactionsScene()));
        enterScheduledTransactionsButton.setStyle(buttonStyle);
        enterScheduledTransactionsButton.setOnMouseEntered(e -> enterScheduledTransactionsButton.setStyle(hoverStyle));
        enterScheduledTransactionsButton.setOnMouseExited(e -> enterScheduledTransactionsButton.setStyle(buttonStyle));

        Button deleteAccountButton = new Button("Delete Selected Account");
        deleteAccountButton.setStyle(buttonStyle);
        deleteAccountButton.setOnMouseEntered(e -> deleteAccountButton.setStyle(hoverStyle));
        deleteAccountButton.setOnMouseExited(e -> deleteAccountButton.setStyle(buttonStyle));
        deleteAccountButton.setOnAction(e -> deleteSelectedAccount());

        // Place buttons in an HBox for horizontal layout
        HBox buttonLayout = new HBox(15);
        buttonLayout.setAlignment(Pos.CENTER);
        buttonLayout.getChildren().addAll(createAccountButton, addTransactionTypeButton, enterTransactionsButton, enterScheduledTransactionsButton, deleteAccountButton);

        // Add a spacer to create space between the account table and buttons
        Region spacer = new Region();
        spacer.setMinHeight(20);


        homeLayout.getChildren().addAll(menuBar, homePageLabel, new Label("Your Accounts"),
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


    // Adnan added-modified-start
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
    }


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
    // Adnan added-modified-end

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

        // Add components to the layout, including enterTransactionPane
        TransactionsLayout.getChildren().addAll(enterTransactionPane, homePageLabel, transactionsTable);

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

        // Add components to the layout, including enterTransactionPane
        scheduledTransactionsLayout.getChildren().addAll(enterTransactionPane, homePageLabel, scheduledTransactionsTable);

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



    public static void main(String[] args) {
        launch(args);
    }
}

