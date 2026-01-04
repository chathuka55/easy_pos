package dao;

import models.WholesaleAudit;
import db.ConnectionFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class WholesaleAuditDAO {
    
    /**
     * Log an audit entry
     */
    public void logAudit(WholesaleAudit audit) throws SQLException {
        String sql = "INSERT INTO WholesaleAuditLog (BillId, Action, UserID, Username, " +
                    "UserFullName, ActionTimestamp, IPAddress, Details, OldValues, NewValues, " +
                    "CustomerID, CustomerName, TotalAmount, PaymentReceived, Outstanding) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, audit.getBillId());
            stmt.setString(2, audit.getAction());
            stmt.setInt(3, audit.getUserId());
            stmt.setString(4, audit.getUsername());
            stmt.setString(5, audit.getUserFullName());
            stmt.setTimestamp(6, audit.getActionTimestamp());
            stmt.setString(7, audit.getIpAddress());
            stmt.setString(8, audit.getDetails());
            stmt.setString(9, audit.getOldValues());
            stmt.setString(10, audit.getNewValues());
            stmt.setInt(11, audit.getCustomerId());
            stmt.setString(12, audit.getCustomerName());
            stmt.setBigDecimal(13, audit.getTotalAmount());
            stmt.setBigDecimal(14, audit.getPaymentReceived());
            stmt.setBigDecimal(15, audit.getOutstanding());
            
            stmt.executeUpdate();
        }
    }
    
    /**
     * Get audit entries by bill ID
     */
    public List<WholesaleAudit> getAuditsByBillId(String billId) throws SQLException {
        String sql = "SELECT * FROM WholesaleAuditLog WHERE BillId = ? ORDER BY ActionTimestamp DESC";
        List<WholesaleAudit> audits = new ArrayList<>();
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, billId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                audits.add(mapResultSetToAudit(rs));
            }
        }
        return audits;
    }
    
    /**
     * Get audit entries by customer ID
     */
    public List<WholesaleAudit> getAuditsByCustomerId(int customerId) throws SQLException {
        String sql = "SELECT * FROM WholesaleAuditLog WHERE CustomerID = ? ORDER BY ActionTimestamp DESC";
        List<WholesaleAudit> audits = new ArrayList<>();
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                audits.add(mapResultSetToAudit(rs));
            }
        }
        return audits;
    }
    
    /**
     * Get audit entries by user ID
     */
    public List<WholesaleAudit> getAuditsByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM WholesaleAuditLog WHERE UserID = ? ORDER BY ActionTimestamp DESC";
        List<WholesaleAudit> audits = new ArrayList<>();
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                audits.add(mapResultSetToAudit(rs));
            }
        }
        return audits;
    }
    
    /**
     * Get audit entries by date range
     */
    public List<WholesaleAudit> getAuditsByDateRange(Timestamp startDate, Timestamp endDate) throws SQLException {
        String sql = "SELECT * FROM WholesaleAuditLog WHERE ActionTimestamp BETWEEN ? AND ? " +
                    "ORDER BY ActionTimestamp DESC";
        List<WholesaleAudit> audits = new ArrayList<>();
        
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
    
    /**
     * Get recent audit entries (last N entries)
     */
    public List<WholesaleAudit> getRecentAudits(int limit) throws SQLException {
        String sql = "SELECT * FROM WholesaleAuditLog ORDER BY ActionTimestamp DESC LIMIT ?";
        List<WholesaleAudit> audits = new ArrayList<>();
        
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
    
    /**
     * Get audit entries by action type
     */
    public List<WholesaleAudit> getAuditsByAction(String action) throws SQLException {
        String sql = "SELECT * FROM WholesaleAuditLog WHERE Action = ? ORDER BY ActionTimestamp DESC";
        List<WholesaleAudit> audits = new ArrayList<>();
        
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
    
    /**
     * Search audits with multiple criteria
     */
    public List<WholesaleAudit> searchAudits(String billId, Integer customerId, 
                                            String action, Integer userId, 
                                            Timestamp startDate, Timestamp endDate) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT * FROM WholesaleAuditLog WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (billId != null && !billId.isEmpty()) {
            sql.append(" AND BillId = ?");
            params.add(billId);
        }
        if (customerId != null) {
            sql.append(" AND CustomerID = ?");
            params.add(customerId);
        }
        if (action != null && !action.equals("All")) {
            sql.append(" AND Action = ?");
            params.add(action);
        }
        if (userId != null) {
            sql.append(" AND UserID = ?");
            params.add(userId);
        }
        if (startDate != null) {
            sql.append(" AND ActionTimestamp >= ?");
            params.add(startDate);
        }
        if (endDate != null) {
            sql.append(" AND ActionTimestamp <= ?");
            params.add(endDate);
        }
        
        sql.append(" ORDER BY ActionTimestamp DESC");
        
        List<WholesaleAudit> audits = new ArrayList<>();
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                audits.add(mapResultSetToAudit(rs));
            }
        }
        return audits;
    }
    
    /**
     * Helper method to map ResultSet to WholesaleAudit
     */
    private WholesaleAudit mapResultSetToAudit(ResultSet rs) throws SQLException {
        WholesaleAudit audit = new WholesaleAudit();
        audit.setAuditId(rs.getInt("AuditID"));
        audit.setBillId(rs.getString("BillId"));
        audit.setAction(rs.getString("Action"));
        audit.setUserId(rs.getInt("UserID"));
        audit.setUsername(rs.getString("Username"));
        audit.setUserFullName(rs.getString("UserFullName"));
        audit.setActionTimestamp(rs.getTimestamp("ActionTimestamp"));
        audit.setIpAddress(rs.getString("IPAddress"));
        audit.setDetails(rs.getString("Details"));
        audit.setOldValues(rs.getString("OldValues"));
        audit.setNewValues(rs.getString("NewValues"));
        audit.setCustomerId(rs.getInt("CustomerID"));
        audit.setCustomerName(rs.getString("CustomerName"));
        
        BigDecimal totalAmount = rs.getBigDecimal("TotalAmount");
        audit.setTotalAmount(totalAmount != null ? totalAmount : BigDecimal.ZERO);
        
        BigDecimal paymentReceived = rs.getBigDecimal("PaymentReceived");
        audit.setPaymentReceived(paymentReceived != null ? paymentReceived : BigDecimal.ZERO);
        
        BigDecimal outstanding = rs.getBigDecimal("Outstanding");
        audit.setOutstanding(outstanding != null ? outstanding : BigDecimal.ZERO);
        
        return audit;
    }
}