package models;

import java.math.BigDecimal;

public class WholesaleCustomer {
    private int customerID;
    private String customerName;
    private String contactNumber;
    private String email;
    private String address;
    private BigDecimal totalOutstanding;

    // Constructor to initialize all fields
    public WholesaleCustomer(int customerID, String customerName, String contactNumber, String email, String address, BigDecimal totalOutstanding) {
        this.customerID = customerID;
        this.customerName = customerName;
        this.contactNumber = contactNumber;
        this.email = email;
        this.address = address;
        this.totalOutstanding = totalOutstanding;
    }

    // Getters and Setters
    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BigDecimal getTotalOutstanding() {
        return totalOutstanding;
    }

    public void setTotalOutstanding(BigDecimal totalOutstanding) {
        this.totalOutstanding = totalOutstanding;
    }

    // Optional: Override toString for better printing/logging
    @Override
    public String toString() {
        return "WholesaleCustomer{" +
                "customerID=" + customerID +
                ", customerName='" + customerName + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", totalOutstanding=" + totalOutstanding +
                '}';
    }
}
