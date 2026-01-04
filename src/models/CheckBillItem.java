package models;

import java.math.BigDecimal;

public class CheckBillItem {

    // ✅ No-arg constructor
    public CheckBillItem() {
    }

    // Optional: Full-arg constructor if needed
    public CheckBillItem(int itemId, String billId, String itemName, BigDecimal price, int quantity, String warranty, BigDecimal total) {
        this.itemId = itemId;
        this.billId = billId;
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
        this.warranty = warranty;
        this.total = total;
    }

    private int itemId;
    private String billId;
    private String itemName;
    private BigDecimal price;
    private int quantity;
    private String warranty;
    private BigDecimal total;

    // Getters and Setters
    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }

    public String getBillId() { return billId; }
    public void setBillId(String billId) { this.billId = billId; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getWarranty() { return warranty; }
    public void setWarranty(String warranty) { this.warranty = warranty; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    
}
