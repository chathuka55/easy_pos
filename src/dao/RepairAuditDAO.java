package dao;

import db.ConnectionFactory;
import models.RepairAudit;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepairAuditDAO {
    
    public boolean addAuditLog(RepairAudit audit) throws SQLException {
        String sql = "INSERT INTO RepairAuditLog (RepairCode, Action, UserID, Username, UserFullName, " +
                    "ActionTimestamp, IPAddress, MachineName, Details, OldValues, NewValues, " +
                    "TotalAmount, PaidAmount, BalanceAmount, CustomerName, RepairType, " +
                    "RepairProgress, PaymentMethod) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, audit.getRepairCode());
            stmt.setString(2, audit.getAction());
            stmt.setInt(3, audit.getUserID());
            stmt.setString(4, audit.getUsername());
            stmt.setString(5, audit.getUserFullName());
            stmt.setTimestamp(6, audit.getActionTimestamp());
            stmt.setString(7, audit.getIpAddress());
            stmt.setString(8, audit.getMachineName());
            stmt.setString(9, audit.getDetails());
            stmt.setString(10, audit.getOldValues());
            stmt.setString(11, audit.getNewValues());
            stmt.setBigDecimal(12, audit.getTotalAmount());
            stmt.setBigDecimal(13, audit.getPaidAmount());
            stmt.setBigDecimal(14, audit.getBalanceAmount());
            stmt.setString(15, audit.getCustomerName());
            stmt.setString(16, audit.getRepairType());
            stmt.setString(17, audit.getRepairProgress());
            stmt.setString(18, audit.getPaymentMethod());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    public List<RepairAudit> getAuditsByRepairCode(String repairCode) throws SQLException {
        String sql = "SELECT * FROM RepairAuditLog WHERE RepairCode = ? ORDER BY ActionTimestamp DESC";
        List<RepairAudit> audits = new ArrayList<>();
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, repairCode);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                audits.add(mapResultSetToAudit(rs));
            }
        }
        return audits;
    }
    
    public List<RepairAudit> getAuditsByUser(int userID) throws SQLException {
        String sql = "SELECT * FROM RepairAuditLog WHERE UserID = ? ORDER BY ActionTimestamp DESC";
        List<RepairAudit> audits = new ArrayList<>();
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userID);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                audits.add(mapResultSetToAudit(rs));
            }
        }
        return audits;
    }
    
    public List<RepairAudit> getRecentAudits(int limit) throws SQLException {
        String sql = "SELECT * FROM RepairAuditLog ORDER BY ActionTimestamp DESC LIMIT ?";
        List<RepairAudit> audits = new ArrayList<>();
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                audits.add(mapResultSetToAudit(rs));
            }
        }
        return audits;
    }
    
    public List<RepairAudit> getAuditsByDateRange(Timestamp startDate, Timestamp endDate) throws SQLException {
        String sql = "SELECT * FROM RepairAuditLog WHERE ActionTimestamp BETWEEN ? AND ? ORDER BY ActionTimestamp DESC";
        List<RepairAudit> audits = new ArrayList<>();
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTimestamp(1, startDate);
            stmt.setTimestamp(2, endDate);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                audits.add(mapResultSetToAudit(rs));
            }
        }
        return audits;
    }
    
    public List<RepairAudit> getAuditsByAction(String action) throws SQLException {
        String sql = "SELECT * FROM RepairAuditLog WHERE Action = ? ORDER BY ActionTimestamp DESC";
        List<RepairAudit> audits = new ArrayList<>();
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, action);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                audits.add(mapResultSetToAudit(rs));
            }
        }
        return audits;
    }
    
    private RepairAudit mapResultSetToAudit(ResultSet rs) throws SQLException {
        RepairAudit audit = new RepairAudit();
        audit.setAuditID(rs.getInt("AuditID"));
        audit.setRepairCode(rs.getString("RepairCode"));
        audit.setAction(rs.getString("Action"));
        audit.setUserID(rs.getInt("UserID"));
        audit.setUsername(rs.getString("Username"));
        audit.setUserFullName(rs.getString("UserFullName"));
        audit.setActionTimestamp(rs.getTimestamp("ActionTimestamp"));
        audit.setIpAddress(rs.getString("IPAddress"));
        audit.setMachineName(rs.getString("MachineName"));
        audit.setDetails(rs.getString("Details"));
        audit.setOldValues(rs.getString("OldValues"));
        audit.setNewValues(rs.getString("NewValues"));
        audit.setTotalAmount(rs.getBigDecimal("TotalAmount"));
        audit.setPaidAmount(rs.getBigDecimal("PaidAmount"));
        audit.setBalanceAmount(rs.getBigDecimal("BalanceAmount"));
        audit.setCustomerName(rs.getString("CustomerName"));
        audit.setRepairType(rs.getString("RepairType"));
        audit.setRepairProgress(rs.getString("RepairProgress"));
        audit.setPaymentMethod(rs.getString("PaymentMethod"));
        return audit;
    }
}