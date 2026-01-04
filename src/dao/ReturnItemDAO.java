package dao;

import models.ReturnItem;
import dao.ItemDAO;
import db.ConnectionFactory;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReturnItemDAO {
    private Connection connection;
    private ItemDAO itemDAO;

    public ReturnItemDAO() throws SQLException {
        this.connection = ConnectionFactory.getConnection();
        this.itemDAO = new ItemDAO();
    }

    public void addReturnItem(ReturnItem returnItem) throws SQLException {
        String query = "INSERT INTO ReturnItems (itemCode, itemName, returnQuantity, returnReason, supplier, stockType, returnDate) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, returnItem.getItemCode());
            pstmt.setString(2, returnItem.getItemName());
            pstmt.setInt(3, returnItem.getReturnQuantity());
            pstmt.setString(4, returnItem.getReturnReason());
            pstmt.setString(5, returnItem.getSupplier());
            pstmt.setString(6, returnItem.getStockType());
            pstmt.setDate(7, Date.valueOf(returnItem.getReturnDate())); // Store today's date

            pstmt.executeUpdate();

            // Reduce item quantity in stock
            int itemID = itemDAO.getItemIDByCode(returnItem.getItemCode());
            itemDAO.reduceQuantity(itemID, returnItem.getReturnQuantity());
        }
    }

    public List<ReturnItem> getAll() throws SQLException {
        List<ReturnItem> returnItems = new ArrayList<>();
        String query = "SELECT * FROM ReturnItems";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                ReturnItem item = new ReturnItem(
                    rs.getString("itemCode"),
                    rs.getString("itemName"),
                    rs.getInt("returnQuantity"),
                    rs.getString("returnReason"),
                    rs.getString("supplier"),
                    rs.getString("stockType"),
                    rs.getDate("returnDate").toLocalDate()
                );
                returnItems.add(item);
            }
        }
        return returnItems;
    }

    public boolean deleteByItemCode(String itemCode) throws SQLException {
        String query = "DELETE FROM ReturnItems WHERE itemCode = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, itemCode);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean updateReturnItem(ReturnItem returnItem) {
        String sql = "UPDATE ReturnItems SET itemName = ?, returnQuantity = ?, returnReason = ?, supplier = ?, stockType = ?, returnDate = ? WHERE itemCode = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, returnItem.getItemName());
            stmt.setInt(2, returnItem.getReturnQuantity());
            stmt.setString(3, returnItem.getReturnReason());
            stmt.setString(4, returnItem.getSupplier());
            stmt.setString(5, returnItem.getStockType());
            stmt.setDate(6, Date.valueOf(returnItem.getReturnDate()));
            stmt.setString(7, returnItem.getItemCode());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<ReturnItem> getReturnItemsByDateRange(String startDate, String endDate) {
        List<ReturnItem> returnItems = new ArrayList<>();
        String sql = "SELECT * FROM ReturnItems WHERE returnDate BETWEEN ? AND ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ReturnItem returnItem = new ReturnItem(
                    rs.getString("itemCode"),
                    rs.getString("itemName"),
                    rs.getInt("returnQuantity"),
                    rs.getString("returnReason"),
                    rs.getString("supplier"),
                    rs.getString("stockType"),
                    rs.getDate("returnDate").toLocalDate()
                );
                returnItems.add(returnItem);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return returnItems;
    }

    public boolean saveReturnItem(ReturnItem returnItem) {
        String sql = "INSERT INTO ReturnItems (itemCode, itemName, returnQuantity, returnReason, supplier, stockType, returnDate) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, returnItem.getItemCode());
            stmt.setString(2, returnItem.getItemName());
            stmt.setInt(3, returnItem.getReturnQuantity());
            stmt.setString(4, returnItem.getReturnReason());
            stmt.setString(5, returnItem.getSupplier());
            stmt.setString(6, returnItem.getStockType());
            stmt.setDate(7, Date.valueOf(returnItem.getReturnDate()));

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteReturnItem(String itemCode) throws SQLException {
    String sql = "DELETE FROM ReturnItems WHERE ItemCode = ?";
    try (Connection conn = ConnectionFactory.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setString(1, itemCode);
        return stmt.executeUpdate() > 0;
    }
}




        
    
}
