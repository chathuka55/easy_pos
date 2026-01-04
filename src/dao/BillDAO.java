package dao;

import db.ConnectionFactory;
import java.math.BigDecimal;
import models.Bill;
import models.BillItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BillDAO {

    // Update the addBill method in BillDAO
    public int addBill(Bill bill) throws SQLException {
    String billSQL = "INSERT INTO Bills (BillCode, CustomerName, TotalAmount, PaidAmount, Balance, " +
                     "Notes, PaymentMethod, CreatedByUserID, CreatedByUsername, CreatedByFullName) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    try (Connection conn = ConnectionFactory.getConnection()) {
        conn.setAutoCommit(false);

        try (PreparedStatement billStmt = conn.prepareStatement(billSQL, Statement.RETURN_GENERATED_KEYS)) {
            billStmt.setString(1, bill.getBillCode());
            billStmt.setString(2, bill.getCustomerName());
            billStmt.setBigDecimal(3, bill.getTotalAmount());
            billStmt.setBigDecimal(4, bill.getPaidAmount());
            billStmt.setBigDecimal(5, bill.getBalance());
            billStmt.setString(6, bill.getNotes());
            billStmt.setString(7, bill.getPaymentMethod());
            billStmt.setInt(8, bill.getCreatedByUserID());
            billStmt.setString(9, bill.getCreatedByUsername());
            billStmt.setString(10, bill.getCreatedByFullName());
            billStmt.executeUpdate();

            ResultSet rs = billStmt.getGeneratedKeys();
            if (rs.next()) {
                int billID = rs.getInt(1);
                
                System.out.println("=== BillDAO.addBill - Bill saved with ID: " + billID + " ===");

                // ✅ CRITICAL FIX: Pass the SAME connection to BillItemsDAO
                if (bill.getItems() != null && !bill.getItems().isEmpty()) {
                    System.out.println("Calling BillItemsDAO.addBillItems() with " + bill.getItems().size() + " items...");
                    
                    BillItemsDAO billItemsDAO = new BillItemsDAO();
                    boolean itemsSaved = billItemsDAO.addBillItems(conn, billID, bill.getItems());
                    
                    if (!itemsSaved) {
                        System.err.println("ERROR: Failed to save bill items!");
                        conn.rollback();
                        return -1;
                    }
                    
                    System.out.println("✓ Bill items saved successfully via BillItemsDAO");
                }

                conn.commit();
                System.out.println("✓ Transaction committed");
                return billID;
            }
        } catch (SQLException e) {
            System.err.println("ERROR in addBill: " + e.getMessage());
            conn.rollback();
            throw e;
        }
    }
    return -1;
}

    // Update the getBillByCode method to include user information
    public Bill getBillByCode(String billCode) throws SQLException {
    String billSQL = "SELECT * FROM Bills WHERE BillCode = ?";
    String itemSQL = "SELECT * FROM BillItems WHERE BillID = ?";

    try (Connection conn = ConnectionFactory.getConnection();
         PreparedStatement billStmt = conn.prepareStatement(billSQL)) {

        billStmt.setString(1, billCode);
        try (ResultSet rs = billStmt.executeQuery()) {
            if (rs.next()) {
                Bill bill = new Bill();
                bill.setBillID(rs.getInt("BillID"));
                bill.setBillCode(rs.getString("BillCode"));
                bill.setCustomerName(rs.getString("CustomerName"));
                bill.setBillDate(rs.getTimestamp("BillDate"));
                bill.setTotalAmount(rs.getBigDecimal("TotalAmount"));
                bill.setPaidAmount(rs.getBigDecimal("PaidAmount"));
                bill.setBalance(rs.getBigDecimal("Balance"));
                bill.setNotes(rs.getString("Notes"));
                bill.setPaymentMethod(rs.getString("PaymentMethod"));
                
                // Get user information
                bill.setCreatedByUserID(rs.getInt("CreatedByUserID"));
                bill.setCreatedByUsername(rs.getString("CreatedByUsername"));
                bill.setCreatedByFullName(rs.getString("CreatedByFullName"));
                bill.setLastModifiedByUserID(rs.getInt("LastModifiedByUserID"));
                bill.setLastModifiedByUsername(rs.getString("LastModifiedByUsername"));
                bill.setLastModifiedDate(rs.getTimestamp("LastModifiedDate"));

                // Fetch associated items WITH DISCOUNT INFO
                try (PreparedStatement itemStmt = conn.prepareStatement(itemSQL)) {
                    itemStmt.setInt(1, bill.getBillID());
                    try (ResultSet itemRS = itemStmt.executeQuery()) {
                        List<BillItem> items = new ArrayList<>();
                        while (itemRS.next()) {
                            BillItem item = new BillItem();
                            item.setItemName(itemRS.getString("ItemName"));
                            item.setWarranty(itemRS.getString("Warranty"));
                            item.setQuantity(itemRS.getInt("Quantity"));
                            item.setPrice(itemRS.getBigDecimal("Price"));
                            item.setTotal(itemRS.getBigDecimal("Total"));
                            
                            // Load discount information
                            BigDecimal discount = itemRS.getBigDecimal("Discount");
                            item.setDiscount(discount != null ? discount : BigDecimal.ZERO);
                            
                            String discountType = itemRS.getString("DiscountType");
                            item.setDiscountType(discountType != null ? discountType : "NONE");
                            
                            BigDecimal discountAmount = itemRS.getBigDecimal("DiscountAmount");
                            item.setDiscountAmount(discountAmount != null ? discountAmount : BigDecimal.ZERO);
                            
                            BigDecimal finalTotal = itemRS.getBigDecimal("FinalTotal");
                            item.setFinalTotal(finalTotal != null ? finalTotal : item.getTotal());
                            
                            items.add(item);
                        }
                        bill.setItems(items);
                    }
                }
                return bill;
            }
        }
    }
    return null;
}

    public boolean updateBill(Bill bill) throws SQLException {
    String deleteBillItemsSQL = "DELETE FROM BillItems WHERE BillID = ?";
    String updateBillSQL = "UPDATE Bills SET CustomerName = ?, TotalAmount = ?, PaidAmount = ?, Balance = ?, " +
                          "Notes = ?, PaymentMethod = ?, LastModifiedByUserID = ?, LastModifiedByUsername = ?, " +
                          "LastModifiedDate = ? WHERE BillCode = ?";
    
    try (Connection conn = ConnectionFactory.getConnection()) {
        conn.setAutoCommit(false);
        
        try {
            // Get BillID
            int billID = -1;
            String getBillIDSQL = "SELECT BillID FROM Bills WHERE BillCode = ?";
            try (PreparedStatement stmt = conn.prepareStatement(getBillIDSQL)) {
                stmt.setString(1, bill.getBillCode());
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    billID = rs.getInt("BillID");
                }
            }
            
            if (billID == -1) {
                conn.rollback();
                return false;
            }
            
            // Delete existing bill items
            try (PreparedStatement pstmt = conn.prepareStatement(deleteBillItemsSQL)) {
                pstmt.setInt(1, billID);
                pstmt.executeUpdate();
            }
            
            // Update bill header
            try (PreparedStatement pstmt = conn.prepareStatement(updateBillSQL)) {
                pstmt.setString(1, bill.getCustomerName());
                pstmt.setBigDecimal(2, bill.getTotalAmount());
                pstmt.setBigDecimal(3, bill.getPaidAmount());
                pstmt.setBigDecimal(4, bill.getBalance());
                pstmt.setString(5, bill.getNotes());
                pstmt.setString(6, bill.getPaymentMethod());
                pstmt.setInt(7, bill.getLastModifiedByUserID());
                pstmt.setString(8, bill.getLastModifiedByUsername());
                pstmt.setTimestamp(9, new Timestamp(System.currentTimeMillis()));
                pstmt.setString(10, bill.getBillCode());
                
                int rowsUpdated = pstmt.executeUpdate();
                if (rowsUpdated == 0) {
                    conn.rollback();
                    return false;
                }
            }
            
            // ✅ CRITICAL FIX: Insert new items using SAME connection
            if (bill.getItems() != null && !bill.getItems().isEmpty()) {
                BillItemsDAO billItemsDAO = new BillItemsDAO();
                boolean itemsSaved = billItemsDAO.addBillItems(conn, billID, bill.getItems());
                
                if (!itemsSaved) {
                    conn.rollback();
                    return false;
                }
            }
            
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        }
    }
}




    
    // Retrieve all bills ordered by date
    public List<Bill> getAllBills() throws SQLException {
    String sql = "SELECT * FROM Bills ORDER BY BillDate DESC";  // Added ORDER BY
    List<Bill> bills = new ArrayList<>();

    try (Connection conn = ConnectionFactory.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {

        while (rs.next()) {
            Bill bill = new Bill();
            bill.setBillID(rs.getInt("BillID"));
            bill.setBillCode(rs.getString("BillCode"));
            bill.setCustomerName(rs.getString("CustomerName"));
            bill.setBillDate(rs.getTimestamp("BillDate"));
            bill.setTotalAmount(rs.getBigDecimal("TotalAmount"));
            bill.setPaidAmount(rs.getBigDecimal("PaidAmount"));
            bill.setBalance(rs.getBigDecimal("Balance"));
            bill.setNotes(rs.getString("Notes"));
            bill.setPaymentMethod(rs.getString("PaymentMethod"));
            bills.add(bill);
        }
    }
    return bills;
}

    // Delete a bill by its ID
    public boolean deleteBill(int billID) throws SQLException {
        String sql = "DELETE FROM Bills WHERE BillID = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, billID);
            return stmt.executeUpdate() > 0;
        }
    }

    
   
    
    
    // Get total income from all bills
public BigDecimal getTotalIncome() throws SQLException {
    String sql = "SELECT SUM(TotalAmount) FROM Bills";
    try (Connection conn = ConnectionFactory.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
            return rs.getBigDecimal(1);
        }
    }
    return BigDecimal.ZERO;
}

// Get total number of items sold
public int getTotalItemsSold() throws SQLException {
    String sql = "SELECT SUM(Quantity) FROM BillItems";
    try (Connection conn = ConnectionFactory.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
            return rs.getInt(1);
        }
    }
    return 0;
}

// Get the most sold item
public String getMostSoldItem() throws SQLException {
    String sql = "SELECT ItemName FROM BillItems GROUP BY ItemName ORDER BY SUM(Quantity) DESC LIMIT 1";
    try (Connection conn = ConnectionFactory.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
            return rs.getString("ItemName");
        }
    }
    return "No items sold";
}

// Get Monthly Sales Report (Fixed for H2 Database)
// Update getMonthlyReport() method - fix the reserved keyword issue
public List<Object[]> getMonthlyReport() throws SQLException {
    String sql = "SELECT YEAR(BillDate) AS YearValue, MONTH(BillDate) AS MonthValue, " +
                 "SUM(TotalAmount) AS TotalSales, SUM(Quantity) AS TotalItemsSold " +
                 "FROM Bills JOIN BillItems ON Bills.BillID = BillItems.BillID " +
                 "GROUP BY YEAR(BillDate), MONTH(BillDate) " +
                 "ORDER BY YearValue DESC, MonthValue DESC";

    List<Object[]> monthlyReport = new ArrayList<>();

    try (Connection conn = ConnectionFactory.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {

        while (rs.next()) {
            // Manually format year-month in Java
            String formattedMonth = rs.getInt("YearValue") + "-" + String.format("%02d", rs.getInt("MonthValue"));

            Object[] row = {
                formattedMonth,  // Formatted Year-Month (YYYY-MM)
                rs.getBigDecimal("TotalSales"),
                rs.getInt("TotalItemsSold")
            };
            monthlyReport.add(row);
        }
    }
    return monthlyReport;
}

// Add this method to your BillDAO class
public boolean deleteBillWithItems(int billID) throws SQLException {
    String deleteBillItemsSQL = "DELETE FROM BillItems WHERE BillID = ?";
    String deleteBillSQL = "DELETE FROM Bills WHERE BillID = ?";
    
    try (Connection conn = ConnectionFactory.getConnection()) {
        conn.setAutoCommit(false);
        
        try {
            // First delete all bill items associated with this bill
            try (PreparedStatement stmt = conn.prepareStatement(deleteBillItemsSQL)) {
                stmt.setInt(1, billID);
                stmt.executeUpdate();
            }
            
            // Then delete the bill itself
            try (PreparedStatement stmt = conn.prepareStatement(deleteBillSQL)) {
                stmt.setInt(1, billID);
                int rowsAffected = stmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    conn.commit();
                    return true;
                } else {
                    conn.rollback();
                    return false;
                }
            }
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        }
    }
}

 // Check if a bill exists by its code
public boolean billExists(String billCode) throws SQLException {
    String sql = "SELECT COUNT(*) FROM Bills WHERE BillCode = ?";
    try (Connection conn = ConnectionFactory.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, billCode);
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
    }
    return false;
}

// Search bills by last digits or bill code
public List<Bill> searchBills(String searchText) throws SQLException {
    List<Bill> bills = new ArrayList<>();
    String sql = "SELECT * FROM Bills WHERE LOWER(BillCode) LIKE ? OR LOWER(CustomerName) LIKE ? ORDER BY BillDate DESC LIMIT 50";
    
    try (Connection conn = ConnectionFactory.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        String searchPattern = "%" + searchText.toLowerCase() + "%";
        stmt.setString(1, searchPattern);
        stmt.setString(2, searchPattern);
        
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Bill bill = new Bill();
                bill.setBillID(rs.getInt("BillID"));
                bill.setBillCode(rs.getString("BillCode"));
                bill.setCustomerName(rs.getString("CustomerName"));
                bill.setBillDate(rs.getTimestamp("BillDate"));
                bill.setTotalAmount(rs.getBigDecimal("TotalAmount"));
                bill.setPaidAmount(rs.getBigDecimal("PaidAmount"));
                bill.setBalance(rs.getBigDecimal("Balance"));
                bill.setNotes(rs.getString("Notes"));
                bill.setPaymentMethod(rs.getString("PaymentMethod"));
                bills.add(bill);
            }
        }
    }
    return bills;
}

// Get recent bills (for initial display)
public List<Bill> getRecentBills(int limit) throws SQLException {
    List<Bill> bills = new ArrayList<>();
    String sql = "SELECT * FROM Bills ORDER BY BillDate DESC LIMIT ?";
    
    try (Connection conn = ConnectionFactory.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setInt(1, limit);
        
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Bill bill = new Bill();
                bill.setBillID(rs.getInt("BillID"));
                bill.setBillCode(rs.getString("BillCode"));
                bill.setCustomerName(rs.getString("CustomerName"));
                bill.setBillDate(rs.getTimestamp("BillDate"));
                bill.setTotalAmount(rs.getBigDecimal("TotalAmount"));
                bill.setPaidAmount(rs.getBigDecimal("PaidAmount"));
                bill.setBalance(rs.getBigDecimal("Balance"));
                bill.setNotes(rs.getString("Notes"));
                bill.setPaymentMethod(rs.getString("PaymentMethod"));
                bills.add(bill);
            }
        }
    }
    return bills;
}
    

    // Add this method to BillDAO.java for optimized today's bills count
// Update getTodaysBillsCount() method
public int getTodaysBillsCount() throws SQLException {
    String sql = "SELECT COUNT(*) FROM Bills WHERE FORMATDATETIME(BillDate, 'yyyy-MM-dd') = FORMATDATETIME(CURRENT_DATE, 'yyyy-MM-dd')";
    
    try (Connection conn = ConnectionFactory.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {
        
        if (rs.next()) {
            return rs.getInt(1);
        }
    }
    return 0;
}
    // In BillDAO.java, update getTodaysBills() method
public List<Bill> getTodaysBills() throws SQLException {
    // H2 compatible query
    String sql = "SELECT * FROM Bills WHERE FORMATDATETIME(BillDate, 'yyyy-MM-dd') = FORMATDATETIME(CURRENT_DATE, 'yyyy-MM-dd') ORDER BY BillDate DESC";
    List<Bill> bills = new ArrayList<>();
    
    try (Connection conn = ConnectionFactory.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {
        
        while (rs.next()) {
            Bill bill = new Bill();
            bill.setBillID(rs.getInt("BillID"));
            bill.setBillCode(rs.getString("BillCode"));
            bill.setCustomerName(rs.getString("CustomerName"));
            bill.setBillDate(rs.getTimestamp("BillDate"));
            bill.setTotalAmount(rs.getBigDecimal("TotalAmount"));
            bill.setPaidAmount(rs.getBigDecimal("PaidAmount"));
            bill.setBalance(rs.getBigDecimal("Balance"));
            bill.setNotes(rs.getString("Notes"));
            bill.setPaymentMethod(rs.getString("PaymentMethod"));
            bills.add(bill);
        }
    }
    return bills;
}
    
}
