package dao;

import models.WholesaleOrder;
import db.ConnectionFactory;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WholesaleOrdersDAO {
    private final Connection connection;

    public WholesaleOrdersDAO() throws SQLException {
        connection = ConnectionFactory.getConnection();
    }

    // Add a new order
    public void addOrder(WholesaleOrder order) throws SQLException {
    String query = "INSERT INTO WholesaleOrders (customerID, totalAmount, amountPaid, outstandingBalance) VALUES (?, ?, ?, ?)";
    try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
        stmt.setInt(1, order.getCustomerID());
        stmt.setBigDecimal(2, order.getTotalAmount());
        stmt.setBigDecimal(3, order.getAmountPaid());
        stmt.setBigDecimal(4, order.getOutstandingBalance()); // Added this line
        stmt.executeUpdate();

        // Retrieve the generated order ID
        ResultSet rs = stmt.getGeneratedKeys();
        if (rs.next()) {
            order.setOrderID(rs.getInt(1));
        }
    }
}


    // Get orders for a customer
    public List<WholesaleOrder> getOrdersByCustomer(int customerID) throws SQLException {
        List<WholesaleOrder> orders = new ArrayList<>();
        String query = "SELECT * FROM WholesaleOrders WHERE customerID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, customerID);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                orders.add(new WholesaleOrder(
                    rs.getInt("orderID"),
                    rs.getInt("customerID"),
                    rs.getTimestamp("orderDate"),
                    rs.getBigDecimal("totalAmount"),
                    rs.getBigDecimal("amountPaid"),
                    rs.getBigDecimal("outstandingBalance")
                ));
            }
        }
        return orders;
    }

    public List<WholesaleOrder> getAllOrders() throws SQLException {
    List<WholesaleOrder> orders = new ArrayList<>();
    String query = "SELECT * FROM WholesaleOrders";
    try (PreparedStatement stmt = connection.prepareStatement(query);
         ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
            orders.add(new WholesaleOrder(
                rs.getInt("orderID"),
                rs.getInt("customerID"),
                rs.getTimestamp("orderDate"),
                rs.getBigDecimal("totalAmount"),
                rs.getBigDecimal("amountPaid"),
                rs.getBigDecimal("outstandingBalance")
            ));
        }
    }
    return orders;
}
    
    public void updateOrderBalance(int orderID, BigDecimal paymentAmount) throws SQLException {
    String query = "UPDATE WholesaleOrders SET amountPaid = amountPaid + ?, outstandingBalance = totalAmount - amountPaid WHERE orderID = ?";
    try (PreparedStatement stmt = connection.prepareStatement(query)) {
        stmt.setBigDecimal(1, paymentAmount);
        stmt.setInt(2, orderID);
        stmt.executeUpdate();
    }
}

    
    public void deleteOrder(int orderID) throws SQLException {
    String query = "DELETE FROM WholesaleOrders WHERE orderID = ?";
    try (PreparedStatement stmt = connection.prepareStatement(query)) {
        stmt.setInt(1, orderID);
        stmt.executeUpdate();
    }
}

    
    public void updateOrder(WholesaleOrder order) throws SQLException {
    String query = "UPDATE WholesaleOrders SET customerID = ?, totalAmount = ?, amountPaid = ? WHERE orderID = ?";
    try (PreparedStatement stmt = connection.prepareStatement(query)) {
        stmt.setInt(1, order.getCustomerID());
        stmt.setBigDecimal(2, order.getTotalAmount());
        stmt.setBigDecimal(3, order.getAmountPaid());
        stmt.setInt(4, order.getOrderID());
        stmt.executeUpdate();
    }
}
    
    


}