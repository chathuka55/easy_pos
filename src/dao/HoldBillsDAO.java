package dao;

import db.ConnectionFactory;
import models.HoldBill;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HoldBillsDAO {

    // Add a new HoldBill
    public void addHoldBill(HoldBill holdBill) throws SQLException {
        String query = "INSERT INTO HoldBills (BillCode, AmountPaid, RemainingBalance, PaymentDate) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, holdBill.getBillCode()); // Changed to billCode
            pstmt.setBigDecimal(2, holdBill.getAmountPaid());
            pstmt.setBigDecimal(3, holdBill.getRemainingBalance());
            pstmt.setTimestamp(4, holdBill.getPaymentDate() != null ? new Timestamp(holdBill.getPaymentDate().getTime()) : null);

            pstmt.executeUpdate();
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    holdBill.setHoldID(generatedKeys.getInt(1));
                }
            }
        }
    }

    // Get HoldBill by HoldID
    public HoldBill getHoldBillById(int holdID) throws SQLException {
        String query = "SELECT * FROM HoldBills WHERE HoldID = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, holdID);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    HoldBill holdBill = new HoldBill();
                    holdBill.setHoldID(rs.getInt("HoldID"));
                    holdBill.setBillCode(rs.getString("BillCode")); // Changed to billCode
                    holdBill.setAmountPaid(rs.getBigDecimal("AmountPaid"));
                    holdBill.setPaymentDate(rs.getTimestamp("PaymentDate"));
                    holdBill.setRemainingBalance(rs.getBigDecimal("RemainingBalance"));
                    return holdBill;
                }
            }
        }
        return null;
    }

    // Get all HoldBills
    public List<HoldBill> getAllHoldBills() throws SQLException {
        String query = "SELECT * FROM HoldBills";
        List<HoldBill> holdBills = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                HoldBill holdBill = new HoldBill();
                holdBill.setHoldID(rs.getInt("HoldID"));
                holdBill.setBillCode(rs.getString("BillCode")); // Changed to billCode
                holdBill.setAmountPaid(rs.getBigDecimal("AmountPaid"));
                holdBill.setPaymentDate(rs.getTimestamp("PaymentDate"));
                holdBill.setRemainingBalance(rs.getBigDecimal("RemainingBalance"));
                holdBills.add(holdBill);
            }
        }
        return holdBills;
    }

    // Update HoldBill
    public void updateHoldBill(HoldBill holdBill) throws SQLException {
        String query = "UPDATE HoldBills SET BillCode = ?, AmountPaid = ?, RemainingBalance = ? WHERE HoldID = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, holdBill.getBillCode()); // Changed to billCode
            pstmt.setBigDecimal(2, holdBill.getAmountPaid());
            pstmt.setBigDecimal(3, holdBill.getRemainingBalance());
            pstmt.setInt(4, holdBill.getHoldID());

            pstmt.executeUpdate();
        }
    }

    // Delete HoldBill
    public void deleteHoldBill(int holdID) throws SQLException {
        String query = "DELETE FROM HoldBills WHERE HoldID = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, holdID);
            pstmt.executeUpdate();
        }
    }
    
    // Get HoldBill by BillCode
public HoldBill getHoldBillByBillCode(String billCode) throws SQLException {
    String query = "SELECT * FROM HoldBills WHERE BillCode = ?";
    try (Connection conn = ConnectionFactory.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(query)) {

        pstmt.setString(1, billCode);
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                HoldBill holdBill = new HoldBill();
                holdBill.setHoldID(rs.getInt("HoldID"));
                holdBill.setBillCode(rs.getString("BillCode"));
                holdBill.setAmountPaid(rs.getBigDecimal("AmountPaid"));
                holdBill.setPaymentDate(rs.getTimestamp("PaymentDate"));
                holdBill.setRemainingBalance(rs.getBigDecimal("RemainingBalance"));
                return holdBill;
            }
        }
    }
    return null;
}

}
