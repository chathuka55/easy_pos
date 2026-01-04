package dao;

import db.ConnectionFactory;
import models.CheckBill;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;
import models.Customer;

public class CheckBillDAO {
    
    
     // Updated addCheckBill method with user tracking
    public void addCheckBill(CheckBill bill) throws SQLException {
        String sql = "INSERT INTO CHECK_BILLS (BILLID, CUSTOMERID, BILLDATE, TOTALPAYABLE, PAYMENTRECEIVED, " +
                     "PAYMENTMETHOD, BANK, CHEQUENO, CHEQUEDATE, NOTES, OUTSTANDING, " +
                     "CREATEDBYUSERID, CREATEDBYUSERNAME, CREATEDDATE) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, bill.getBillId());
            stmt.setString(2, bill.getCustomerId());
            stmt.setTimestamp(3, bill.getBillDate());
            stmt.setBigDecimal(4, bill.getTotalPayable());
            stmt.setBigDecimal(5, bill.getPaymentReceived());
            stmt.setString(6, bill.getPaymentMethod());
            stmt.setString(7, bill.getBank());
            stmt.setString(8, bill.getChequeNo());
            stmt.setDate(9, bill.getChequeDate());
            stmt.setString(10, bill.getNotes());
            stmt.setBigDecimal(11, bill.getOutstanding());
            stmt.setInt(12, bill.getCreatedByUserId());
            stmt.setString(13, bill.getCreatedByUsername());
            stmt.setTimestamp(14, new Timestamp(System.currentTimeMillis()));

            stmt.executeUpdate();
        }
    }
     
    // New method: Update check bill with user tracking
    public void updateCheckBill(CheckBill bill) throws SQLException {
        String sql = "UPDATE CHECK_BILLS SET TOTALPAYABLE = ?, PAYMENTRECEIVED = ?, " +
                     "PAYMENTMETHOD = ?, BANK = ?, CHEQUENO = ?, CHEQUEDATE = ?, NOTES = ?, " +
                     "OUTSTANDING = ?, LASTMODIFIEDBYUSERID = ?, LASTMODIFIEDBYUSERNAME = ?, " +
                     "LASTMODIFIEDDATE = ? WHERE BILLID = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBigDecimal(1, bill.getTotalPayable());
            stmt.setBigDecimal(2, bill.getPaymentReceived());
            stmt.setString(3, bill.getPaymentMethod());
            stmt.setString(4, bill.getBank());
            stmt.setString(5, bill.getChequeNo());
            stmt.setDate(6, bill.getChequeDate());
            stmt.setString(7, bill.getNotes());
            stmt.setBigDecimal(8, bill.getOutstanding());
            stmt.setInt(9, bill.getLastModifiedByUserId());
            stmt.setString(10, bill.getLastModifiedByUsername());
            stmt.setTimestamp(11, new Timestamp(System.currentTimeMillis()));
            stmt.setString(12, bill.getBillId());

            stmt.executeUpdate();
        }
    }

    // Updated helper method to map ResultSet to CheckBill object
    private CheckBill mapResultSetToCheckBill(ResultSet rs) throws SQLException {
        CheckBill bill = new CheckBill();
        bill.setBillId(rs.getString("BILLID"));
        bill.setCustomerId(rs.getString("CUSTOMERID"));
        bill.setBillDate(rs.getTimestamp("BILLDATE"));
        bill.setTotalPayable(rs.getBigDecimal("TOTALPAYABLE"));
        bill.setPaymentReceived(rs.getBigDecimal("PAYMENTRECEIVED"));
        bill.setPaymentMethod(rs.getString("PAYMENTMETHOD"));
        bill.setBank(rs.getString("BANK"));
        bill.setChequeNo(rs.getString("CHEQUENO"));
        bill.setChequeDate(rs.getDate("CHEQUEDATE"));
        bill.setNotes(rs.getString("NOTES"));
        bill.setOutstanding(rs.getBigDecimal("OUTSTANDING"));
        
        // Try to get user tracking fields (they might not exist in older records)
        try {
            bill.setCreatedByUserId(rs.getInt("CREATEDBYUSERID"));
            bill.setCreatedByUsername(rs.getString("CREATEDBYUSERNAME"));
            bill.setCreatedDate(rs.getTimestamp("CREATEDDATE"));
            bill.setLastModifiedByUserId(rs.getInt("LASTMODIFIEDBYUSERID"));
            bill.setLastModifiedByUsername(rs.getString("LASTMODIFIEDBYUSERNAME"));
            bill.setLastModifiedDate(rs.getTimestamp("LASTMODIFIEDDATE"));
        } catch (SQLException e) {
            // Columns might not exist for older records
        }
        
        return bill;
    }
    
    // Get a check bill by its ID (existing method - keeping as is)
    public CheckBill getCheckBillById(String billId) throws SQLException {
        String sql = "SELECT * FROM CHECK_BILLS WHERE BILLID = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, billId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCheckBill(rs);
                }
            }
        }
        return null;
    }

    // Alternative method name for compatibility with print methods
    public CheckBill getById(String billId) throws SQLException {
        return getCheckBillById(billId);
    }

    // Get all check bills (existing method - improved)
    public List<CheckBill> getAllCheckBills() throws SQLException {
        List<CheckBill> list = new ArrayList<>();
        String sql = "SELECT * FROM CHECK_BILLS ORDER BY BILLDATE DESC";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToCheckBill(rs));
            }
        }
        return list;
    }

    // NEW: Get check bills by customer ID
    public List<CheckBill> getCheckBillsByCustomerId(String customerId) throws SQLException {
        List<CheckBill> list = new ArrayList<>();
        String sql = "SELECT * FROM CHECK_BILLS WHERE CUSTOMERID = ? ORDER BY BILLDATE DESC";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToCheckBill(rs));
                }
            }
        }
        return list;
    }

    // NEW: Get pending check bills (outstanding > 0)
    public List<CheckBill> getPendingCheckBills() throws SQLException {
        List<CheckBill> list = new ArrayList<>();
        String sql = "SELECT * FROM CHECK_BILLS WHERE OUTSTANDING > 0 ORDER BY CHEQUEDATE";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToCheckBill(rs));
            }
        }
        return list;
    }

    // NEW: Get pending check bills for a specific customer
    public List<CheckBill> getPendingCheckBillsByCustomer(String customerId) throws SQLException {
        List<CheckBill> list = new ArrayList<>();
        String sql = "SELECT * FROM CHECK_BILLS WHERE CUSTOMERID = ? AND OUTSTANDING > 0 ORDER BY CHEQUEDATE";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToCheckBill(rs));
                }
            }
        }
        return list;
    }

    // NEW: Update outstanding amount for a bill (used when cheque is completed)
    public boolean updateOutstanding(String billId, BigDecimal newOutstanding) throws SQLException {
        String sql = "UPDATE CHECK_BILLS SET OUTSTANDING = ? WHERE BILLID = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBigDecimal(1, newOutstanding);
            stmt.setString(2, billId);

            return stmt.executeUpdate() > 0;
        }
    }

    // NEW: Mark cheque as completed (set outstanding to 0)
    public boolean markChequeAsCompleted(String billId) throws SQLException {
        return updateOutstanding(billId, BigDecimal.ZERO);
    }

    // NEW: Get total outstanding for a customer
    public BigDecimal getTotalOutstandingByCustomer(String customerId) throws SQLException {
        String sql = "SELECT SUM(OUTSTANDING) as TOTAL FROM CHECK_BILLS WHERE CUSTOMERID = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal total = rs.getBigDecimal("TOTAL");
                    return total != null ? total : BigDecimal.ZERO;
                }
            }
        }
        return BigDecimal.ZERO;
    }

    // NEW: Check if adding new outstanding would exceed credit limit
    public boolean wouldExceedCreditLimit(String customerId, BigDecimal additionalOutstanding) throws SQLException {
        CustomerDAO customerDAO = new CustomerDAO();
        Customer customer = customerDAO.getById(Integer.parseInt(customerId));
        
        if (customer == null) {
            throw new SQLException("Customer not found");
        }

        BigDecimal currentOutstanding = customer.getOutstandingAmount();
        BigDecimal creditLimit = customer.getCreditLimit();
        BigDecimal newTotal = currentOutstanding.add(additionalOutstanding);

        return newTotal.compareTo(creditLimit) > 0;
    }

    

    
    
    // NEW: Delete check bill and all its items
    public boolean deleteCheckBill(String billId) throws SQLException {
        Connection conn = null;
        try {
            conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false); // Start transaction
            
            // First, get the bill details to update customer outstanding
            CheckBill bill = getCheckBillById(billId);
            if (bill == null) {
                throw new SQLException("Bill not found: " + billId);
            }
            
            // Delete all items first (due to foreign key constraint)
            String deleteItemsSql = "DELETE FROM CHECK_BILL_ITEMS WHERE BILLID = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteItemsSql)) {
                stmt.setString(1, billId);
                stmt.executeUpdate();
            }
            
            // Delete the bill
            String deleteBillSql = "DELETE FROM CHECK_BILLS WHERE BILLID = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteBillSql)) {
                stmt.setString(1, billId);
                stmt.executeUpdate();
            }
            
            // Update customer outstanding (reduce it by the bill's outstanding amount)
            if (bill.getOutstanding().compareTo(BigDecimal.ZERO) > 0) {
                CustomerDAO customerDAO = new CustomerDAO();
                Customer customer = customerDAO.getById(Integer.parseInt(bill.getCustomerId()));
                if (customer != null) {
                    BigDecimal newOutstanding = customer.getOutstandingAmount()
                        .subtract(bill.getOutstanding());
                    customerDAO.updateOutstandingAmount(customer.getCustomerID(), newOutstanding);
                }
            }
            
            conn.commit(); // Commit transaction
            return true;
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback on error
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
 * Process partial payment - UPDATED VERSION
 */
public boolean processPartialPayment(String billId, BigDecimal paymentAmount, 
                                    String paymentMethod, String paymentDetails) throws SQLException {
    Connection conn = null;
    try {
        conn = ConnectionFactory.getConnection();
        conn.setAutoCommit(false);
        
        // Get current bill
        CheckBill bill = getCheckBillById(billId);
        if (bill == null) {
            throw new SQLException("Bill not found: " + billId);
        }
        
        // Calculate new outstanding
        BigDecimal currentOutstanding = bill.getOutstanding();
        BigDecimal newOutstanding = currentOutstanding.subtract(paymentAmount);
        
        if (newOutstanding.compareTo(BigDecimal.ZERO) < 0) {
            throw new SQLException("Payment amount exceeds outstanding amount");
        }
        
        // Update bill outstanding
        String updateBillSql = "UPDATE CHECK_BILLS SET OUTSTANDING = ?, " +
                              "PAYMENTRECEIVED = PAYMENTRECEIVED + ? WHERE BILLID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(updateBillSql)) {
            stmt.setBigDecimal(1, newOutstanding);
            stmt.setBigDecimal(2, paymentAmount);
            stmt.setString(3, billId);
            stmt.executeUpdate();
        }
        
        // Update customer outstanding
        CustomerDAO customerDAO = new CustomerDAO();
        Customer customer = customerDAO.getById(Integer.parseInt(bill.getCustomerId()));
        if (customer != null) {
            BigDecimal newCustomerOutstanding = customer.getOutstandingAmount()
                .subtract(paymentAmount);
            customerDAO.updateOutstandingAmount(customer.getCustomerID(), newCustomerOutstanding);
        }
        
        conn.commit();
        return true;
        
    } catch (SQLException e) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        throw e;
    } finally {
        if (conn != null) {
            try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
    
    
    
}