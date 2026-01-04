package dao;

import db.ConnectionFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;
import models.Payment;

/**
 * @author CJAY
 */
public class PaymentsDAO {

    // Add a new payment to the Payments table
    public void addPayment(Payment payment) throws SQLException {
        String query = "INSERT INTO Payments (BillCode, PaymentType, Amount, PaymentDate) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, payment.getBillCode());
            pstmt.setString(2, payment.getPaymentType());
            pstmt.setBigDecimal(3, payment.getAmount());
            pstmt.setTimestamp(4, payment.getPaymentDate());

            pstmt.executeUpdate();
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    payment.setPaymentID(generatedKeys.getInt(1));
                }
            }
        }
    }

    // Retrieve a payment by its ID
    public Payment getPaymentById(int paymentID) throws SQLException {
        String query = "SELECT * FROM Payments WHERE PaymentID = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, paymentID);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Payment payment = new Payment(
                        rs.getString("BillCode"),
                        rs.getString("PaymentType"),
                        rs.getBigDecimal("Amount"),
                        rs.getTimestamp("PaymentDate")
                    );
                    payment.setPaymentID(rs.getInt("PaymentID"));
                    return payment;
                }
            }
        }
        return null;
    }

    // **NEW: Get all payments for a specific bill**
    public List<Payment> getPaymentsByBillCode(String billCode) throws SQLException {
        String query = "SELECT * FROM Payments WHERE BillCode = ? ORDER BY PaymentDate DESC";
        List<Payment> payments = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, billCode);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Payment payment = new Payment(
                        rs.getString("BillCode"),
                        rs.getString("PaymentType"),
                        rs.getBigDecimal("Amount"),
                        rs.getTimestamp("PaymentDate")
                    );
                    payment.setPaymentID(rs.getInt("PaymentID"));
                    payments.add(payment);
                }
            }
        }
        return payments;
    }

    // **NEW: Get all payments for a customer (via all their bills)**
    public List<Payment> getPaymentsByCustomer(String customerId) throws SQLException {
        String query = "SELECT p.* FROM Payments p " +
                      "INNER JOIN CHECK_BILLS cb ON p.BillCode = cb.BILLID " +
                      "WHERE cb.CUSTOMERID = ? " +
                      "ORDER BY p.PaymentDate DESC";
        List<Payment> payments = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, customerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Payment payment = new Payment(
                        rs.getString("BillCode"),
                        rs.getString("PaymentType"),
                        rs.getBigDecimal("Amount"),
                        rs.getTimestamp("PaymentDate")
                    );
                    payment.setPaymentID(rs.getInt("PaymentID"));
                    payments.add(payment);
                }
            }
        }
        return payments;
    }

    // **NEW: Get total payments for a specific bill**
    public BigDecimal getTotalPaymentsByBillCode(String billCode) throws SQLException {
        String query = "SELECT SUM(Amount) as TotalPaid FROM Payments WHERE BillCode = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, billCode);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal total = rs.getBigDecimal("TotalPaid");
                    return total != null ? total : BigDecimal.ZERO;
                }
            }
        }
        return BigDecimal.ZERO;
    }

    // Get all payments from the Payments table
    public List<Payment> getAllPayments() throws SQLException {
        String query = "SELECT * FROM Payments ORDER BY PaymentDate DESC";
        List<Payment> payments = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Payment payment = new Payment(
                    rs.getString("BillCode"),
                    rs.getString("PaymentType"),
                    rs.getBigDecimal("Amount"),
                    rs.getTimestamp("PaymentDate")
                );
                payment.setPaymentID(rs.getInt("PaymentID"));
                payments.add(payment);
            }
        }
        return payments;
    }

    // Update an existing payment in the Payments table
    public void updatePayment(Payment payment) throws SQLException {
        String query = "UPDATE Payments SET BillCode = ?, PaymentType = ?, Amount = ?, PaymentDate = ? WHERE PaymentID = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, payment.getBillCode());
            pstmt.setString(2, payment.getPaymentType());
            pstmt.setBigDecimal(3, payment.getAmount());
            pstmt.setTimestamp(4, payment.getPaymentDate());
            pstmt.setInt(5, payment.getPaymentID());

            pstmt.executeUpdate();
        }
    }

    // Delete a payment by its ID
    public void deletePayment(int paymentID) throws SQLException {
        String query = "DELETE FROM Payments WHERE PaymentID = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, paymentID);
            pstmt.executeUpdate();
        }
    }
}