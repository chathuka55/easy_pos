/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

/**
 *
 * @author CJAY
 */
import db.ConnectionFactory;
import models.PurchaseHistory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PurchaseHistoryDAO implements BaseDAO<PurchaseHistory> {

    @Override
    public boolean add(PurchaseHistory history) throws SQLException {
        String sql = "INSERT INTO PurchaseHistory (BillCode, CustomerName, CustomerType, PurchasedItems, TotalPayment, OutstandingAmount, Discount, PurchaseDate, ReturnType, ReturnQuantity) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, history.getBillCode());
            stmt.setString(2, history.getCustomerName());
            stmt.setString(3, history.getCustomerType());
            stmt.setString(4, history.getPurchasedItems());
            stmt.setDouble(5, history.getTotalPayment());
            stmt.setDouble(6, history.getOutstandingAmount());
            stmt.setDouble(7, history.getDiscount());
            stmt.setDate(8, new java.sql.Date(history.getPurchaseDate().getTime()));
            stmt.setString(9, history.getReturnType());
            stmt.setInt(10, history.getReturnQuantity());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean update(PurchaseHistory history) throws SQLException {
        String sql = "UPDATE PurchaseHistory SET BillCode = ?, CustomerName = ?, CustomerType = ?, PurchasedItems = ?, TotalPayment = ?, OutstandingAmount = ?, Discount = ?, PurchaseDate = ?, ReturnType = ?, ReturnQuantity = ? " +
                     "WHERE HistoryID = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, history.getBillCode());
            stmt.setString(2, history.getCustomerName());
            stmt.setString(3, history.getCustomerType());
            stmt.setString(4, history.getPurchasedItems());
            stmt.setDouble(5, history.getTotalPayment());
            stmt.setDouble(6, history.getOutstandingAmount());
            stmt.setDouble(7, history.getDiscount());
            stmt.setDate(8, new java.sql.Date(history.getPurchaseDate().getTime()));
            stmt.setString(9, history.getReturnType());
            stmt.setInt(10, history.getReturnQuantity());
            stmt.setInt(11, history.getHistoryID());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM PurchaseHistory WHERE HistoryID = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public PurchaseHistory getById(int id) throws SQLException {
        String sql = "SELECT * FROM PurchaseHistory WHERE HistoryID = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    PurchaseHistory history = new PurchaseHistory();
                    history.setHistoryID(rs.getInt("HistoryID"));
                    history.setBillCode(rs.getString("BillCode"));
                    history.setCustomerName(rs.getString("CustomerName"));
                    history.setCustomerType(rs.getString("CustomerType"));
                    history.setPurchasedItems(rs.getString("PurchasedItems"));
                    history.setTotalPayment(rs.getDouble("TotalPayment"));
                    history.setOutstandingAmount(rs.getDouble("OutstandingAmount"));
                    history.setDiscount(rs.getDouble("Discount"));
                    history.setPurchaseDate(rs.getDate("PurchaseDate"));
                    history.setReturnType(rs.getString("ReturnType"));
                    history.setReturnQuantity(rs.getInt("ReturnQuantity"));
                    return history;
                }
            }
        }
        return null;
    }

    @Override
    public List<PurchaseHistory> getAll() throws SQLException {
        String sql = "SELECT * FROM PurchaseHistory";
        List<PurchaseHistory> historyList = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                PurchaseHistory history = new PurchaseHistory();
                history.setHistoryID(rs.getInt("HistoryID"));
                history.setBillCode(rs.getString("BillCode"));
                history.setCustomerName(rs.getString("CustomerName"));
                history.setCustomerType(rs.getString("CustomerType"));
                history.setPurchasedItems(rs.getString("PurchasedItems"));
                history.setTotalPayment(rs.getDouble("TotalPayment"));
                history.setOutstandingAmount(rs.getDouble("OutstandingAmount"));
                history.setDiscount(rs.getDouble("Discount"));
                history.setPurchaseDate(rs.getDate("PurchaseDate"));
                history.setReturnType(rs.getString("ReturnType"));
                history.setReturnQuantity(rs.getInt("ReturnQuantity"));
                historyList.add(history);
            }
        }
        return historyList;
    }

    // Custom Method: Fetch history by customer name
    public List<PurchaseHistory> getByCustomerName(String customerName) throws SQLException {
        String sql = "SELECT * FROM PurchaseHistory WHERE CustomerName = ?";
        List<PurchaseHistory> historyList = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, customerName);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    PurchaseHistory history = new PurchaseHistory();
                    history.setHistoryID(rs.getInt("HistoryID"));
                    history.setBillCode(rs.getString("BillCode"));
                    history.setCustomerName(rs.getString("CustomerName"));
                    history.setCustomerType(rs.getString("CustomerType"));
                    history.setPurchasedItems(rs.getString("PurchasedItems"));
                    history.setTotalPayment(rs.getDouble("TotalPayment"));
                    history.setOutstandingAmount(rs.getDouble("OutstandingAmount"));
                    history.setDiscount(rs.getDouble("Discount"));
                    history.setPurchaseDate(rs.getDate("PurchaseDate"));
                    history.setReturnType(rs.getString("ReturnType"));
                    history.setReturnQuantity(rs.getInt("ReturnQuantity"));
                    historyList.add(history);
                }
            }
        }
        return historyList;
    }
}