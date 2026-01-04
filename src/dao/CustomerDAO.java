package dao;

import db.ConnectionFactory;
import java.math.BigDecimal;
import models.Customer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO implements BaseDAO<Customer> {

    @Override
    public boolean add(Customer customer) throws SQLException {
        String sql = "INSERT INTO Customers (Name, ContactNumber, Address, CustomerType, OutstandingAmount, CreditLimit, CreatedAt) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getContactNumber());
            stmt.setString(3, customer.getAddress());
            stmt.setString(4, customer.getCustomerType());
            stmt.setBigDecimal(5, customer.getOutstandingAmount());
            stmt.setBigDecimal(6, customer.getCreditLimit());
            stmt.setTimestamp(7, customer.getCreatedAt());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean update(Customer customer) throws SQLException {
        String sql = "UPDATE Customers SET Name = ?, ContactNumber = ?, Address = ?, CustomerType = ?, OutstandingAmount = ?, CreditLimit = ? WHERE CustomerID = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getContactNumber());
            stmt.setString(3, customer.getAddress());
            stmt.setString(4, customer.getCustomerType());
            stmt.setBigDecimal(5, customer.getOutstandingAmount());
            stmt.setBigDecimal(6, customer.getCreditLimit());
            stmt.setInt(7, customer.getCustomerID());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM Customers WHERE CustomerID = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public Customer getById(int id) throws SQLException {
        String sql = "SELECT * FROM Customers WHERE CustomerID = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapCustomer(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Customer> getAll() throws SQLException {
        String sql = "SELECT * FROM Customers";
        List<Customer> customers = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                customers.add(mapCustomer(rs));
            }
        }
        return customers;
    }

    // ✅ Helper method to avoid repetition
    private Customer mapCustomer(ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        customer.setCustomerID(rs.getInt("CustomerID"));
        customer.setName(rs.getString("Name"));
        customer.setContactNumber(rs.getString("ContactNumber"));
        customer.setAddress(rs.getString("Address"));
        customer.setCustomerType(rs.getString("CustomerType"));
        customer.setOutstandingAmount(rs.getBigDecimal("OutstandingAmount"));
        customer.setCreditLimit(rs.getBigDecimal("CreditLimit"));
        customer.setCreatedAt(rs.getTimestamp("CreatedAt"));
        return customer;
    }
    
    // Add this method to CustomerDAO
public boolean updateOutstandingAmount(int customerId, BigDecimal newOutstanding) throws SQLException {
    String sql = "UPDATE Customers SET OutstandingAmount = ? WHERE CustomerID = ?";
    try (Connection conn = ConnectionFactory.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setBigDecimal(1, newOutstanding);
        stmt.setInt(2, customerId);
        return stmt.executeUpdate() > 0;
    }
}

}
