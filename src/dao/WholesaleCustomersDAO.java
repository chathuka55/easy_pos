package dao;

import models.WholesaleCustomer;
import db.ConnectionFactory;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WholesaleCustomersDAO {
    private final Connection connection;

    public WholesaleCustomersDAO() throws SQLException {
        connection = ConnectionFactory.getConnection();
    }

    // Add a new wholesale customer
    public void addCustomer(WholesaleCustomer customer) throws SQLException {
        String query = "INSERT INTO WholesaleCustomers (customerName, contactNumber, email, address) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, customer.getCustomerName());
            stmt.setString(2, customer.getContactNumber());
            stmt.setString(3, customer.getEmail());
            stmt.setString(4, customer.getAddress());
            stmt.executeUpdate();
        }
    }

    // Get a customer by ID
    public WholesaleCustomer getCustomerById(int customerID) throws SQLException {
        String query = "SELECT * FROM WholesaleCustomers WHERE customerID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, customerID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new WholesaleCustomer(
                    rs.getInt("customerID"),
                    rs.getString("customerName"),
                    rs.getString("contactNumber"),
                    rs.getString("email"),
                    rs.getString("address"),
                    rs.getBigDecimal("totalOutstanding")
                );
            }
        }
        return null;
    }

    // Get all customers
    public List<WholesaleCustomer> getAllCustomers() throws SQLException {
        List<WholesaleCustomer> customers = new ArrayList<>();
        String query = "SELECT * FROM WholesaleCustomers";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                customers.add(new WholesaleCustomer(
                    rs.getInt("customerID"),
                    rs.getString("customerName"),
                    rs.getString("contactNumber"),
                    rs.getString("email"),
                    rs.getString("address"),
                    rs.getBigDecimal("totalOutstanding")
                ));
            }
        }
        return customers;
    }
    
    
    public void updateCustomer(WholesaleCustomer customer) throws SQLException {
    String query = "UPDATE WholesaleCustomers SET customerName = ?, contactNumber = ?, email = ?, address = ? WHERE customerID = ?";
    try (PreparedStatement stmt = connection.prepareStatement(query)) {
        stmt.setString(1, customer.getCustomerName());
        stmt.setString(2, customer.getContactNumber());
        stmt.setString(3, customer.getEmail());
        stmt.setString(4, customer.getAddress());
        stmt.setInt(5, customer.getCustomerID());
        stmt.executeUpdate();
    }
}

    public void deleteCustomer(int customerID) throws SQLException {
    String query = "DELETE FROM WholesaleCustomers WHERE customerID = ?";
    try (PreparedStatement stmt = connection.prepareStatement(query)) {
        stmt.setInt(1, customerID);
        stmt.executeUpdate();
    }
}

    public BigDecimal getOutstandingAmount(int customerID) throws SQLException {
    String query = "SELECT outstandingAmount FROM WholesaleCustomers WHERE customerID = ?";
    try (PreparedStatement stmt = connection.prepareStatement(query)) {
        stmt.setInt(1, customerID);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getBigDecimal("outstandingAmount");
        }
    }
    return BigDecimal.ZERO; // Default if no outstanding balance found
}
    
  


}
