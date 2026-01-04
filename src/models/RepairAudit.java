package models;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.net.InetAddress;

public class RepairAudit {
    private int auditID;
    private String repairCode;
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
    private BigDecimal paidAmount;
    private BigDecimal balanceAmount;
    private String customerName;
    private String repairType;
    private String repairProgress;
    private String paymentMethod;

    // Constructor for easy creation
    public RepairAudit(String repairCode, String action, User user) {
        this.repairCode = repairCode;
        this.action = action;
        if (user != null) {
            this.userID = user.getUserID();
            this.username = user.getUsername();
            this.userFullName = user.getName();
        } else {
            this.userID = 0;
            this.username = "System";
            this.userFullName = "System";
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

    public RepairAudit() {}

    // All getters and setters
    public int getAuditID() { return auditID; }
    public void setAuditID(int auditID) { this.auditID = auditID; }

    public String getRepairCode() { return repairCode; }
    public void setRepairCode(String repairCode) { this.repairCode = repairCode; }

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

    public BigDecimal getPaidAmount() { return paidAmount; }
    public void setPaidAmount(BigDecimal paidAmount) { this.paidAmount = paidAmount; }

    public BigDecimal getBalanceAmount() { return balanceAmount; }
    public void setBalanceAmount(BigDecimal balanceAmount) { this.balanceAmount = balanceAmount; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getRepairType() { return repairType; }
    public void setRepairType(String repairType) { this.repairType = repairType; }

    public String getRepairProgress() { return repairProgress; }
    public void setRepairProgress(String repairProgress) { this.repairProgress = repairProgress; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
}