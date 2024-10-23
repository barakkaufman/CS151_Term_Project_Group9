import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.Date;
import java.time.LocalDate;

public class Centsible extends Application {

    private Stage primaryStage;
    private DatabaseHelper dbHelper;
    private TextField accountNameField;
    private DatePicker openingDatePicker;
    private TextField openingBalanceField;
    private TableView<Account> accountTable; // Use TableView for tabular display

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

        homeLayout.getChildren().addAll(homePageLabel, new Label("Your Accounts"), accountTable, createAccountButton);

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
        openingDatePicker = new DatePicker();
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

    public static void main(String[] args) {
        launch(args);
    }
}

