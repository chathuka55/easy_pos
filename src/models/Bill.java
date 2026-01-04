package models;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public class Bill {
    private int billID;
    private String billCode;
    private String customerName;
    private Timestamp billDate;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private BigDecimal balance;
    private String notes;
    private String paymentMethod;
    private List<BillItem> items;
    
    // New audit fields
    private int createdByUserID;
    private String createdByUsername;
    private String createdByFullName;
    private int lastModifiedByUserID;
    private String lastModifiedByUsername;
    private Timestamp lastModifiedDate;


    // Getters and Setters

    public int getBillID() {
        return billID;
    }

    public void setBillID(int billID) {
        this.billID = billID;
    }

    public String getBillCode() {
        return billCode;
    }

    public void setBillCode(String billCode) {
        this.billCode = billCode;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Timestamp getBillDate() {
        return billDate;
    }

    public void setBillDate(Timestamp billDate) {
        this.billDate = billDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
    
     // Getters and Setters
    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    
    public List<BillItem> getItems() {
        return items;
    }

    public void setItems(List<BillItem> items) {
        this.items = items;
    }
    
    public int getCreatedByUserID() {
        return createdByUserID;
    }

    public void setCreatedByUserID(int createdByUserID) {
        this.createdByUserID = createdByUserID;
    }

    public String getCreatedByUsername() {
        return createdByUsername;
    }

    public void setCreatedByUsername(String createdByUsername) {
        this.createdByUsername = createdByUsername;
    }

    public String getCreatedByFullName() {
        return createdByFullName;
    }

    public void setCreatedByFullName(String createdByFullName) {
        this.createdByFullName = createdByFullName;
    }

    public int getLastModifiedByUserID() {
        return lastModifiedByUserID;
    }

    public void setLastModifiedByUserID(int lastModifiedByUserID) {
        this.lastModifiedByUserID = lastModifiedByUserID;
    }

    public String getLastModifiedByUsername() {
        return lastModifiedByUsername;
    }

    public void setLastModifiedByUsername(String lastModifiedByUsername) {
        this.lastModifiedByUsername = lastModifiedByUsername;
    }

    public Timestamp getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Timestamp lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}
    
    
