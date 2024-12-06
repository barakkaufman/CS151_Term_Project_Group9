package application;

public class PaymentType {
    protected String accountName;
    protected String transactionType;
    protected double paymentAmount;

    public PaymentType(String accountName, String transactionType, double paymentAmount){
        this.accountName = accountName;
        this.transactionType = transactionType;
        this.paymentAmount = paymentAmount;
    }

    public String getAccountName() { return accountName; }
    public String getTransactionType() { return transactionType; }
    public double getPaymentAmount() { return paymentAmount; }


}
