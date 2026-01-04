package dao;

import db.ConnectionFactory;
import models.CheckBillItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class CheckBillItemDAO {

    // Add a check bill item (existing method - keeping as is)
    public void addCheckBillItem(CheckBillItem item) throws SQLException {
        String sql = "INSERT INTO CHECK_BILL_ITEMS (BILLID, ITEMNAME, PRICE, QUANTITY, WARRANTY, TOTAL) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, item.getBillId());
            stmt.setString(2, item.getItemName());
            stmt.setBigDecimal(3, item.getPrice());
            stmt.setInt(4, item.getQuantity());
            stmt.setString(5, item.getWarranty());
            stmt.setBigDecimal(6, item.getTotal());
            stmt.executeUpdate();
        }
    }

    // Retrieve all items for a specific check bill - UPDATED VERSION
public List<CheckBillItem> getItemsByBillId(String billId) throws SQLException {
    List<CheckBillItem> list = new ArrayList<>();
    String sql = "SELECT * FROM CHECK_BILL_ITEMS WHERE BILLID = ?";
    try (Connection conn = ConnectionFactory.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, billId);
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                CheckBillItem item = new CheckBillItem();
                item.setItemId(rs.getInt("ITEMID"));
                item.setBillId(rs.getString("BILLID"));
                item.setItemName(rs.getString("ITEMNAME"));
                item.setPrice(rs.getBigDecimal("PRICE"));
                item.setQuantity(rs.getInt("QUANTITY"));
                item.setWarranty(rs.getString("WARRANTY"));
                item.setTotal(rs.getBigDecimal("TOTAL"));
                list.add(item);
            }
        }
    }
    return list;
}

    // Alternative method name for compatibility
    public List<CheckBillItem> getByBillId(String billId) throws SQLException {
        return getItemsByBillId(billId);
    }

    // NEW: Get total value of items in a bill
    public BigDecimal getTotalValueByBillId(String billId) throws SQLException {
        String sql = "SELECT SUM(TOTAL) as TOTAL_VALUE FROM CHECK_BILL_ITEMS WHERE BILLID = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, billId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal total = rs.getBigDecimal("TOTAL_VALUE");
                    return total != null ? total : BigDecimal.ZERO;
                }
            }
        }
        return BigDecimal.ZERO;
    }

    // NEW: Delete all items for a bill (useful if bill needs to be cancelled)
    public boolean deleteItemsByBillId(String billId) throws SQLException {
        String sql = "DELETE FROM CHECK_BILL_ITEMS WHERE BILLID = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, billId);
            return stmt.executeUpdate() > 0;
        }
    }

    // NEW: Get count of items in a bill
    public int getItemCountByBillId(String billId) throws SQLException {
        String sql = "SELECT COUNT(*) as ITEM_COUNT FROM CHECK_BILL_ITEMS WHERE BILLID = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, billId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("ITEM_COUNT");
                }
            }
        }
        return 0;
    }

    // Helper method to map ResultSet to CheckBillItem object - CORRECTED
    private CheckBillItem mapResultSetToCheckBillItem(ResultSet rs) throws SQLException {
        CheckBillItem item = new CheckBillItem();
        item.setItemId(rs.getInt("ITEMID"));  // Changed from setId to setItemId
        item.setBillId(rs.getString("BILLID"));
        item.setItemName(rs.getString("ITEMNAME"));
        item.setPrice(rs.getBigDecimal("PRICE"));
        item.setQuantity(rs.getInt("QUANTITY"));
        item.setWarranty(rs.getString("WARRANTY"));
        item.setTotal(rs.getBigDecimal("TOTAL"));
        return item;
    }

    // Alternative constructor-based creation if needed
    private CheckBillItem createCheckBillItemFromResultSet(ResultSet rs) throws SQLException {
        return new CheckBillItem(
            rs.getInt("ITEMID"),
            rs.getString("BILLID"),
            rs.getString("ITEMNAME"),
            rs.getBigDecimal("PRICE"),
            rs.getInt("QUANTITY"),
            rs.getString("WARRANTY"),
            rs.getBigDecimal("TOTAL")
        );
    }
    
    // In CheckBillItemDAO.java - Add method to calculate cost for wholesale items
public BigDecimal calculateCheckBillItemsCost(String billId) throws SQLException {
    String sql = "SELECT cbi.ITEMNAME, cbi.QUANTITY, i.CostPrice " +
                 "FROM CHECK_BILL_ITEMS cbi " +
                 "LEFT JOIN Items i ON UPPER(i.Name) = UPPER(cbi.ITEMNAME) " +
                 "WHERE cbi.BILLID = ?";
    
    BigDecimal totalCost = BigDecimal.ZERO;
    
    try (Connection conn = ConnectionFactory.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, billId);
        
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                double costPrice = rs.getDouble("CostPrice");
                int quantity = rs.getInt("QUANTITY");
                
                if (!rs.wasNull() && costPrice > 0) {
                    totalCost = totalCost.add(BigDecimal.valueOf(costPrice * quantity));
                }
            }
        }
    }
    return totalCost;
}
}