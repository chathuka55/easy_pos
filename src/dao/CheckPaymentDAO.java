package dao;

import db.ConnectionFactory;
import models.CheckPayment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class CheckPaymentDAO {

    // Add a new check payment
    public void addPayment(CheckPayment payment) throws SQLException {
        String sql = "INSERT INTO CHECK_PAYMENTS (BILLID, PAYMENTDATE, AMOUNT, METHOD, BANK, CHEQUENO, CHEQUEDATE) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, payment.getBillId());
            stmt.setTimestamp(2, payment.getPaymentDate());
            stmt.setBigDecimal(3, payment.getAmount());
            stmt.setString(4, payment.getMethod());
            stmt.setString(5, payment.getBank());
            stmt.setString(6, payment.getChequeNo());
            stmt.setDate(7, payment.getChequeDate());
            stmt.executeUpdate();
        }
    }

    // Get payments by bill ID
    public List<CheckPayment> getPaymentsByBillId(String billId) throws SQLException {
        List<CheckPayment> list = new ArrayList<>();
        String sql = "SELECT * FROM CHECK_PAYMENTS WHERE BILLID = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, billId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new CheckPayment(
                        rs.getInt("PAYMENTID"),
                        rs.getString("BILLID"),
                        rs.getTimestamp("PAYMENTDATE"),
                        rs.getBigDecimal("AMOUNT"),
                        rs.getString("METHOD"),
                        rs.getString("BANK"),
                        rs.getString("CHEQUENO"),
                        rs.getDate("CHEQUEDATE")
                    ));
                }
            }
        }
        return list;
    }

    // Update an existing payment
    public void updatePayment(CheckPayment payment) throws SQLException {
        String sql = "UPDATE CHECK_PAYMENTS SET BILLID = ?, PAYMENTDATE = ?, AMOUNT = ?, METHOD = ?, BANK = ?, CHEQUENO = ?, CHEQUEDATE = ? WHERE PAYMENTID = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, payment.getBillId());
            stmt.setTimestamp(2, payment.getPaymentDate());
            stmt.setBigDecimal(3, payment.getAmount());
            stmt.setString(4, payment.getMethod());
            stmt.setString(5, payment.getBank());
            stmt.setString(6, payment.getChequeNo());
            stmt.setDate(7, payment.getChequeDate());
            stmt.setInt(8, payment.getPaymentId());
            stmt.executeUpdate();
        }
    }

    // Delete a payment by its ID
    public void deletePayment(int paymentId) throws SQLException {
        String sql = "DELETE FROM CHECK_PAYMENTS WHERE PAYMENTID = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, paymentId);
            stmt.executeUpdate();
        }
    }

    // Get total paid amount for a bill
    public BigDecimal getTotalPaidForBill(String billId) throws SQLException {
        String sql = "SELECT SUM(AMOUNT) AS TOTAL FROM CHECK_PAYMENTS WHERE BILLID = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, billId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("TOTAL") != null ? rs.getBigDecimal("TOTAL") : BigDecimal.ZERO;
                }
            }
        }
        return BigDecimal.ZERO;
    }

    // Get the most recent cheque payment for a given bank
    public CheckPayment getLatestChequeByBank(String bankName) throws SQLException {
        String sql = "SELECT * FROM CHECK_PAYMENTS WHERE BANK = ? AND METHOD = 'Cheque' ORDER BY CHEQUEDATE DESC LIMIT 1";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, bankName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new CheckPayment(
                        rs.getInt("PAYMENTID"),
                        rs.getString("BILLID"),
                        rs.getTimestamp("PAYMENTDATE"),
                        rs.getBigDecimal("AMOUNT"),
                        rs.getString("METHOD"),
                        rs.getString("BANK"),
                        rs.getString("CHEQUENO"),
                        rs.getDate("CHEQUEDATE")
                    );
                }
            }
        }
        return null;
    }
}
