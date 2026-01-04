// dao/RefundDAO.java
package dao;

import db.ConnectionFactory;
import models.Refund;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RefundDAO {
    
    // Add a new refund record
    public void addRefund(Refund refund) throws SQLException {
        String query = "INSERT INTO Refunds (RefundCode, RepairCode, CustomerName, ContactNumber, " +
                      "RefundAmount, RefundReason, RefundedBy, RefundDate, Items, Notes) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, refund.getRefundCode());
            stmt.setString(2, refund.getRepairCode());
            stmt.setString(3, refund.getCustomerName());
            stmt.setString(4, refund.getContactNumber());
            stmt.setBigDecimal(5, refund.getRefundAmount());
            stmt.setString(6, refund.getRefundReason());
            stmt.setString(7, refund.getRefundedBy());
            stmt.setTimestamp(8, refund.getRefundDate());
            stmt.setString(9, refund.getItems());
            stmt.setString(10, refund.getNotes());
            
            stmt.executeUpdate();
        }
    }
    
    // Get refund by code
    public Refund getRefundByCode(String refundCode) throws SQLException {
        String query = "SELECT * FROM Refunds WHERE RefundCode = ?";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, refundCode);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractRefund(rs);
                }
            }
        }
        return null;
    }
    
    // Get all refunds
    public List<Refund> getAllRefunds() throws SQLException {
        String query = "SELECT * FROM Refunds ORDER BY RefundDate DESC";
        List<Refund> refunds = new ArrayList<>();
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                refunds.add(extractRefund(rs));
            }
        }
        return refunds;
    }
    
    // Get refunds by repair code
    public List<Refund> getRefundsByRepairCode(String repairCode) throws SQLException {
        String query = "SELECT * FROM Refunds WHERE RepairCode = ?";
        List<Refund> refunds = new ArrayList<>();
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, repairCode);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    refunds.add(extractRefund(rs));
                }
            }
        }
        return refunds;
    }
    
    // Check if repair has been refunded
    public boolean isRepairRefunded(String repairCode) throws SQLException {
        String query = "SELECT COUNT(*) FROM Refunds WHERE RepairCode = ?";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, repairCode);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    
    
    // Add this method to check if a bill has been refunded
public boolean isBillRefunded(String billCode) throws SQLException {
    String query = "SELECT COUNT(*) FROM Refunds WHERE RepairCode = ? AND RefundCode LIKE 'BILL-REFUND%'";
    
    try (Connection conn = ConnectionFactory.getConnection();
         PreparedStatement stmt = conn.prepareStatement(query)) {
        
        stmt.setString(1, billCode);
        
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
    }
    return false;
}

// Get refunds by bill code (reusing RepairCode field for bill code)
public List<Refund> getRefundsByBillCode(String billCode) throws SQLException {
    String query = "SELECT * FROM Refunds WHERE RepairCode = ? AND RefundCode LIKE 'BILL-REFUND%'";
    List<Refund> refunds = new ArrayList<>();
    
    try (Connection conn = ConnectionFactory.getConnection();
         PreparedStatement stmt = conn.prepareStatement(query)) {
        
        stmt.setString(1, billCode);
        
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                refunds.add(extractRefund(rs));
            }
        }
    }
    return refunds;
}
    
    // Helper method to extract refund from ResultSet
    private Refund extractRefund(ResultSet rs) throws SQLException {
        Refund refund = new Refund();
        refund.setRefundID(rs.getInt("RefundID"));
        refund.setRefundCode(rs.getString("RefundCode"));
        refund.setRepairCode(rs.getString("RepairCode"));
        refund.setCustomerName(rs.getString("CustomerName"));
        refund.setContactNumber(rs.getString("ContactNumber"));
        refund.setRefundAmount(rs.getBigDecimal("RefundAmount"));
        refund.setRefundReason(rs.getString("RefundReason"));
        refund.setRefundedBy(rs.getString("RefundedBy"));
        refund.setRefundDate(rs.getTimestamp("RefundDate"));
        refund.setItems(rs.getString("Items"));
        refund.setNotes(rs.getString("Notes"));
        return refund;
    }
}