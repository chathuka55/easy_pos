/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import models.Repair;
import db.ConnectionFactory; // Assuming you have this for managing connections
import java.math.BigDecimal;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RepairsDAO {
    private final Connection connection;

    public RepairsDAO() throws SQLException {
        connection = ConnectionFactory.getConnection(); // Use your connection method
    }

   // Updated addRepair method with user tracking
    public void addRepair(Repair repair) throws SQLException {
        String query = "INSERT INTO Repairs (repairCode, customerName, contactNumber, repairType, " +
                      "repairProgress, serviceCharge, totalAmount, discount, paidAmount, balanceAmount, " +
                      "paymentMethod, conditions, borrowedItems, notes, repairDate, " +
                      "createdByUserID, createdByUsername, createdByFullName) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, repair.getRepairCode());
            stmt.setString(2, repair.getCustomerName());
            stmt.setString(3, repair.getContactNumber());
            stmt.setString(4, repair.getRepairType());
            stmt.setString(5, repair.getRepairProgress());
            stmt.setBigDecimal(6, repair.getServiceCharge());
            stmt.setBigDecimal(7, repair.getTotalAmount());
            stmt.setBigDecimal(8, repair.getDiscount());
            stmt.setBigDecimal(9, repair.getPaidAmount());
            stmt.setBigDecimal(10, repair.getBalanceAmount());
            stmt.setString(11, repair.getPaymentMethod());
            stmt.setString(12, repair.getConditions());
            stmt.setString(13, repair.getBorrowedItems());
            stmt.setString(14, repair.getNotes());
            stmt.setTimestamp(15, repair.getRepairDate());
            stmt.setInt(16, repair.getCreatedByUserID());
            stmt.setString(17, repair.getCreatedByUsername());
            stmt.setString(18, repair.getCreatedByFullName());
            
            stmt.executeUpdate();
        }
    }

    // Updated getRepairByCode method
    public Repair getRepairByCode(String repairCode) throws SQLException {
        String query = "SELECT * FROM Repairs WHERE repairCode = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, repairCode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRepair(rs);
                }
            }
        }
        return null;
    }

// Updated getAllRepairs method
    public List<Repair> getAllRepairs() throws SQLException {
        String query = "SELECT * FROM Repairs ORDER BY repairDate DESC";
        List<Repair> repairs = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                repairs.add(mapResultSetToRepair(rs));
            }
        }
        return repairs;
    }

    // Updated updateRepair method with user tracking
    public void updateRepair(Repair repair) throws SQLException {
        String query = "UPDATE Repairs SET customerName = ?, contactNumber = ?, repairType = ?, " +
                      "repairProgress = ?, serviceCharge = ?, totalAmount = ?, discount = ?, " +
                      "paidAmount = ?, balanceAmount = ?, paymentMethod = ?, conditions = ?, " +
                      "borrowedItems = ?, notes = ?, lastModifiedByUserID = ?, " +
                      "lastModifiedByUsername = ?, lastModifiedDate = ? WHERE repairCode = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, repair.getCustomerName());
            stmt.setString(2, repair.getContactNumber());
            stmt.setString(3, repair.getRepairType());
            stmt.setString(4, repair.getRepairProgress());
            stmt.setBigDecimal(5, repair.getServiceCharge());
            stmt.setBigDecimal(6, repair.getTotalAmount());
            stmt.setBigDecimal(7, repair.getDiscount());
            stmt.setBigDecimal(8, repair.getPaidAmount());
            stmt.setBigDecimal(9, repair.getBalanceAmount());
            stmt.setString(10, repair.getPaymentMethod());
            stmt.setString(11, repair.getConditions());
            stmt.setString(12, repair.getBorrowedItems());
            stmt.setString(13, repair.getNotes());
            stmt.setInt(14, repair.getLastModifiedByUserID());
            stmt.setString(15, repair.getLastModifiedByUsername());
            stmt.setTimestamp(16, new Timestamp(System.currentTimeMillis()));
            stmt.setString(17, repair.getRepairCode());
            stmt.executeUpdate();
        }
    }

    // Helper method to map ResultSet to Repair object
    private Repair mapResultSetToRepair(ResultSet rs) throws SQLException {
        Repair repair = new Repair();
        repair.setRepairCode(rs.getString("repairCode"));
        repair.setCustomerName(rs.getString("customerName"));
        repair.setContactNumber(rs.getString("contactNumber"));
        repair.setRepairType(rs.getString("repairType"));
        repair.setRepairProgress(rs.getString("repairProgress"));
        repair.setServiceCharge(rs.getBigDecimal("serviceCharge"));
        repair.setTotalAmount(rs.getBigDecimal("totalAmount"));
        repair.setDiscount(rs.getBigDecimal("discount"));
        repair.setPaidAmount(rs.getBigDecimal("paidAmount"));
        repair.setBalanceAmount(rs.getBigDecimal("balanceAmount"));
        repair.setPaymentMethod(rs.getString("paymentMethod"));
        repair.setConditions(rs.getString("conditions"));
        repair.setBorrowedItems(rs.getString("borrowedItems"));
        repair.setNotes(rs.getString("notes"));
        repair.setRepairDate(rs.getTimestamp("repairDate"));
        
        // Try to get audit fields (they might not exist in older databases)
        try {
            repair.setCreatedByUserID(rs.getInt("createdByUserID"));
            repair.setCreatedByUsername(rs.getString("createdByUsername"));
            repair.setCreatedByFullName(rs.getString("createdByFullName"));
            repair.setLastModifiedByUserID(rs.getInt("lastModifiedByUserID"));
            repair.setLastModifiedByUsername(rs.getString("lastModifiedByUsername"));
            repair.setLastModifiedDate(rs.getTimestamp("lastModifiedDate"));
        } catch (SQLException e) {
            // Columns might not exist in older databases
        }
        
        return repair;
    }

    // Existing methods remain the same...
    public void deleteRepair(String repairCode) throws SQLException {
        String query = "DELETE FROM Repairs WHERE repairCode = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, repairCode);
            stmt.executeUpdate();
        }
    }

    public boolean repairExists(String repairCode) throws SQLException {
        String query = "SELECT COUNT(*) FROM Repairs WHERE repairCode = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, repairCode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public List<Repair> getCompletedRepairs() throws SQLException {
        String query = "SELECT * FROM Repairs WHERE repairProgress = 'Completed'";
        List<Repair> repairs = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                repairs.add(mapResultSetToRepair(rs));
            }
        }
        return repairs;
    }

    // In RepairsDAO.java, update getRepairsByDate() method
public List<Repair> getRepairsByDate(Date date) throws SQLException {
    String query = "SELECT * FROM Repairs WHERE FORMATDATETIME(repairDate, 'yyyy-MM-dd') = ?";
    List<Repair> repairs = new ArrayList<>();
    
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String dateStr = sdf.format(date);
    
    try (PreparedStatement stmt = connection.prepareStatement(query)) {
        stmt.setString(1, dateStr);
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                repairs.add(mapResultSetToRepair(rs));
            }
        }
    }
    return repairs;
}

// Update getTodayRepairSales() method
public BigDecimal getTodayRepairSales() throws SQLException {
    String query = "SELECT repairProgress, totalAmount, paidAmount FROM Repairs " +
                   "WHERE FORMATDATETIME(repairDate, 'yyyy-MM-dd') = FORMATDATETIME(CURRENT_DATE, 'yyyy-MM-dd')";
    BigDecimal totalSales = BigDecimal.ZERO;
    
    try (PreparedStatement stmt = connection.prepareStatement(query);
         ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
            String progress = rs.getString("repairProgress");
            BigDecimal totalAmount = rs.getBigDecimal("totalAmount");
            BigDecimal paidAmount = rs.getBigDecimal("paidAmount");
            
            // For Pending repairs, only count paid amount
            if ("Pending".equalsIgnoreCase(progress)) {
                totalSales = totalSales.add(paidAmount != null ? paidAmount : BigDecimal.ZERO);
            } 
            // For all other statuses (Completed, In Progress, Handed Over Parts), count full amount
            else {
                totalSales = totalSales.add(totalAmount != null ? totalAmount : BigDecimal.ZERO);
            }
        }
    }
    return totalSales;
}
    // Add method to calculate repair sales with proper logic
public BigDecimal calculateRepairSales(List<Repair> repairs) {
    BigDecimal totalSales = BigDecimal.ZERO;
    
    for (Repair repair : repairs) {
        String progress = repair.getRepairProgress();
        
        // For Pending repairs, only count paid amount
        if ("Pending".equalsIgnoreCase(progress)) {
            totalSales = totalSales.add(repair.getPaidAmount() != null ? 
                repair.getPaidAmount() : BigDecimal.ZERO);
        } 
        // For all other statuses, count full amount
        else {
            totalSales = totalSales.add(repair.getTotalAmount() != null ? 
                repair.getTotalAmount() : BigDecimal.ZERO);
        }
    }
    
    return totalSales;
}

// Add method to get repairs by date range
public List<Repair> getRepairsByDateRange(Date startDate, Date endDate) throws SQLException {
    String query = "SELECT * FROM Repairs WHERE repairDate >= ? AND repairDate <= ? ORDER BY repairDate DESC";
    List<Repair> repairs = new ArrayList<>();
    
    try (PreparedStatement stmt = connection.prepareStatement(query)) {
        stmt.setTimestamp(1, new Timestamp(startDate.getTime()));
        stmt.setTimestamp(2, new Timestamp(endDate.getTime() + 86400000 - 1)); // End of day
        
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                repairs.add(mapResultSetToRepair(rs));
            }
        }
    }
    return repairs;
}
// Get repair statistics with proper sales calculation
public Map<String, BigDecimal> getRepairStatsByStatus() throws SQLException {
    String query = "SELECT repairProgress, totalAmount, paidAmount FROM Repairs";
    Map<String, BigDecimal> stats = new HashMap<>();
    
    try (PreparedStatement stmt = connection.prepareStatement(query);
         ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
            String progress = rs.getString("repairProgress");
            BigDecimal amount;
            
            if ("Pending".equalsIgnoreCase(progress)) {
                amount = rs.getBigDecimal("paidAmount");
            } else {
                amount = rs.getBigDecimal("totalAmount");
            }
            
            if (amount != null) {
                stats.merge(progress, amount, BigDecimal::add);
            }
        }
    }
    return stats;
}




}