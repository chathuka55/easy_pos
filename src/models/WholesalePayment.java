package models;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class WholesalePayment {
    private int paymentID;
    private int customerID;
    private int orderID;
    private BigDecimal paymentAmount;
    private Timestamp paymentDate;
    private String paymentMethod;

    // Constructor to initialize all fields
    public WholesalePayment(int paymentID, int customerID, int orderID, BigDecimal paymentAmount, Timestamp paymentDate, String paymentMethod) {
        this.paymentID = paymentID;
        this.customerID = customerID;
        this.orderID = orderID;
        this.paymentAmount = paymentAmount;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
    }

    // Getters and Setters
    public int getPaymentID() {
        return paymentID;
    }

    public void setPaymentID(int paymentID) {
        this.paymentID = paymentID;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public BigDecimal getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public Timestamp getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Timestamp paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    // Optional: Override toString for better printing/logging
    @Override
    public String toString() {
        return "WholesalePayment{" +
                "paymentID=" + paymentID +
                ", customerID=" + customerID +
                ", orderID=" + orderID +
                ", paymentAmount=" + paymentAmount +
                ", paymentDate=" + paymentDate +
                ", paymentMethod='" + paymentMethod + '\'' +
                '}';
    }
}
