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
import models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO implements BaseDAO<User>{

    @Override
    public boolean add(User user) throws SQLException {
        String sql = "INSERT INTO Users (Name, Username, Password, ContactNumber, Address, Role, Picture) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getUsername());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getContactNumber());
            stmt.setString(5, user.getAddress());
            stmt.setString(6, user.getRole());
            stmt.setBytes(7, user.getPicture());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean update(User user) throws SQLException {
        String sql = "UPDATE Users SET Name = ?, Username = ?, Password = ?, ContactNumber = ?, Address = ?, Role = ?, Picture = ? WHERE UserID = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getUsername());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getContactNumber());
            stmt.setString(5, user.getAddress());
            stmt.setString(6, user.getRole());
            stmt.setBytes(7, user.getPicture());
            stmt.setInt(8, user.getUserID());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM Users WHERE UserID = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public User getById(int id) throws SQLException {
        String sql = "SELECT * FROM Users WHERE UserID = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setUserID(rs.getInt("UserID"));
                    user.setName(rs.getString("Name"));
                    user.setUsername(rs.getString("Username"));
                    user.setPassword(rs.getString("Password"));
                    user.setContactNumber(rs.getString("ContactNumber"));
                    user.setAddress(rs.getString("Address"));
                    user.setRole(rs.getString("Role"));
                    user.setPicture(rs.getBytes("Picture"));
                    return user;
                }
            }
        }
        return null;
    }

    @Override
    public List<User> getAll() throws SQLException {
        String sql = "SELECT * FROM Users";
        List<User> users = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                User user = new User();
                user.setUserID(rs.getInt("UserID"));
                user.setName(rs.getString("Name"));
                user.setUsername(rs.getString("Username"));
                user.setPassword(rs.getString("Password"));
                user.setContactNumber(rs.getString("ContactNumber"));
                user.setAddress(rs.getString("Address"));
                user.setRole(rs.getString("Role"));
                user.setPicture(rs.getBytes("Picture"));
                users.add(user);
            }
        }
        return users;
    }

    public User getUserByCredentials(String username, String password) throws SQLException {
        String sql = "SELECT * FROM Users WHERE Username = ? AND Password = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setUserID(rs.getInt("UserID"));
                    user.setName(rs.getString("Name"));
                    user.setUsername(rs.getString("Username"));
                    user.setPassword(rs.getString("Password"));
                    user.setContactNumber(rs.getString("ContactNumber"));
                    user.setAddress(rs.getString("Address"));
                    user.setRole(rs.getString("Role"));
                    user.setPicture(rs.getBytes("Picture"));
                    return user;
                }
            }
        }
        return null; // Return null if no user is found
    }
}
