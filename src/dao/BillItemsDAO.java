package dao;

import db.ConnectionFactory;
import java.math.BigDecimal;
import models.BillItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BillItemsDAO {

    // ✅ NEW: Overloaded method that accepts Connection (for transactions)
    public boolean addBillItems(Connection conn, int billID, List<BillItem> items) throws SQLException {
        String sql = "INSERT INTO BillItems (BillID, ItemName, Warranty, Quantity, Price, Total, " +
                     "Discount, DiscountType, DiscountAmount, FinalTotal) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        System.out.println("=== BillItemsDAO.addBillItems (with Connection) - Starting batch insert ===");
        System.out.println("BillID: " + billID + " | Items count: " + items.size());
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            int itemIndex = 0;
            for (BillItem item : items) {
                BigDecimal discount = item.getDiscount() != null ? item.getDiscount() : BigDecimal.ZERO;
                String discountType = item.getDiscountType() != null ? item.getDiscountType() : "NONE";
                BigDecimal discountAmount = item.getDiscountAmount() != null ? item.getDiscountAmount() : BigDecimal.ZERO;
                BigDecimal finalTotal = item.getFinalTotal() != null ? item.getFinalTotal() : item.getTotal();
                
                System.out.println("  [" + itemIndex + "] " + item.getItemName());
                System.out.println("      Discount: " + discount + " | Type: " + discountType + 
                                 " | Amount: " + discountAmount + " | FinalTotal: " + finalTotal);
                
                stmt.setInt(1, billID);
                stmt.setString(2, item.getItemName());
                stmt.setString(3, item.getWarranty());
                stmt.setInt(4, item.getQuantity());
                stmt.setBigDecimal(5, item.getPrice());
                stmt.setBigDecimal(6, item.getTotal());
                stmt.setBigDecimal(7, discount);
                stmt.setString(8, discountType);
                stmt.setBigDecimal(9, discountAmount);
                stmt.setBigDecimal(10, finalTotal);
                
                stmt.addBatch();
                itemIndex++;
            }
            
            System.out.println("Executing batch insert...");
            int[] result = stmt.executeBatch();
            
            System.out.println("=== Batch results ===");
            for (int i = 0; i < result.length; i++) {
                System.out.println("  Item " + i + ": " + (result[i] > 0 ? "✓" : "✗"));
                if (result[i] <= 0) return false;
            }
            
            System.out.println("=== All items saved successfully ===");
            return true;
            
        } catch (SQLException e) {
            System.err.println("ERROR in addBillItems: " + e.getMessage());
            throw e;
        }
    }

    // Original method (creates its own connection) - for standalone use
    public boolean addBillItems(int billID, List<BillItem> items) throws SQLException {
        try (Connection conn = ConnectionFactory.getConnection()) {
            return addBillItems(conn, billID, items);
        }
    }

    // Add single bill item
    public boolean addBillItem(int billID, BillItem item) throws SQLException {
        List<BillItem> items = new ArrayList<>();
        items.add(item);
        return addBillItems(billID, items);
    }

    // Get items by BillID
    public List<BillItem> getItemsByBillID(int billID) throws SQLException {
        String sql = "SELECT * FROM BillItems WHERE BillID = ?";
        List<BillItem> items = new ArrayList<>();
        
        System.out.println("=== Loading items for BillID: " + billID + " ===");
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, billID);
            
            try (ResultSet rs = stmt.executeQuery()) {
                int count = 0;
                while (rs.next()) {
                    BillItem item = new BillItem();
                    
                    item.setItemName(rs.getString("ItemName"));
                    item.setWarranty(rs.getString("Warranty"));
                    item.setQuantity(rs.getInt("Quantity"));
                    item.setPrice(rs.getBigDecimal("Price"));
                    item.setTotal(rs.getBigDecimal("Total"));
                    
                    BigDecimal discount = rs.getBigDecimal("Discount");
                    item.setDiscount(discount != null ? discount : BigDecimal.ZERO);
                    
                    String discountType = rs.getString("DiscountType");
                    item.setDiscountType(discountType != null && !discountType.isEmpty() ? discountType : "NONE");
                    
                    BigDecimal discountAmount = rs.getBigDecimal("DiscountAmount");
                    item.setDiscountAmount(discountAmount != null ? discountAmount : BigDecimal.ZERO);
                    
                    BigDecimal finalTotal = rs.getBigDecimal("FinalTotal");
                    item.setFinalTotal(finalTotal != null ? finalTotal : item.getTotal());
                    
                    System.out.println("  [" + count + "] Loaded: " + item.getItemName() + 
                        " | DiscountType: " + item.getDiscountType() + 
                        " | Discount: " + item.getDiscount() +
                        " | FinalTotal: " + item.getFinalTotal());
                    
                    items.add(item);
                    count++;
                }
                System.out.println("=== Loaded " + count + " items ===");
            }
        }
        
        return items;
    }

    // Get items by BillCode
    public List<BillItem> getItemsByBillCode(String billCode) throws SQLException {
        String sql = "SELECT bi.* FROM BillItems bi " +
                     "INNER JOIN Bills b ON bi.BillID = b.BillID " +
                     "WHERE b.BillCode = ?";
        List<BillItem> items = new ArrayList<>();
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, billCode);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    BillItem item = new BillItem();
                    item.setItemName(rs.getString("ItemName"));
                    item.setWarranty(rs.getString("Warranty"));
                    item.setQuantity(rs.getInt("Quantity"));
                    item.setPrice(rs.getBigDecimal("Price"));
                    item.setTotal(rs.getBigDecimal("Total"));
                    
                    BigDecimal discount = rs.getBigDecimal("Discount");
                    item.setDiscount(discount != null ? discount : BigDecimal.ZERO);
                    
                    String discountType = rs.getString("DiscountType");
                    item.setDiscountType(discountType != null ? discountType : "NONE");
                    
                    BigDecimal discountAmount = rs.getBigDecimal("DiscountAmount");
                    item.setDiscountAmount(discountAmount != null ? discountAmount : BigDecimal.ZERO);
                    
                    BigDecimal finalTotal = rs.getBigDecimal("FinalTotal");
                    item.setFinalTotal(finalTotal != null ? finalTotal : item.getTotal());
                    
                    items.add(item);
                }
            }
        }
        return items;
    }

    // Update bill item
    public boolean updateBillItem(int billItemID, BillItem item) throws SQLException {
        String sql = "UPDATE BillItems SET ItemName = ?, Warranty = ?, Quantity = ?, Price = ?, " +
                     "Total = ?, Discount = ?, DiscountType = ?, DiscountAmount = ?, FinalTotal = ? WHERE BillItemID = ?";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, item.getItemName());
            stmt.setString(2, item.getWarranty());
            stmt.setInt(3, item.getQuantity());
            stmt.setBigDecimal(4, item.getPrice());
            stmt.setBigDecimal(5, item.getTotal());
            stmt.setBigDecimal(6, item.getDiscount() != null ? item.getDiscount() : BigDecimal.ZERO);
            stmt.setString(7, item.getDiscountType() != null ? item.getDiscountType() : "NONE");
            stmt.setBigDecimal(8, item.getDiscountAmount() != null ? item.getDiscountAmount() : BigDecimal.ZERO);
            stmt.setBigDecimal(9, item.getFinalTotal() != null ? item.getFinalTotal() : item.getTotal());
            stmt.setInt(10, billItemID);
            
            return stmt.executeUpdate() > 0;
        }
    }

    // Delete items by BillID
    public boolean deleteItemsByBillID(int billID) throws SQLException {
        String sql = "DELETE FROM BillItems WHERE BillID = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, billID);
            return stmt.executeUpdate() > 0;
        }
    }

    // Get bill item by ID
    public BillItem getBillItemByID(int billItemID) throws SQLException {
        String sql = "SELECT * FROM BillItems WHERE BillItemID = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, billItemID);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    BillItem item = new BillItem();
                    item.setItemName(rs.getString("ItemName"));
                    item.setWarranty(rs.getString("Warranty"));
                    item.setQuantity(rs.getInt("Quantity"));
                    item.setPrice(rs.getBigDecimal("Price"));
                    item.setTotal(rs.getBigDecimal("Total"));
                    
                    BigDecimal discount = rs.getBigDecimal("Discount");
                    item.setDiscount(discount != null ? discount : BigDecimal.ZERO);
                    
                    String discountType = rs.getString("DiscountType");
                    item.setDiscountType(discountType != null ? discountType : "NONE");
                    
                    BigDecimal discountAmount = rs.getBigDecimal("DiscountAmount");
                    item.setDiscountAmount(discountAmount != null ? discountAmount : BigDecimal.ZERO);
                    
                    BigDecimal finalTotal = rs.getBigDecimal("FinalTotal");
                    item.setFinalTotal(finalTotal != null ? finalTotal : item.getTotal());
                    
                    return item;
                }
            }
        }
        return null;
    }
    
    public List<Object[]> getTopSoldItems(int limit) throws SQLException {
        String sql = "SELECT ItemName, SUM(Quantity) AS TotalSold FROM BillItems " +
                     "GROUP BY ItemName ORDER BY TotalSold DESC LIMIT ?";

        List<Object[]> topItems = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    topItems.add(new Object[]{rs.getString("ItemName"), rs.getInt("TotalSold")});
                }
            }
        }
        return topItems;
    }

    public BigDecimal calculateBillItemsCost(int billID) throws SQLException {
        String sql = "SELECT bi.ITEMNAME, bi.QUANTITY, i.CostPrice " +
                     "FROM BillItems bi " +
                     "LEFT JOIN Items i ON UPPER(i.Name) = UPPER(bi.ITEMNAME) " +
                     "WHERE bi.BillID = ?";
        
        BigDecimal totalCost = BigDecimal.ZERO;
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, billID);
            
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

    public BigDecimal calculateTotalCostForBills(List<Integer> billIDs) throws SQLException {
        if (billIDs.isEmpty()) return BigDecimal.ZERO;
        
        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < billIDs.size(); i++) {
            placeholders.append("?");
            if (i < billIDs.size() - 1) placeholders.append(",");
        }
        
        String sql = "SELECT bi.ITEMNAME, bi.QUANTITY, i.CostPrice " +
                     "FROM BillItems bi " +
                     "LEFT JOIN Items i ON UPPER(i.Name) = UPPER(bi.ITEMNAME) " +
                     "WHERE bi.BillID IN (" + placeholders + ")";
        
        BigDecimal totalCost = BigDecimal.ZERO;
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            for (int i = 0; i < billIDs.size(); i++) {
                stmt.setInt(i + 1, billIDs.get(i));
            }
            
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