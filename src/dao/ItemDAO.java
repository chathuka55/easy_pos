package dao;

import db.ConnectionFactory;
import models.Item;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ItemDAO implements BaseDAO<Item> {

    @Override
public boolean add(Item item) throws SQLException {
    String sql = "INSERT INTO Items (ItemCode, Name, SupplierID, Category, RetailPrice, " +
                 "WholesalePrice, Quantity, ReorderLevel, BarCode, IsOldStock, CostPrice, " +
                 "AddedDate, LastModifiedDate, WarrantyPeriod) " +
                 "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    try (Connection conn = ConnectionFactory.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, item.getItemCode());
        stmt.setString(2, item.getName());
        stmt.setInt(3, item.getSupplierID());
        stmt.setString(4, item.getCategory());
        stmt.setDouble(5, item.getRetailPrice());
        stmt.setDouble(6, item.getWholesalePrice());
        stmt.setInt(7, item.getQuantity());
        stmt.setInt(8, item.getReorderLevel());
        stmt.setString(9, item.getBarCode());
        stmt.setBoolean(10, item.isOldStock());
        stmt.setDouble(11, item.getCostPrice());
        stmt.setTimestamp(12, Timestamp.valueOf(LocalDateTime.now()));
        stmt.setTimestamp(13, Timestamp.valueOf(LocalDateTime.now()));
        stmt.setString(14, item.getWarrantyPeriod() != null ? item.getWarrantyPeriod() : "No Warranty");
        return stmt.executeUpdate() > 0;
    }
}

    // Update the update method to include warranty
@Override
public boolean update(Item item) throws SQLException {
    String sql = "UPDATE Items SET Name = ?, SupplierID = ?, Category = ?, RetailPrice = ?, " +
                 "WholesalePrice = ?, Quantity = ?, ReorderLevel = ?, BarCode = ?, " +
                 "IsOldStock = ?, CostPrice = ?, LastModifiedDate = ?, WarrantyPeriod = ? " +
                 "WHERE ItemCode = ?";
    try (Connection conn = ConnectionFactory.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, item.getName());
        stmt.setInt(2, item.getSupplierID());
        stmt.setString(3, item.getCategory());
        stmt.setDouble(4, item.getRetailPrice());
        stmt.setDouble(5, item.getWholesalePrice());
        stmt.setInt(6, item.getQuantity());
        stmt.setInt(7, item.getReorderLevel());
        stmt.setString(8, item.getBarCode());
        stmt.setBoolean(9, item.isOldStock());
        stmt.setDouble(10, item.getCostPrice());
        stmt.setTimestamp(11, Timestamp.valueOf(LocalDateTime.now()));
        stmt.setString(12, item.getWarrantyPeriod() != null ? item.getWarrantyPeriod() : "No Warranty");
        stmt.setString(13, item.getItemCode());
        return stmt.executeUpdate() > 0;
    }
}


    // Method to add quantity to existing item
    public boolean addQuantity(String itemCode, int quantityToAdd) throws SQLException {
        String sql = "UPDATE Items SET Quantity = Quantity + ?, LastModifiedDate = ? WHERE ItemCode = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, quantityToAdd);
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setString(3, itemCode);
            return stmt.executeUpdate() > 0;
        }
    }

    // Keep all existing methods and add date fields to extractItem method
   // Update extractItem method to include warranty
private Item extractItem(ResultSet rs) throws SQLException {
    Item item = new Item();
    item.setItemID(rs.getInt("ItemID"));
    item.setItemCode(rs.getString("ItemCode"));
    item.setName(rs.getString("Name"));
    item.setSupplierID(rs.getInt("SupplierID"));
    item.setCategory(rs.getString("Category"));
    item.setRetailPrice(rs.getDouble("RetailPrice"));
    item.setWholesalePrice(rs.getDouble("WholesalePrice"));
    item.setQuantity(rs.getInt("Quantity"));
    item.setReorderLevel(rs.getInt("ReorderLevel"));
    item.setBarCode(rs.getString("BarCode"));
    item.setOldStock(rs.getBoolean("IsOldStock"));
    
    // Add date fields
    Timestamp addedDate = rs.getTimestamp("AddedDate");
    if (addedDate != null) {
        item.setAddedDate(addedDate.toLocalDateTime());
    }
    
    Timestamp modifiedDate = rs.getTimestamp("LastModifiedDate");
    if (modifiedDate != null) {
        item.setLastModifiedDate(modifiedDate.toLocalDateTime());
    }
    
    item.setCostPrice(rs.getDouble("CostPrice"));
    
    // Add warranty field
    try {
        item.setWarrantyPeriod(rs.getString("WarrantyPeriod"));
    } catch (SQLException e) {
        item.setWarrantyPeriod("No Warranty");
    }
    
    return item;
}

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM Items WHERE ItemID = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public Item getById(int id) throws SQLException {
        String sql = "SELECT * FROM Items WHERE ItemID = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractItem(rs);
                }
            }
        }
        return null;
    }

   @Override
public List<Item> getAll() throws SQLException {
    String sql = "SELECT * FROM Items";
    List<Item> items = new ArrayList<>();
    try (Connection conn = ConnectionFactory.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
            Item item = new Item();
            item.setItemID(rs.getInt("ItemID"));
            item.setItemCode(rs.getString("ItemCode"));
            item.setName(rs.getString("Name"));
            item.setSupplierID(rs.getInt("SupplierID"));
            item.setCategory(rs.getString("Category"));
            item.setRetailPrice(rs.getDouble("RetailPrice"));
            item.setWholesalePrice(rs.getDouble("WholesalePrice"));
            item.setCostPrice(rs.getDouble("CostPrice"));  // ADD THIS LINE - FIX
            item.setQuantity(rs.getInt("Quantity"));
            item.setReorderLevel(rs.getInt("ReorderLevel"));
            item.setBarCode(rs.getString("BarCode"));
            item.setOldStock(rs.getBoolean("IsOldStock"));
            
            // Add warranty field
            try {
                item.setWarrantyPeriod(rs.getString("WarrantyPeriod"));
            } catch (SQLException e) {
                item.setWarrantyPeriod("No Warranty");
            }
            
            items.add(item);
        }
    }
    return items;
}

       
    public Item getByCode(String itemCode) throws SQLException {
    String sql = "SELECT * FROM Items WHERE ItemCode = ?";
    try (Connection conn = ConnectionFactory.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, itemCode);
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return extractItem(rs);  // Use extractItem method which now includes everything
            }
        }
    }
    return null;
}
    
    // Add this method for exact match search (useful for barcode scanning)
public Item getByBarcodeExact(String barcode) throws SQLException {
    String sql = "SELECT * FROM Items WHERE BarCode = ?";
    try (Connection conn = ConnectionFactory.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, barcode);
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return extractItem(rs);
            }
        }
    }
    return null;
}

// Add this method for searching by multiple criteria
public List<Item> advancedSearch(String searchText, String category, Integer supplierId) throws SQLException {
    StringBuilder sql = new StringBuilder("SELECT * FROM Items WHERE 1=1");
    List<Object> params = new ArrayList<>();
    
    if (searchText != null && !searchText.trim().isEmpty()) {
        sql.append(" AND (UPPER(Name) LIKE UPPER(?) OR UPPER(ItemCode) LIKE UPPER(?) OR UPPER(BarCode) LIKE UPPER(?))");
        String pattern = "%" + searchText.trim() + "%";
        params.add(pattern);
        params.add(pattern);
        params.add(pattern);
    }
    
    if (category != null && !category.trim().isEmpty()) {
        sql.append(" AND UPPER(Category) = UPPER(?)");
        params.add(category.trim());
    }
    
    if (supplierId != null) {
        sql.append(" AND SupplierID = ?");
        params.add(supplierId);
    }
    
    sql.append(" ORDER BY Name");
    
    List<Item> items = new ArrayList<>();
    try (Connection conn = ConnectionFactory.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
        
        for (int i = 0; i < params.size(); i++) {
            stmt.setObject(i + 1, params.get(i));
        }
        
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                items.add(extractItem(rs));
            }
        }
    }
    return items;
}
    
    public Item getByName(String itemCode) throws SQLException {
        String sql = "SELECT * FROM Items WHERE ItemName = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, itemCode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Item item = new Item();
                    item.setItemID(rs.getInt("ItemID"));
                    item.setItemCode(rs.getString("ItemCode"));
                    item.setName(rs.getString("Name"));
                    item.setSupplierID(rs.getInt("SupplierID"));
                    item.setCategory(rs.getString("Category"));
                    item.setRetailPrice(rs.getDouble("RetailPrice"));
                    item.setWholesalePrice(rs.getDouble("WholesalePrice"));
                    item.setQuantity(rs.getInt("Quantity"));
                    item.setReorderLevel(rs.getInt("ReorderLevel"));
                    item.setBarCode(rs.getString("BarCode"));
                    item.setOldStock(rs.getBoolean("IsOldStock"));
                    return item;
                }
            }
        }
        return null;
    }

    public List<Item> getLowStockItems(int threshold) throws SQLException {
        String sql = "SELECT * FROM Items WHERE Quantity <= ?";
        List<Item> lowStockItems = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, threshold);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lowStockItems.add(extractItem(rs));
                }
            }
        }
        return lowStockItems;
    }
    
     // Updated searchByNameOrCode method in ItemDAO for case-insensitive search
public List<Item> searchByNameOrCode(String searchText) throws SQLException {
    // Use UPPER or LOWER for case-insensitive comparison
    String sql = "SELECT * FROM Items WHERE UPPER(Name) LIKE UPPER(?) OR UPPER(ItemCode) LIKE UPPER(?) OR UPPER(BarCode) LIKE UPPER(?)";
    List<Item> items = new ArrayList<>();
    
    try (Connection conn = ConnectionFactory.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        String searchPattern = "%" + searchText.trim() + "%";
        stmt.setString(1, searchPattern);
        stmt.setString(2, searchPattern);
        stmt.setString(3, searchPattern); // Also search in barcode
        
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                items.add(extractItem(rs));
            }
        }
    }
    return items;
}

    
     public boolean deleteByCode(String itemCode) throws SQLException {
        String sql = "DELETE FROM Items WHERE ItemCode = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, itemCode);
            return stmt.executeUpdate() > 0;
        }
    }
     
    public void reduceQuantity(int itemID, int quantity) throws SQLException {
    String query = "UPDATE Items SET Quantity = Quantity - ? WHERE ItemID = ?";
    try (Connection conn = ConnectionFactory.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(query)) {
        pstmt.setInt(1, quantity);  // Reduce by the quantity sold
        pstmt.setInt(2, itemID);    // Where ItemID matches
        pstmt.executeUpdate();
    }
}
    
    
    public void updateItem(Item item) throws SQLException {
    String sql = "UPDATE Items SET Quantity = ? WHERE ItemID = ?";
    try (Connection conn = ConnectionFactory.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, item.getQuantity());
        stmt.setInt(2, item.getItemID());
        stmt.executeUpdate();
    }
}
    
    public int getItemIDByCode(String itemCode) throws SQLException {
    String query = "SELECT ItemID FROM Items WHERE itemCode = ?";
    try (Connection conn = ConnectionFactory.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(query)) {
        pstmt.setString(1, itemCode);
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("ItemID");
            }
        }
    }
    throw new SQLException("Item with code " + itemCode + " not found.");
}
    
    public void restoreQuantityByCode(String itemCode, int quantity) throws SQLException {
    String query = "UPDATE Items SET Quantity = Quantity + ? WHERE ItemCode = ?";
    try (Connection conn = ConnectionFactory.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(query)) {
        pstmt.setInt(1, quantity);  // Restore quantity
        pstmt.setString(2, itemCode);  // Where ItemCode matches
        pstmt.executeUpdate();
    }
}

    public boolean deleteItem(String itemCode) throws SQLException {
    String sql = "DELETE FROM Items WHERE ItemCode = ?";
    try (Connection conn = ConnectionFactory.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, itemCode);
        return stmt.executeUpdate() > 0;
    }
}

    
}
