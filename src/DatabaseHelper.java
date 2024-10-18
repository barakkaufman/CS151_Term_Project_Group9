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
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    public List<String> getAllAccountDetails() {
        List<String> accountDetails = new ArrayList<>();
        String sql = "SELECT name, opening_balance FROM accounts";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String name = rs.getString("name");
                double balance = rs.getDouble("opening_balance");
                accountDetails.add(name + " - $" + balance); // Format the display
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

}
