package dao;

import db.ConnectionFactory;
import models.RepairItem;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepairItemsDAO {
    private final Connection connection;

    public RepairItemsDAO() throws SQLException {
        connection = ConnectionFactory.getConnection();
    }

    // ✅ Add a single repair item (WITH DISCOUNT SUPPORT)
    public void addRepairItem(RepairItem repairItem) throws SQLException {
        String query = "INSERT INTO RepairItems (REPAIRCODE, ITEMNAME, WARRANTY, QUANTITY, PRICE, TOTAL, " +
                       "DISCOUNT, DISCOUNTTYPE, DISCOUNTAMOUNT, FINALTOTAL) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            BigDecimal discount = repairItem.getDiscount() != null ? repairItem.getDiscount() : BigDecimal.ZERO;
            String discountType = repairItem.getDiscountType() != null ? repairItem.getDiscountType() : "NONE";
            BigDecimal discountAmount = repairItem.getDiscountAmount() != null ? repairItem.getDiscountAmount() : BigDecimal.ZERO;
            BigDecimal finalTotal = repairItem.getFinalTotal() != null ? repairItem.getFinalTotal() : repairItem.getTotal();
            
            stmt.setString(1, repairItem.getRepairId());
            stmt.setString(2, repairItem.getItemName());
            stmt.setString(3, repairItem.getWarranty());
            stmt.setInt(4, repairItem.getQuantity());
            stmt.setBigDecimal(5, repairItem.getPrice());
            stmt.setBigDecimal(6, repairItem.getTotal());
            stmt.setBigDecimal(7, discount);
            stmt.setString(8, discountType);
            stmt.setBigDecimal(9, discountAmount);
            stmt.setBigDecimal(10, finalTotal);
            
            stmt.executeUpdate();

            // Retrieve generated ID
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    repairItem.setRepairItemId(generatedKeys.getInt(1));
                    System.out.println("✓ Saved repair item: " + repairItem.getItemName() + 
                        " | ID: " + repairItem.getRepairItemId() + 
                        " | Discount: " + discount + " | Type: " + discountType + 
                        " | FinalTotal: " + finalTotal);
                }
            }
        }
    }

    // ✅ Add multiple repair items - BATCH INSERT (WITH DISCOUNT SUPPORT)
    public boolean addRepairItems(String repairCode, List<RepairItem> items) throws SQLException {
        String sql = "INSERT INTO RepairItems (REPAIRCODE, ITEMNAME, WARRANTY, QUANTITY, PRICE, TOTAL, " +
                     "DISCOUNT, DISCOUNTTYPE, DISCOUNTAMOUNT, FINALTOTAL) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        System.out.println("=== RepairItemsDAO.addRepairItems - Starting batch insert ===");
        System.out.println("RepairCode: " + repairCode + " | Items count: " + items.size());
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            int itemIndex = 0;
            for (RepairItem item : items) {
                BigDecimal discount = item.getDiscount() != null ? item.getDiscount() : BigDecimal.ZERO;
                String discountType = item.getDiscountType() != null ? item.getDiscountType() : "NONE";
                BigDecimal discountAmount = item.getDiscountAmount() != null ? item.getDiscountAmount() : BigDecimal.ZERO;
                BigDecimal finalTotal = item.getFinalTotal() != null ? item.getFinalTotal() : item.getTotal();
                
                System.out.println("  [" + itemIndex + "] " + item.getItemName());
                System.out.println("      Discount: " + discount + " | Type: " + discountType + 
                                 " | Amount: " + discountAmount + " | FinalTotal: " + finalTotal);
                
                stmt.setString(1, repairCode);
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
        }
    }

    // ✅ Fetch all repair items by repairCode (WITH DISCOUNT SUPPORT)
    public List<RepairItem> getRepairItemsByRepairCode(String repairCode) throws SQLException {
        String query = "SELECT * FROM RepairItems WHERE REPAIRCODE = ?";
        List<RepairItem> repairItems = new ArrayList<>();
        
        System.out.println("=== Loading items for RepairCode: " + repairCode + " ===");
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, repairCode);
            
            try (ResultSet rs = stmt.executeQuery()) {
                int count = 0;
                while (rs.next()) {
                    RepairItem item = new RepairItem();
                    
                    item.setRepairItemId(rs.getInt("ID"));
                    item.setRepairId(rs.getString("REPAIRCODE"));
                    item.setItemName(rs.getString("ITEMNAME"));
                    item.setWarranty(rs.getString("WARRANTY"));
                    item.setQuantity(rs.getInt("QUANTITY"));
                    item.setPrice(rs.getBigDecimal("PRICE"));
                    item.setTotal(rs.getBigDecimal("TOTAL"));
                    
                    // ✅ Load discount fields
                    BigDecimal discount = rs.getBigDecimal("DISCOUNT");
                    item.setDiscount(discount != null ? discount : BigDecimal.ZERO);
                    
                    String discountType = rs.getString("DISCOUNTTYPE");
                    item.setDiscountType(discountType != null && !discountType.isEmpty() ? discountType : "NONE");
                    
                    BigDecimal discountAmount = rs.getBigDecimal("DISCOUNTAMOUNT");
                    item.setDiscountAmount(discountAmount != null ? discountAmount : BigDecimal.ZERO);
                    
                    BigDecimal finalTotal = rs.getBigDecimal("FINALTOTAL");
                    item.setFinalTotal(finalTotal != null ? finalTotal : item.getTotal());
                    
                    System.out.println("  [" + count + "] Loaded: " + item.getItemName() + 
                        " | DiscountType: " + item.getDiscountType() + 
                        " | Discount: " + item.getDiscount() +
                        " | FinalTotal: " + item.getFinalTotal());
                    
                    repairItems.add(item);
                    count++;
                }
                System.out.println("=== Loaded " + count + " items ===");
            }
        }
        return repairItems;
    }

    // ✅ Update a RepairItem (WITH DISCOUNT SUPPORT)
    public void updateRepairItem(RepairItem repairItem) throws SQLException {
        String query = "UPDATE RepairItems SET ITEMNAME = ?, WARRANTY = ?, QUANTITY = ?, PRICE = ?, " +
                       "TOTAL = ?, DISCOUNT = ?, DISCOUNTTYPE = ?, DISCOUNTAMOUNT = ?, FINALTOTAL = ? " +
                       "WHERE ID = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, repairItem.getItemName());
            stmt.setString(2, repairItem.getWarranty());
            stmt.setInt(3, repairItem.getQuantity());
            stmt.setBigDecimal(4, repairItem.getPrice());
            stmt.setBigDecimal(5, repairItem.getTotal());
            stmt.setBigDecimal(6, repairItem.getDiscount() != null ? repairItem.getDiscount() : BigDecimal.ZERO);
            stmt.setString(7, repairItem.getDiscountType() != null ? repairItem.getDiscountType() : "NONE");
            stmt.setBigDecimal(8, repairItem.getDiscountAmount() != null ? repairItem.getDiscountAmount() : BigDecimal.ZERO);
            stmt.setBigDecimal(9, repairItem.getFinalTotal() != null ? repairItem.getFinalTotal() : repairItem.getTotal());
            stmt.setInt(10, repairItem.getRepairItemId());
            
            int rowsUpdated = stmt.executeUpdate();
            
            if (rowsUpdated > 0) {
                System.out.println("✓ Updated repair item ID: " + repairItem.getRepairItemId() + 
                    " | " + repairItem.getItemName());
            } else {
                System.out.println("✗ No repair item found with ID: " + repairItem.getRepairItemId());
            }
        }
    }

    // ✅ Delete a single RepairItem by ID
    public void deleteRepairItem(int repairItemId) throws SQLException {
        String query = "DELETE FROM RepairItems WHERE ID = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, repairItemId);
            int rowsDeleted = stmt.executeUpdate();
            
            if (rowsDeleted > 0) {
                System.out.println("✓ Deleted repair item ID: " + repairItemId);
            } else {
                System.out.println("✗ No repair item found with ID: " + repairItemId);
            }
        }
    }

    // ✅ Delete all repair items by RepairCode
    public void deleteRepairItemsByCode(String repairCode) throws SQLException {
        String query = "DELETE FROM RepairItems WHERE REPAIRCODE = ?";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, repairCode);
            int rowsDeleted = stmt.executeUpdate();
            
            System.out.println("✓ Deleted " + rowsDeleted + " repair items for RepairCode: " + repairCode);
        }
    }

    // ✅ Calculate cost for repair items (for profit calculation)
    public BigDecimal calculateRepairItemsCost(String repairCode) throws SQLException {
        String sql = "SELECT ri.ITEMNAME, ri.QUANTITY, i.CostPrice " +
                     "FROM RepairItems ri " +
                     "LEFT JOIN Items i ON UPPER(i.Name) = UPPER(ri.ITEMNAME) " +
                     "WHERE ri.REPAIRCODE = ?";
        
        BigDecimal totalCost = BigDecimal.ZERO;
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, repairCode);
            
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

    // ✅ Check if repair has items
    public boolean repairHasItems(String repairCode) throws SQLException {
        String sql = "SELECT COUNT(*) FROM RepairItems WHERE REPAIRCODE = ?";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, repairCode);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    // ✅ Get repair item by ID (WITH DISCOUNT SUPPORT)
    public RepairItem getRepairItemById(int repairItemId) throws SQLException {
        String sql = "SELECT * FROM RepairItems WHERE ID = ?";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, repairItemId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    RepairItem item = new RepairItem();
                    
                    item.setRepairItemId(rs.getInt("ID"));
                    item.setRepairId(rs.getString("REPAIRCODE"));
                    item.setItemName(rs.getString("ITEMNAME"));
                    item.setWarranty(rs.getString("WARRANTY"));
                    item.setQuantity(rs.getInt("QUANTITY"));
                    item.setPrice(rs.getBigDecimal("PRICE"));
                    item.setTotal(rs.getBigDecimal("TOTAL"));
                    
                    BigDecimal discount = rs.getBigDecimal("DISCOUNT");
                    item.setDiscount(discount != null ? discount : BigDecimal.ZERO);
                    
                    String discountType = rs.getString("DISCOUNTTYPE");
                    item.setDiscountType(discountType != null ? discountType : "NONE");
                    
                    BigDecimal discountAmount = rs.getBigDecimal("DISCOUNTAMOUNT");
                    item.setDiscountAmount(discountAmount != null ? discountAmount : BigDecimal.ZERO);
                    
                    BigDecimal finalTotal = rs.getBigDecimal("FINALTOTAL");
                    item.setFinalTotal(finalTotal != null ? finalTotal : item.getTotal());
                    
                    return item;
                }
            }
        }
        return null;
    }

    // ✅ Get all repair items (WITH DISCOUNT SUPPORT)
    public List<RepairItem> getAllRepairItems() throws SQLException {
        String sql = "SELECT * FROM RepairItems ORDER BY REPAIRCODE DESC, ID ASC";
        List<RepairItem> items = new ArrayList<>();
        
        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                RepairItem item = new RepairItem();
                
                item.setRepairItemId(rs.getInt("ID"));
                item.setRepairId(rs.getString("REPAIRCODE"));
                item.setItemName(rs.getString("ITEMNAME"));
                item.setWarranty(rs.getString("WARRANTY"));
                item.setQuantity(rs.getInt("QUANTITY"));
                item.setPrice(rs.getBigDecimal("PRICE"));
                item.setTotal(rs.getBigDecimal("TOTAL"));
                
                BigDecimal discount = rs.getBigDecimal("DISCOUNT");
                item.setDiscount(discount != null ? discount : BigDecimal.ZERO);
                
                String discountType = rs.getString("DISCOUNTTYPE");
                item.setDiscountType(discountType != null ? discountType : "NONE");
                
                BigDecimal discountAmount = rs.getBigDecimal("DISCOUNTAMOUNT");
                item.setDiscountAmount(discountAmount != null ? discountAmount : BigDecimal.ZERO);
                
                BigDecimal finalTotal = rs.getBigDecimal("FINALTOTAL");
                item.setFinalTotal(finalTotal != null ? finalTotal : item.getTotal());
                
                items.add(item);
            }
        }
        
        return items;
    }

    // ✅ Get count of items for a repair
    public int getItemCount(String repairCode) throws SQLException {
        String sql = "SELECT COUNT(*) AS ItemCount FROM RepairItems WHERE REPAIRCODE = ?";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, repairCode);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("ItemCount");
                }
            }
        }
        return 0;
    }

    // ✅ Get total items amount for a repair (sum of final totals - INCLUDES DISCOUNTS)
    public BigDecimal getTotalItemsAmount(String repairCode) throws SQLException {
        String sql = "SELECT SUM(FINALTOTAL) AS TotalAmount FROM RepairItems WHERE REPAIRCODE = ?";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, repairCode);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal total = rs.getBigDecimal("TotalAmount");
                    return total != null ? total : BigDecimal.ZERO;
                }
            }
        }
        return BigDecimal.ZERO;
    }

    // ✅ Get total discount amount for a repair
    public BigDecimal getTotalDiscountAmount(String repairCode) throws SQLException {
        String sql = "SELECT SUM(DISCOUNTAMOUNT) AS TotalDiscount FROM RepairItems WHERE REPAIRCODE = ?";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, repairCode);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal total = rs.getBigDecimal("TotalDiscount");
                    return total != null ? total : BigDecimal.ZERO;
                }
            }
        }
        return BigDecimal.ZERO;
    }

    // ✅ Calculate total cost for multiple repairs (for reports)
    public BigDecimal calculateTotalCostForRepairs(List<String> repairCodes) throws SQLException {
        if (repairCodes.isEmpty()) return BigDecimal.ZERO;
        
        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < repairCodes.size(); i++) {
            placeholders.append("?");
            if (i < repairCodes.size() - 1) placeholders.append(",");
        }
        
        String sql = "SELECT ri.ITEMNAME, ri.QUANTITY, i.CostPrice " +
                     "FROM RepairItems ri " +
                     "LEFT JOIN Items i ON UPPER(i.Name) = UPPER(ri.ITEMNAME) " +
                     "WHERE ri.REPAIRCODE IN (" + placeholders + ")";
        
        BigDecimal totalCost = BigDecimal.ZERO;
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            for (int i = 0; i < repairCodes.size(); i++) {
                stmt.setString(i + 1, repairCodes.get(i));
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

    // ✅ Get top sold items from repairs (for analytics)
    public List<Object[]> getTopSoldItemsFromRepairs(int limit) throws SQLException {
        String sql = "SELECT ITEMNAME, SUM(QUANTITY) AS TotalSold " +
                     "FROM RepairItems " +
                     "GROUP BY ITEMNAME " +
                     "ORDER BY TotalSold DESC " +
                     "LIMIT ?";

        List<Object[]> topItems = new ArrayList<>();
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, limit);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    topItems.add(new Object[]{
                        rs.getString("ITEMNAME"), 
                        rs.getInt("TotalSold")
                    });
                }
            }
        }
        return topItems;
    }

    // ✅ Close connection
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✓ RepairItemsDAO connection closed");
            }
        } catch (SQLException e) {
            System.err.println("Error closing RepairItemsDAO connection: " + e.getMessage());
        }
    }
}