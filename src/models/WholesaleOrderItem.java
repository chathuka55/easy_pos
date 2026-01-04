package models;

import java.math.BigDecimal;

public class WholesaleOrderItem {
    private int orderItemID;
    private int orderID;
    private String itemName;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;

    // Constructor to initialize all fields
    public WholesaleOrderItem(int orderItemID, int orderID, String itemName, int quantity, BigDecimal unitPrice, BigDecimal totalPrice) {
        this.orderItemID = orderItemID;
        this.orderID = orderID;
        this.itemName = itemName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
    }

    // Getters and Setters
    public int getOrderItemID() {
        return orderItemID;
    }

    public void setOrderItemID(int orderItemID) {
        this.orderItemID = orderItemID;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    // Optional: Override toString for better printing/logging
    @Override
    public String toString() {
        return "WholesaleOrderItem{" +
                "orderItemID=" + orderItemID +
                ", orderID=" + orderID +
                ", itemName='" + itemName + '\'' +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", totalPrice=" + totalPrice +
                '}';
    }
}
