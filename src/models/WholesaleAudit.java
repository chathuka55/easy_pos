package models;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class WholesaleAudit {
    private int auditId;
    private String billId;
    private String action;
    private int userId;
    private String username;
    private String userFullName;
    private Timestamp actionTimestamp;
    private String ipAddress;
    private String details;
    private String oldValues;
    private String newValues;
    private int customerId;
    private String customerName;
    private BigDecimal totalAmount;
    private BigDecimal paymentReceived;
    private BigDecimal outstanding;
    
    // Default constructor
    public WholesaleAudit() {
        this.actionTimestamp = new Timestamp(System.currentTimeMillis());
        this.ipAddress = getLocalIPAddress();
    }
    
    // Constructor with User
    public WholesaleAudit(String billId, String action, User user) {
        this();
        this.billId = billId;
        this.action = action;
        if (user != null) {
            this.userId = user.getUserID();
            this.username = user.getUsername();
            this.userFullName = user.getName();
        }
    }
    
    // Get local IP address
    private String getLocalIPAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "Unknown";
        }
    }
    
    // All getters and setters
    public int getAuditId() { return auditId; }
    public void setAuditId(int auditId) { this.auditId = auditId; }
    
    public String getBillId() { return billId; }
    public void setBillId(String billId) { this.billId = billId; }
    
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getUserFullName() { return userFullName; }
    public void setUserFullName(String userFullName) { this.userFullName = userFullName; }
    
    public Timestamp getActionTimestamp() { return actionTimestamp; }
    public void setActionTimestamp(Timestamp actionTimestamp) { this.actionTimestamp = actionTimestamp; }
    
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    
    public String getOldValues() { return oldValues; }
    public void setOldValues(String oldValues) { this.oldValues = oldValues; }
    
    public String getNewValues() { return newValues; }
    public void setNewValues(String newValues) { this.newValues = newValues; }
    
    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }
    
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    
    public BigDecimal getPaymentReceived() { return paymentReceived; }
    public void setPaymentReceived(BigDecimal paymentReceived) { this.paymentReceived = paymentReceived; }
    
    public BigDecimal getOutstanding() { return outstanding; }
    public void setOutstanding(BigDecimal outstanding) { this.outstanding = outstanding; }
}