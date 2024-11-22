package application;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {
    private Connection connection;

    public DatabaseHelper() {
        // Initialize the database connection
        try {
            // Adjust the URL to your SQLite database location
            connection = DriverManager.getConnection("jdbc:sqlite:mydatabase.db");
            createAccountTable();
            createTransactionTable();
            createTransactionTypeTable();
            createSchedulesTable();
            createScheduledTransactionTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        populateInitialTransactions(); //Adnan added-modified-start-&-end
    }

    private void createTransactionTypeTable() {
        String sql = "CREATE TABLE IF NOT EXISTS transaction_types (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL UNIQUE" +
                ");";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean addTransactionType(String typeName) {
        String sql = "INSERT INTO transaction_types (name) VALUES (?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, typeName);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint failed")) {
                System.out.println("Transaction type already exists.");
            }
            e.printStackTrace();
            return false;
        }
    }

    public boolean transactionTypeExists(String transactionType) {
        String sql = "SELECT COUNT(*) FROM transaction_types WHERE name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, transactionType);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0; // Return true if count is greater than 0
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean scheduleNameExists(String scheduleName) {
        String query = "SELECT COUNT(*) FROM scheduled_transactions WHERE schedule_name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, scheduleName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }



    public List<String> getAllTransactionTypes() {
        List<String> transactionTypes = new ArrayList<>();
        String sql = "SELECT name FROM transaction_types";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                transactionTypes.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactionTypes;
    }

    public List<ScheduledTransaction> getScheduledTransactions()  {
        List<ScheduledTransaction> transactions = new ArrayList<>();
        String sql = "SELECT schedule_name, account_name, transaction_type, frequency, due_date, payment_amount FROM scheduled_transactions ORDER BY due_date ASC";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                transactions.add(new ScheduledTransaction(
                        rs.getString("schedule_name"),
                        rs.getString("account_name"),
                        rs.getString("transaction_type"),
                        rs.getString("frequency"),
                        rs.getInt("due_date"),
                        rs.getDouble("payment_amount")
                ));
            }

        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    public List<Transaction> getTransactions()  {
        List<Transaction> trans = new ArrayList<>();
        String sql = "SELECT account_name, transaction_type, transaction_date, description, payment_amount, deposit_amount FROM transactions ORDER BY transaction_date DESC";
        try (Statement stmt = connection.createStatement();
             ResultSet res = stmt.executeQuery(sql)) {
            while (res.next()) {
                trans.add(new Transaction(
                        res.getString("account_name"),
                        res.getString("transaction_type"),
                        res.getDate("transaction_date"),
                        res.getString("description"),
                        res.getDouble("deposit_amount"),
                        res.getDouble("payment_amount")
                ));
            }

        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return trans;
    }


    private void createTransactionTable() {
        String sql = "CREATE TABLE IF NOT EXISTS transactions (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "account_name TEXT NOT NULL," +
                "transaction_type TEXT NOT NULL," +
                "transaction_date DATE NOT NULL," +
                "description TEXT," +
                "payment_amount REAL," +
                "deposit_amount REAL," +
                "FOREIGN KEY (account_name) REFERENCES accounts(name)" +
                ");";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createSchedulesTable() {
        String sql = "CREATE TABLE IF NOT EXISTS schedules (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL UNIQUE" +
                ");";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
            System.out.println("Created 'schedules' table successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to create the 'scheduled_transactions' table if it does not exist
    private void createScheduledTransactionTable() {
        String sql = "CREATE TABLE IF NOT EXISTS scheduledTransactions (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "schedule_name TEXT NOT NULL," +
                "account_name TEXT NOT NULL," +
                "transaction_type TEXT NOT NULL," +
                "frequency TEXT NOT NULL," +
                "due_date TEXT NOT NULL," +
                "payment_amount REAL," +
                "FOREIGN KEY (schedule_name) REFERENCES schedules(name)" +
                ");";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
            System.out.println("Created 'scheduledTransactions' table successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean saveTransaction(String accountName, String transactionType, Date transactionDate,
                                   String description, double paymentAmount, double depositAmount) {
        String sql = "INSERT INTO transactions (account_name, transaction_type, transaction_date, " +
                "description, payment_amount, deposit_amount) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, accountName);
            pstmt.setString(2, transactionType);
            pstmt.setDate(3, transactionDate);
            pstmt.setString(4, description);
            pstmt.setDouble(5, paymentAmount);
            pstmt.setDouble(6, depositAmount);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean saveScheduledTransaction(String scheduleName, String accountName, String transactionType,
                                            String frequency, String dueDate, double paymentAmount) {
        String sql = "INSERT INTO scheduled_transactions (schedule_name, account_name, transaction_type, frequency, due_date, payment_amount) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, scheduleName);
            pstmt.setString(2, accountName);
            pstmt.setString(3, transactionType);
            pstmt.setString(4, frequency);
            pstmt.setString(5, dueDate);
            pstmt.setDouble(6, paymentAmount);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public List<String> getAllAccountNames() {
        List<String> accountNames = new ArrayList<>();
        String sql = "SELECT name FROM accounts";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                accountNames.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return accountNames;
    }

    private void createAccountTable() {
        String sql = "CREATE TABLE IF NOT EXISTS accounts (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "opening_date DATE NOT NULL," +
                "opening_balance REAL NOT NULL" +
                ");";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean createAccount(String name, Date openingDate, double openingBalance) {
        String sql = "INSERT INTO accounts (name, opening_date, opening_balance) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setDate(2, openingDate);
            pstmt.setDouble(3, openingBalance);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Account> getAllAccountDetails() {
        List<Account> accountDetails = new ArrayList<>();
        String sql = "SELECT name, opening_date, opening_balance FROM accounts ORDER BY opening_date DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String name = rs.getString("name");// Retrieve the opening_date as a long (timestamp)
                long timestamp = rs.getLong("opening_date");
                java.sql.Date date = new java.sql.Date(timestamp); // Convert to java.sql.Date
                double balance = rs.getDouble("opening_balance");
                accountDetails.add(new Account(name, date, balance)); // Format the display
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return accountDetails;
    }

    public boolean accountExists(String accountName) {
        String sql = "SELECT COUNT(*) FROM accounts WHERE name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, accountName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Return true if count is greater than 0
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteAccount(String accountName) {
        String sql = "DELETE FROM accounts WHERE name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, accountName);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0; // Return true if rows were affected
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteScheduledTransaction(String scheduleName) {
        String sql = "DELETE FROM scheduled_transactions WHERE schedule_name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, scheduleName);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

// Adnan added-modified-start
public List<Transaction> searchTransactions(String searchTerm) {
    List<Transaction> transactions = new ArrayList<>();
    String sql = "SELECT account_name, transaction_type, transaction_date, description, " +
                 "payment_amount, deposit_amount FROM transactions " +
                 "WHERE description LIKE ? ORDER BY transaction_date DESC";

    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
        pstmt.setString(1, "%" + searchTerm + "%");
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            transactions.add(new Transaction(
                rs.getString("account_name"),
                rs.getString("transaction_type"),
                rs.getDate("transaction_date"),
                rs.getString("description"),
                rs.getDouble("deposit_amount"),
                rs.getDouble("payment_amount")
            ));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return transactions;
}

public boolean updateTransaction(int id, String accountName, String transactionType,
                               Date transactionDate, String description,
                               double paymentAmount, double depositAmount) {
    String sql = "UPDATE transactions SET account_name = ?, transaction_type = ?, " +
                 "transaction_date = ?, description = ?, payment_amount = ?, " +
                 "deposit_amount = ? WHERE id = ?";

    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
        pstmt.setString(1, accountName);
        pstmt.setString(2, transactionType);
        pstmt.setDate(3, transactionDate);
        pstmt.setString(4, description);
        pstmt.setDouble(5, paymentAmount);
        pstmt.setDouble(6, depositAmount);
        pstmt.setInt(7, id);
        return pstmt.executeUpdate() > 0;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

// Add method to populate initial transactions
public void populateInitialTransactions() {
    if (getTransactionCount() == 0) {
        String[] descriptions = {
            "Monthly Rent Payment",
            "Grocery Shopping at Walmart",
            "Salary Deposit",
            "Internet Bill Payment",
            "Car Insurance Premium",
            "Restaurant Dinner",
            "Gas Station Fill-up",
            "Movie Theater Tickets",
            "Utility Bill Payment",
            "Phone Bill Payment"
        };

        double[] amounts = {1200.00, 156.78, 3500.00, 79.99, 145.50,
                          65.43, 45.67, 32.50, 125.00, 89.99};

        for (int i = 0; i < 10; i++) {
            boolean isPayment = i != 2; // Make the third transaction a deposit
            saveTransaction(
                "Main Account",
                isPayment ? "Payment" : "Deposit",
                new Date(System.currentTimeMillis() - (i * 86400000)),
                descriptions[i],
                isPayment ? amounts[i] : 0,
                isPayment ? 0 : amounts[i]
            );
        }
    }
}

private int getTransactionCount() {
    String sql = "SELECT COUNT(*) FROM transactions";
    try (Statement stmt = connection.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {
        if (rs.next()) {
            return rs.getInt(1);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return 0;
} // Adnan added-modified-end

}

