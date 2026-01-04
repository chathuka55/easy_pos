package models;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class WholesaleOrder {
    private int orderID;
    private int customerID;
    private Timestamp orderDate;
    private BigDecimal totalAmount;
    private BigDecimal amountPaid;
    private BigDecimal outstandingBalance;

    // Constructor to initialize all fields
    public WholesaleOrder(int orderID, int customerID, Timestamp orderDate, BigDecimal totalAmount, BigDecimal amountPaid, BigDecimal outstandingBalance) {
        this.orderID = orderID;
        this.customerID = customerID;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.amountPaid = amountPaid;
        this.outstandingBalance = outstandingBalance;
    }

    // Getters and Setters
    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public Timestamp getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Timestamp orderDate) {
        this.orderDate = orderDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(BigDecimal amountPaid) {
        this.amountPaid = amountPaid;
    }

    public BigDecimal getOutstandingBalance() {
        return outstandingBalance;
    }

    public void setOutstandingBalance(BigDecimal outstandingBalance) {
        this.outstandingBalance = outstandingBalance;
    }

    // Optional: Override toString for better printing/logging
    @Override
    public String toString() {
        return "WholesaleOrder{" +
                "orderID=" + orderID +
                ", customerID=" + customerID +
                ", orderDate=" + orderDate +
                ", totalAmount=" + totalAmount +
                ", amountPaid=" + amountPaid +
                ", outstandingBalance=" + outstandingBalance +
                '}';
    }
}
