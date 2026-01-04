package dao;

import models.WholesaleOrderItem;
import db.ConnectionFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WholesaleOrderItemsDAO {
    private final Connection connection;

    public WholesaleOrderItemsDAO() throws SQLException {
        connection = ConnectionFactory.getConnection();
    }

    // Add items to an order
    public void addOrderItem(WholesaleOrderItem item) throws SQLException {
        String query = "INSERT INTO WholesaleOrderItems (orderID, itemName, quantity, unitPrice) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, item.getOrderID());
            stmt.setString(2, item.getItemName());
            stmt.setInt(3, item.getQuantity());
            stmt.setBigDecimal(4, item.getUnitPrice());
            stmt.executeUpdate();
        }
    }

    // Get items for an order
    public List<WholesaleOrderItem> getItemsByOrder(int orderID) throws SQLException {
        List<WholesaleOrderItem> items = new ArrayList<>();
        String query = "SELECT * FROM WholesaleOrderItems WHERE orderID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, orderID);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                items.add(new WholesaleOrderItem(
                    rs.getInt("orderItemID"),
                    rs.getInt("orderID"),
                    rs.getString("itemName"),
                    rs.getInt("quantity"),
                    rs.getBigDecimal("unitPrice"),
                    rs.getBigDecimal("totalPrice")
                ));
            }
        }
        return items;
    }
}
