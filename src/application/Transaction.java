package application;

import java.sql.Date;

public class Transaction {
    private int id;
    private String accountName;
    private String transactionType;
    private Date transactionDate;
    private String description;
    private double paymentAmount;
    private double depositAmount;

    // Constructor
    public Transaction(String accountName, String transactionType,
                                Date transactionDate, String description, double paymentAmount, double depositAmount) {
        this.accountName = accountName;
        this.transactionType = transactionType;
        this.transactionDate = transactionDate;
        this.description = description;
        this.paymentAmount = paymentAmount;
        this.depositAmount = depositAmount;
    }

    // Getters
    public int getId() { return id; } 
    public void setId(int id) { this.id = id; } //Adnan added-modified-start-&-end
    public String getAccountName() { return accountName; }
    public String getTransactionType() { return transactionType; }
    public Date getTransactionDate() { return transactionDate; }
    public String getDescription() { return description; }
    public double getPaymentAmount() { return paymentAmount; }
    public double getDepositAmount() { return depositAmount; }

}
