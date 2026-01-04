package dao;

import db.ConnectionFactory;
import models.ShopDetails;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ShopDetailsDAO implements BaseDAO<ShopDetails> {

    @Override
    public boolean add(ShopDetails shopDetails) throws SQLException {
        String query = "INSERT INTO ShopDetails (ShopName, AddressLine1, AddressLine2, ContactNumber, Email, Website, Logo) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, shopDetails.getShopName());
            ps.setString(2, shopDetails.getAddressLine1());
            ps.setString(3, shopDetails.getAddressLine2());
            ps.setString(4, shopDetails.getContactNumber());
            ps.setString(5, shopDetails.getEmail());
            ps.setString(6, shopDetails.getWebsite());
            ps.setBytes(7, shopDetails.getLogo());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean update(ShopDetails shopDetails) throws SQLException {
        String query = "UPDATE ShopDetails SET ShopName = ?, AddressLine1 = ?, AddressLine2 = ?, ContactNumber = ?, Email = ?, Website = ?, Logo = ? WHERE ShopID = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, shopDetails.getShopName());
            ps.setString(2, shopDetails.getAddressLine1());
            ps.setString(3, shopDetails.getAddressLine2());
            ps.setString(4, shopDetails.getContactNumber());
            ps.setString(5, shopDetails.getEmail());
            ps.setString(6, shopDetails.getWebsite());
            ps.setBytes(7, shopDetails.getLogo());
            ps.setInt(8, shopDetails.getShopID());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String query = "DELETE FROM ShopDetails WHERE ShopID = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public ShopDetails getById(int id) throws SQLException {
        String query = "SELECT * FROM ShopDetails WHERE ShopID = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extractShopDetails(rs);
            }
        }
        return null;
    }

    @Override
    public List<ShopDetails> getAll() throws SQLException {
        String query = "SELECT * FROM ShopDetails";
        List<ShopDetails> shopDetailsList = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                shopDetailsList.add(extractShopDetails(rs));
            }
        }
        return shopDetailsList;
    }

    private ShopDetails extractShopDetails(ResultSet rs) throws SQLException {
        ShopDetails shopDetails = new ShopDetails();
        shopDetails.setShopID(rs.getInt("ShopID"));
        shopDetails.setShopName(rs.getString("ShopName"));
        
        // Try to get new column names first, fall back to old if needed
        try {
            shopDetails.setAddressLine1(rs.getString("AddressLine1"));
            shopDetails.setAddressLine2(rs.getString("AddressLine2"));
        } catch (SQLException e) {
            // Fallback for old schema
            try {
                String address = rs.getString("Address");
                shopDetails.setAddressLine1(address);
                shopDetails.setAddressLine2("");
            } catch (SQLException ex) {
                shopDetails.setAddressLine1("");
                shopDetails.setAddressLine2("");
            }
        }
        
        shopDetails.setContactNumber(rs.getString("ContactNumber"));
        shopDetails.setEmail(rs.getString("Email"));
        shopDetails.setWebsite(rs.getString("Website"));
        shopDetails.setLogo(rs.getBytes("Logo"));
        return shopDetails;
    }
    
    public ShopDetails getFirstShop() throws SQLException {
        String query = "SELECT * FROM ShopDetails LIMIT 1";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return extractShopDetails(rs);
            }
        }
        return null;
    }
    
    // Method to clear address fields
    public boolean clearAddress(int shopID) throws SQLException {
        String query = "UPDATE ShopDetails SET AddressLine1 = '', AddressLine2 = '' WHERE ShopID = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, shopID);
            return ps.executeUpdate() > 0;
        }
    }
}