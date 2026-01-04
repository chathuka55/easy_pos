package models;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Repair {
    private String repairCode;
    private String customerName;
    private String contactNumber;
    private String repairType;
    private String repairProgress;
    private BigDecimal serviceCharge;
    private BigDecimal totalAmount;
    private BigDecimal discount;
    private BigDecimal paidAmount;
    private BigDecimal balanceAmount;
    private String paymentMethod;
    private String conditions;
    private String borrowedItems;
    private String notes;
    private Timestamp repairDate;
    
    // New audit fields
    private int createdByUserID;
    private String createdByUsername;
    private String createdByFullName;
    private int lastModifiedByUserID;
    private String lastModifiedByUsername;
    private Timestamp lastModifiedDate;

    // Getters and Setters
    public String getRepairCode() {
        return repairCode;
    }

    public void setRepairCode(String repairCode) {
        this.repairCode = repairCode;
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

    public String getRepairType() {
        return repairType;
    }

    public void setRepairType(String repairType) {
        this.repairType = repairType;
    }

    public String getRepairProgress() {
        return repairProgress;
    }

    public void setRepairProgress(String repairProgress) {
        this.repairProgress = repairProgress;
    }

    public BigDecimal getServiceCharge() {
        return serviceCharge;
    }

    public void setServiceCharge(BigDecimal serviceCharge) {
        this.serviceCharge = serviceCharge;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    public BigDecimal getBalanceAmount() {
        return balanceAmount;
    }

    public void setBalanceAmount(BigDecimal balanceAmount) {
        this.balanceAmount = balanceAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getConditions() {
        return conditions;
    }

    public void setConditions(String conditions) {
        this.conditions = conditions;
    }

    public String getBorrowedItems() {
        return borrowedItems;
    }

    public void setBorrowedItems(String borrowedItems) {
        this.borrowedItems = borrowedItems;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Timestamp getRepairDate() {
        return repairDate;
    }

    public void setRepairDate(Timestamp repairDate) {
        this.repairDate = repairDate;
    }
    
    // New audit getters and setters
    public int getCreatedByUserID() { return createdByUserID; }
    public void setCreatedByUserID(int createdByUserID) { this.createdByUserID = createdByUserID; }

    public String getCreatedByUsername() { return createdByUsername; }
    public void setCreatedByUsername(String createdByUsername) { this.createdByUsername = createdByUsername; }

    public String getCreatedByFullName() { return createdByFullName; }
    public void setCreatedByFullName(String createdByFullName) { this.createdByFullName = createdByFullName; }

    public int getLastModifiedByUserID() { return lastModifiedByUserID; }
    public void setLastModifiedByUserID(int lastModifiedByUserID) { this.lastModifiedByUserID = lastModifiedByUserID; }

    public String getLastModifiedByUsername() { return lastModifiedByUsername; }
    public void setLastModifiedByUsername(String lastModifiedByUsername) { this.lastModifiedByUsername = lastModifiedByUsername; }

    public Timestamp getLastModifiedDate() { return lastModifiedDate; }
    public void setLastModifiedDate(Timestamp lastModifiedDate) { this.lastModifiedDate = lastModifiedDate; }
}
    

