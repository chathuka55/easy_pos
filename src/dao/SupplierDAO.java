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
import models.Supplier;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupplierDAO implements BaseDAO<Supplier> {

    @Override
    public boolean add(Supplier supplier) throws SQLException {
        String sql = "INSERT INTO Suppliers (Name, ContactNumber, Address, TotalSpent) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, supplier.getName());
            stmt.setString(2, supplier.getContactNumber());
            stmt.setString(3, supplier.getAddress());
            stmt.setDouble(4, supplier.getTotalSpent());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
   public boolean update(Supplier supplier) throws SQLException {
        String sql = "UPDATE Suppliers SET Name = ?, ContactNumber = ?, Address = ?, TotalSpent = ? WHERE SupplierID = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, supplier.getName());
            stmt.setString(2, supplier.getContactNumber());
            stmt.setString(3, supplier.getAddress());
            stmt.setDouble(4, supplier.getTotalSpent());
            stmt.setInt(5, supplier.getSupplierID());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
     public boolean delete(int supplierID) throws SQLException {
        String sql = "DELETE FROM Suppliers WHERE SupplierID = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, supplierID);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public Supplier getById(int supplierID) throws SQLException {
        String sql = "SELECT * FROM Suppliers WHERE SupplierID = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, supplierID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Supplier supplier = new Supplier();
                    supplier.setSupplierID(rs.getInt("SupplierID"));
                    supplier.setName(rs.getString("Name"));
                    supplier.setContactNumber(rs.getString("ContactNumber"));
                    supplier.setAddress(rs.getString("Address"));
                    supplier.setTotalSpent(rs.getDouble("TotalSpent"));
                    supplier.setCreatedAt(rs.getTimestamp("CreatedAt"));
                    return supplier;
                }
            }
        }
        return null;
    }

    @Override
     public List<Supplier> getAll() throws SQLException {
        String sql = "SELECT * FROM Suppliers";
        List<Supplier> suppliers = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Supplier supplier = new Supplier();
                supplier.setSupplierID(rs.getInt("SupplierID"));
                supplier.setName(rs.getString("Name"));
                supplier.setContactNumber(rs.getString("ContactNumber"));
                supplier.setAddress(rs.getString("Address"));
                supplier.setTotalSpent(rs.getDouble("TotalSpent"));
                supplier.setCreatedAt(rs.getTimestamp("CreatedAt"));
                suppliers.add(supplier);
            }
        }
        return suppliers;
    }

        
    public Supplier getByName(String name) throws SQLException {
        String sql = "SELECT * FROM Suppliers WHERE Name = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Supplier supplier = new Supplier();
                    supplier.setSupplierID(rs.getInt("SupplierID"));
                    supplier.setName(rs.getString("Name"));
                    supplier.setContactNumber(rs.getString("ContactNumber"));
                    supplier.setAddress(rs.getString("Address"));
                    supplier.setTotalSpent(rs.getDouble("TotalSpent"));
                    supplier.setCreatedAt(rs.getTimestamp("CreatedAt"));
                    return supplier;
                }
            }
        }
        return null;
    }
    
     public List<Supplier> searchByName(String name) throws SQLException {
        List<Supplier> suppliers = new ArrayList<>();
        String query = "SELECT * FROM suppliers WHERE name LIKE ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, "%" + name + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Supplier supplier = new Supplier();
                // Check actual column name here, e.g. SupplierID, supplier_id
                supplier.setSupplierID(rs.getInt("SupplierID"));  // or adjust based on your DB schema
                supplier.setName(rs.getString("Name"));
                supplier.setContactNumber(rs.getString("ContactNumber"));
                supplier.setAddress(rs.getString("Address"));
                supplier.setTotalSpent(rs.getDouble("TotalSpent"));
                suppliers.add(supplier);
            }
        }

        return suppliers;
    }
     
     
    public boolean addWithId(Supplier supplier) throws SQLException {
        String sql = "INSERT INTO Suppliers (SupplierID, Name, ContactNumber, Address, TotalSpent, CreatedAt) " +
                 "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, supplier.getSupplierID());
            stmt.setString(2, supplier.getName());
            stmt.setString(3, supplier.getContactNumber());
            stmt.setString(4, supplier.getAddress());
            stmt.setDouble(5, supplier.getTotalSpent());
            stmt.setTimestamp(6, supplier.getCreatedAt());
            return stmt.executeUpdate() > 0;
        }
    }
    
     // Add to SupplierDAO.java
    public boolean existsById(int supplierID) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Suppliers WHERE SupplierID = ?";
        try (Connection conn = ConnectionFactory.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, supplierID);
        try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    
}
    
    