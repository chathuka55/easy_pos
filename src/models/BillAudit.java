package models;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.net.InetAddress;

public class BillAudit {
    private int auditID;
    private String billCode;
    private String action;
    private int userID;
    private String username;
    private String userFullName;
    private Timestamp actionTimestamp;
    private String ipAddress;
    private String machineName;
    private String details;
    private String oldValues;
    private String newValues;
    private BigDecimal totalAmount;
    private String customerName;

    // Constructor for easy creation
    public BillAudit(String billCode, String action, User user) {
        this.billCode = billCode;
        this.action = action;
        if (user != null) {
            this.userID = user.getUserID();
            this.username = user.getUsername();
            this.userFullName = user.getName(); // or getFullName() based on your User model
        }
        this.actionTimestamp = new Timestamp(System.currentTimeMillis());
        try {
            InetAddress localhost = InetAddress.getLocalHost();
            this.ipAddress = localhost.getHostAddress();
            this.machineName = localhost.getHostName();
        } catch (Exception e) {
            this.ipAddress = "Unknown";
            this.machineName = "Unknown";
        }
    }

    public BillAudit() {}

    // Getters and setters
    public int getAuditID() { return auditID; }
    public void setAuditID(int auditID) { this.auditID = auditID; }

    public String getBillCode() { return billCode; }
    public void setBillCode(String billCode) { this.billCode = billCode; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public int getUserID() { return userID; }
    public void setUserID(int userID) { this.userID = userID; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getUserFullName() { return userFullName; }
    public void setUserFullName(String userFullName) { this.userFullName = userFullName; }

    public Timestamp getActionTimestamp() { return actionTimestamp; }
    public void setActionTimestamp(Timestamp actionTimestamp) { this.actionTimestamp = actionTimestamp; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getMachineName() { return machineName; }
    public void setMachineName(String machineName) { this.machineName = machineName; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public String getOldValues() { return oldValues; }
    public void setOldValues(String oldValues) { this.oldValues = oldValues; }

    public String getNewValues() { return newValues; }
    public void setNewValues(String newValues) { this.newValues = newValues; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
}