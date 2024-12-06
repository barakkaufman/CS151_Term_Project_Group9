package application;

import java.sql.Date;

public class Transaction extends  PaymentType{
    private int id;
    private Date transactionDate;
    private String description;
    private double depositAmount;

    // Constructor
    public Transaction(String accountName, String transactionType,
                                Date transactionDate, String description, double paymentAmount, double depositAmount) {
        super(accountName, transactionType,paymentAmount);
        this.transactionDate = transactionDate;
        this.description = description;
        this.depositAmount = depositAmount;
    }

    // Getters
    public int getId() { return id; } 
    public void setId(int id) { this.id = id; } //Adnan added-modified-start-&-end
    public Date getTransactionDate() { return transactionDate; }
    public String getDescription() { return description; }
    public double getDepositAmount() { return depositAmount; }

}
