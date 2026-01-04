/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package dao;

import db.ConnectionFactory;
import models.BillAudit;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BillAuditDAO {
    
    public boolean addAuditLog(BillAudit audit) throws SQLException {
        String sql = "INSERT INTO BillAuditLog (BillCode, Action, UserID, Username, UserFullName, " +
                    "ActionTimestamp, IPAddress, MachineName, Details, OldValues, NewValues, " +
                    "TotalAmount, CustomerName) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, audit.getBillCode());
            stmt.setString(2, audit.getAction());
            stmt.setInt(3, audit.getUserID());
            stmt.setString(4, audit.getUsername());
            stmt.setString(5, audit.getUserFullName());
            stmt.setTimestamp(6, audit.getActionTimestamp());
            stmt.setString(7, audit.getIpAddress());
            stmt.setString(8, audit.getMachineName());
            stmt.setString(9, audit.getDetails());
            stmt.setString(10, audit.getOldValues());
            stmt.setString(11, audit.getNewValues());
            stmt.setBigDecimal(12, audit.getTotalAmount());
            stmt.setString(13, audit.getCustomerName());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    public List<BillAudit> getAuditsByBillCode(String billCode) throws SQLException {
        String sql = "SELECT * FROM BillAuditLog WHERE BillCode = ? ORDER BY ActionTimestamp DESC";
        List<BillAudit> audits = new ArrayList<>();
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, billCode);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                BillAudit audit = new BillAudit();
                audit.setAuditID(rs.getInt("AuditID"));
                audit.setBillCode(rs.getString("BillCode"));
                audit.setAction(rs.getString("Action"));
                audit.setUserID(rs.getInt("UserID"));
                audit.setUsername(rs.getString("Username"));
                audit.setUserFullName(rs.getString("UserFullName"));
                audit.setActionTimestamp(rs.getTimestamp("ActionTimestamp"));
                audit.setIpAddress(rs.getString("IPAddress"));
                audit.setMachineName(rs.getString("MachineName"));
                audit.setDetails(rs.getString("Details"));
                audit.setOldValues(rs.getString("OldValues"));
                audit.setNewValues(rs.getString("NewValues"));
                audit.setTotalAmount(rs.getBigDecimal("TotalAmount"));
                audit.setCustomerName(rs.getString("CustomerName"));
                audits.add(audit);
            }
        }
        return audits;
    }
    
    public List<BillAudit> getAuditsByUser(int userID) throws SQLException {
        String sql = "SELECT * FROM BillAuditLog WHERE UserID = ? ORDER BY ActionTimestamp DESC";
        List<BillAudit> audits = new ArrayList<>();
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userID);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                BillAudit audit = new BillAudit();
                audit.setAuditID(rs.getInt("AuditID"));
                audit.setBillCode(rs.getString("BillCode"));
                audit.setAction(rs.getString("Action"));
                audit.setUserID(rs.getInt("UserID"));
                audit.setUsername(rs.getString("Username"));
                audit.setUserFullName(rs.getString("UserFullName"));
                audit.setActionTimestamp(rs.getTimestamp("ActionTimestamp"));
                audit.setIpAddress(rs.getString("IPAddress"));
                audit.setMachineName(rs.getString("MachineName"));
                audit.setDetails(rs.getString("Details"));
                audit.setOldValues(rs.getString("OldValues"));
                audit.setNewValues(rs.getString("NewValues"));
                audit.setTotalAmount(rs.getBigDecimal("TotalAmount"));
                audit.setCustomerName(rs.getString("CustomerName"));
                audits.add(audit);
            }
        }
        return audits;
    }
    
    public List<BillAudit> getAllAudits(int limit) throws SQLException {
        String sql = "SELECT * FROM BillAuditLog ORDER BY ActionTimestamp DESC LIMIT ?";
        List<BillAudit> audits = new ArrayList<>();
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                BillAudit audit = new BillAudit();
                audit.setAuditID(rs.getInt("AuditID"));
                audit.setBillCode(rs.getString("BillCode"));
                audit.setAction(rs.getString("Action"));
                audit.setUserID(rs.getInt("UserID"));
                audit.setUsername(rs.getString("Username"));
                audit.setUserFullName(rs.getString("UserFullName"));
                audit.setActionTimestamp(rs.getTimestamp("ActionTimestamp"));
                audit.setIpAddress(rs.getString("IPAddress"));
                audit.setMachineName(rs.getString("MachineName"));
                audit.setDetails(rs.getString("Details"));
                audit.setOldValues(rs.getString("OldValues"));
                audit.setNewValues(rs.getString("NewValues"));
                audit.setTotalAmount(rs.getBigDecimal("TotalAmount"));
                audit.setCustomerName(rs.getString("CustomerName"));
                audits.add(audit);
            }
        }
        return audits;
    }
}