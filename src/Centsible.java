import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.Date;
import java.time.LocalDate;

public class Centsible extends Application {

    private Stage primaryStage;
    private DatabaseHelper dbHelper; // Assuming you have a DatabaseHelper class
    private TextField accountNameField;
    private DatePicker openingDatePicker;
    private TextField openingBalanceField;
    private ListView<String> accountListView;

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

        accountListView = new ListView<>();
        refreshAccountList();

        Button createAccountButton = new Button("Create Account");
        createAccountButton.setOnAction(e -> primaryStage.setScene(createCreateAccountScene()));

        homeLayout.getChildren().addAll(new Label("Your Accounts"), accountListView, createAccountButton);

        return new Scene(homeLayout, 800, 640);
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

        createAccountPane.add(new Label("Account Name:"), 0, 0);
        createAccountPane.add(accountNameField, 1, 0);
        createAccountPane.add(new Label("Opening Date:"), 0, 1);
        createAccountPane.add(openingDatePicker, 1, 1);
        createAccountPane.add(new Label("Opening Balance:"), 0, 2);
        createAccountPane.add(openingBalanceField, 1, 2);
        createAccountPane.add(submitButton, 1, 3);

        return new Scene(createAccountPane, 800, 640);
    }

    private void createAccount() {
        String accountName = accountNameField.getText();
        LocalDate openingDate = openingDatePicker.getValue();
        double openingBalance;

        // Validate the opening balance input
        try {
            openingBalance = Double.parseDouble(openingBalanceField.getText());
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

    private void refreshAccountList() {
        accountListView.getItems().clear();
        // Get account details with balances from the database
        for (String accountDetail : dbHelper.getAllAccountDetails()) {
            accountListView.getItems().add(accountDetail);
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
