package application;

import javafx.application.Application;
import javafx.geometry.Insets;
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
    private TableView<Account> accountTable; // Use TableView for tabular display

    // Adnan added-modified-start transaction fields
    private ComboBox<String> accountComboBox;
    private ComboBox<String> transactionTypeComboBox;
    private DatePicker transactionDatePicker;
    private TextField transactionDescriptionField;
    private TextField paymentAmountField;
    private TextField depositAmountField;
    // Adnan added-modified-end

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        dbHelper = new DatabaseHelper(); // Initialize your database helper
        primaryStage.setTitle("Centsible Banking App");
        primaryStage.setScene(createHomeScene());
        primaryStage.show();
    }

    private Scene createHomeScene() {
        VBox homeLayout = new VBox(10);
        homeLayout.setPadding(new Insets(10));

        Label homePageLabel = new Label("Home Page");
        homePageLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        accountTable = new TableView<>(); // Initialize the TableView
        setupAccountTable();

        refreshAccountTable(); // Populate the table with account details

        Button createAccountButton = new Button("Create Account");
        createAccountButton.setOnAction(e -> primaryStage.setScene(createCreateAccountScene()));

        // Adnan added-modified-start


        Button enterTransactionsButton = new Button("Create New Transaction");
        enterTransactionsButton.setOnAction(e -> primaryStage.setScene(createEnterTransactionsScene()));

        homeLayout.getChildren().addAll(homePageLabel, new Label("Your Accounts"),
                accountTable, createAccountButton, enterTransactionsButton);
        // Adnan added-modified-end

        return new Scene(homeLayout, 800, 640);
    }

    private void setupAccountTable() {
        TableColumn<Account, String> nameColumn = new TableColumn<>("Account Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Account, Date> dateColumn = new TableColumn<>("Opening Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("openingDate"));

        TableColumn<Account, Double> balanceColumn = new TableColumn<>("Opening Balance");
        balanceColumn.setCellValueFactory(new PropertyValueFactory<>("openingBalance"));

        accountTable.getColumns().addAll(nameColumn, dateColumn, balanceColumn);
    }

    private Scene createCreateAccountScene() {
        GridPane createAccountPane = new GridPane();
        createAccountPane.setPadding(new Insets(10));
        createAccountPane.setHgap(10);
        createAccountPane.setVgap(10);

        accountNameField = new TextField();
        openingDatePicker = new DatePicker(LocalDate.now());
        openingBalanceField = new TextField();

        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> createAccount());

        // Add a "Back" button to return to the home page
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> primaryStage.setScene(createHomeScene()));

        // Add components to the grid
        createAccountPane.add(new Label("Account Name:"), 0, 0);
        createAccountPane.add(accountNameField, 1, 0);
        createAccountPane.add(new Label("Opening Date:"), 0, 1);
        createAccountPane.add(openingDatePicker, 1, 1);
        createAccountPane.add(new Label("Opening Balance:"), 0, 2);
        createAccountPane.add(openingBalanceField, 1, 2);
        createAccountPane.add(submitButton, 1, 3);
        createAccountPane.add(backButton, 0, 3); // Add the "Back" button to the same row as the "Submit" button

        return new Scene(createAccountPane, 800, 640);
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

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private Scene createAddTransactionTypeScene() {
        GridPane addTransactionTypePane = new GridPane();
        addTransactionTypePane.setPadding(new Insets(10));
        addTransactionTypePane.setHgap(10);
        addTransactionTypePane.setVgap(10);

        TextField transactionTypeNameField = new TextField();

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
                transactionTypeComboBox.getItems().add(transactionTypeName); // Update ComboBox
                primaryStage.setScene(createEnterTransactionsScene());
            } else {
                showAlert("Error", "Failed to add transaction type.");
            }
        });

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> primaryStage.setScene(createEnterTransactionsScene()));

        addTransactionTypePane.add(new Label("Transaction Type Name:"), 0, 0);
        addTransactionTypePane.add(transactionTypeNameField, 1, 0);
        addTransactionTypePane.add(submitButton, 1, 1);
        addTransactionTypePane.add(backButton, 0, 1);

        return new Scene(addTransactionTypePane, 400, 200);
    }


    // Adnan added-modified-start
    private Scene createEnterTransactionsScene() {
        GridPane enterTransactionPane = new GridPane();
        enterTransactionPane.setPadding(new Insets(10));
        enterTransactionPane.setHgap(10);
        enterTransactionPane.setVgap(10);

        accountComboBox = new ComboBox<>();
        accountComboBox.getItems().addAll(dbHelper.getAllAccountNames());
        if (!accountComboBox.getItems().isEmpty()) {
            accountComboBox.setValue(accountComboBox.getItems().get(0));
        }

        transactionTypeComboBox = new ComboBox<>();
        transactionTypeComboBox.getItems().addAll(dbHelper.getAllTransactionTypes());
        transactionTypeComboBox.setValue(transactionTypeComboBox.getItems().isEmpty() ? null : transactionTypeComboBox.getItems().get(0));

        Button addTransactionTypeButton = new Button("Add Type");
        addTransactionTypeButton.setOnAction(e -> primaryStage.setScene(createAddTransactionTypeScene()));

        transactionDatePicker = new DatePicker(LocalDate.now());
        transactionDescriptionField = new TextField();
        paymentAmountField = new TextField();
        depositAmountField = new TextField();

        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> saveTransaction());

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> primaryStage.setScene(createHomeScene()));

        enterTransactionPane.add(new Label("Account:"), 0, 0);
        enterTransactionPane.add(accountComboBox, 1, 0);
        enterTransactionPane.add(new Label("Transaction Type:"), 0, 1);
        enterTransactionPane.add(transactionTypeComboBox, 1, 1);
        enterTransactionPane.add(addTransactionTypeButton, 2, 1);
        enterTransactionPane.add(new Label("Transaction Date:"), 0, 2);
        enterTransactionPane.add(transactionDatePicker, 1, 2);
        enterTransactionPane.add(new Label("Transaction Description:"), 0, 3);
        enterTransactionPane.add(transactionDescriptionField, 1, 3);
        enterTransactionPane.add(new Label("Payment Amount:"), 0, 4);
        enterTransactionPane.add(paymentAmountField, 1, 4);
        enterTransactionPane.add(new Label("Deposit Amount:"), 0, 5);
        enterTransactionPane.add(depositAmountField, 1, 5);
        enterTransactionPane.add(submitButton, 1, 6);
        enterTransactionPane.add(backButton, 0, 6);

        return new Scene(enterTransactionPane, 800, 640);
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

    public static void main(String[] args) {
        launch(args);
    }
}

