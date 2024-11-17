package application;

public class ScheduledTransaction {
    private String scheduleName;
    private String accountName;
    private String transactionType;
    private String frequency;
    private int dueDate;
    private double paymentAmount;

    // Constructor
    public ScheduledTransaction(String scheduleName, String accountName, String transactionType,
                                String frequency, int dueDate, double paymentAmount) {
        this.scheduleName = scheduleName;
        this.accountName = accountName;
        this.transactionType = transactionType;
        this.frequency = frequency;
        this.dueDate = dueDate;
        this.paymentAmount = paymentAmount;
    }

    // Getters
    public String getScheduleName() { return scheduleName; }
    public String getAccountName() { return accountName; }
    public String getTransactionType() { return transactionType; }
    public String getFrequency() { return frequency; }
    public int getDueDate() { return dueDate; }
    public double getPaymentAmount() { return paymentAmount; }
}

