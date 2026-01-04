package models;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

public class CheckBill {

    // No-arg constructor
    public CheckBill() {
    }

    // Full-arg constructor with user tracking
    public CheckBill(String billId, String customerId, Timestamp billDate, BigDecimal totalPayable, BigDecimal paymentReceived,
                     String paymentMethod, String bank, String chequeNo, Date chequeDate, String notes, BigDecimal outstanding,
                     int createdByUserId, String createdByUsername, Timestamp createdDate,
                     int lastModifiedByUserId, String lastModifiedByUsername, Timestamp lastModifiedDate) {
        this.billId = billId;
        this.customerId = customerId;
        this.billDate = billDate;
        this.totalPayable = totalPayable;
        this.paymentReceived = paymentReceived;
        this.paymentMethod = paymentMethod;
        this.bank = bank;
        this.chequeNo = chequeNo;
        this.chequeDate = chequeDate;
        this.notes = notes;
        this.outstanding = outstanding;
        this.createdByUserId = createdByUserId;
        this.createdByUsername = createdByUsername;
        this.createdDate = createdDate;
        this.lastModifiedByUserId = lastModifiedByUserId;
        this.lastModifiedByUsername = lastModifiedByUsername;
        this.lastModifiedDate = lastModifiedDate;
    }

    // Existing fields
    private String billId;
    private String customerId;
    private Timestamp billDate;
    private BigDecimal totalPayable;
    private BigDecimal paymentReceived;
    private String paymentMethod;
    private String bank;
    private String chequeNo;
    private Date chequeDate;
    private String notes;
    private BigDecimal outstanding;
    
    // New user tracking fields
    private int createdByUserId;
    private String createdByUsername;
    private Timestamp createdDate;
    private int lastModifiedByUserId;
    private String lastModifiedByUsername;
    private Timestamp lastModifiedDate;

    // Existing getters and setters
    public String getBillId() { return billId; }
    public void setBillId(String billId) { this.billId = billId; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public Timestamp getBillDate() { return billDate; }
    public void setBillDate(Timestamp billDate) { this.billDate = billDate; }

    public BigDecimal getTotalPayable() { return totalPayable; }
    public void setTotalPayable(BigDecimal totalPayable) { this.totalPayable = totalPayable; }

    public BigDecimal getPaymentReceived() { return paymentReceived; }
    public void setPaymentReceived(BigDecimal paymentReceived) { this.paymentReceived = paymentReceived; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getBank() { return bank; }
    public void setBank(String bank) { this.bank = bank; }

    public String getChequeNo() { return chequeNo; }
    public void setChequeNo(String chequeNo) { this.chequeNo = chequeNo; }

    public Date getChequeDate() { return chequeDate; }
    public void setChequeDate(Date chequeDate) { this.chequeDate = chequeDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public BigDecimal getOutstanding() { return outstanding; }
    public void setOutstanding(BigDecimal outstanding) { this.outstanding = outstanding; }
    
    // New getters and setters for user tracking
    public int getCreatedByUserId() { return createdByUserId; }
    public void setCreatedByUserId(int createdByUserId) { this.createdByUserId = createdByUserId; }
    
    public String getCreatedByUsername() { return createdByUsername; }
    public void setCreatedByUsername(String createdByUsername) { this.createdByUsername = createdByUsername; }
    
    public Timestamp getCreatedDate() { return createdDate; }
    public void setCreatedDate(Timestamp createdDate) { this.createdDate = createdDate; }
    
    public int getLastModifiedByUserId() { return lastModifiedByUserId; }
    public void setLastModifiedByUserId(int lastModifiedByUserId) { this.lastModifiedByUserId = lastModifiedByUserId; }
    
    public String getLastModifiedByUsername() { return lastModifiedByUsername; }
    public void setLastModifiedByUsername(String lastModifiedByUsername) { this.lastModifiedByUsername = lastModifiedByUsername; }
    
    public Timestamp getLastModifiedDate() { return lastModifiedDate; }
    public void setLastModifiedDate(Timestamp lastModifiedDate) { this.lastModifiedDate = lastModifiedDate; }
}