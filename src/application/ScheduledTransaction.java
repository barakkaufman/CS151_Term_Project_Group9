package application;

public class ScheduledTransaction  extends  PaymentType{
    private String scheduleName;
    private String frequency;
    private int dueDate;

    // Constructor
    public ScheduledTransaction(String scheduleName, String accountName, String transactionType,
                                String frequency, int dueDate, double paymentAmount) {
        super(accountName, transactionType,paymentAmount);
        this.scheduleName = scheduleName;
        this.frequency = frequency;
        this.dueDate = dueDate;
    }

    // Getters
    public String getScheduleName() { return scheduleName; }
    public String getFrequency() { return frequency; }
    public int getDueDate() { return dueDate; }
}

