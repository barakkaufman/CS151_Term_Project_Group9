package application;

import java.util.Date;

public class Account {
    private String name;
    private java.sql.Date openingDate;
    private double openingBalance;

    public Account(String name, java.sql.Date openingDate, double openingBalance) {
        this.name = name;
        this.openingDate = openingDate;
        this.openingBalance = openingBalance;
    }

    public String getName() {
        return name;
    }

    public java.sql.Date getOpeningDate() {
        return openingDate;
    }

    public double getOpeningBalance() {
        return openingBalance;
    }
}
