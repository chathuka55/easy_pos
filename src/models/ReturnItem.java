package models;

import java.time.LocalDate;

public class ReturnItem {
    private String itemCode;
    private String itemName;
    private int returnQuantity;
    private String returnReason;
    private String supplier;
    private String stockType;
    private LocalDate returnDate; // New field

    public ReturnItem(String itemCode, String itemName, int returnQuantity, String returnReason, String supplier, String stockType, LocalDate returnDate) {
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.returnQuantity = returnQuantity;
        this.returnReason = returnReason;
        this.supplier = supplier;
        this.stockType = stockType;
        this.returnDate = returnDate;
    }

    public String getItemCode() { return itemCode; }
    public void setItemCode(String itemCode) { this.itemCode = itemCode; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public int getReturnQuantity() { return returnQuantity; }
    public void setReturnQuantity(int returnQuantity) { this.returnQuantity = returnQuantity; }

    public String getReturnReason() { return returnReason; }
    public void setReturnReason(String returnReason) { this.returnReason = returnReason; }

    public String getSupplier() { return supplier; }
    public void setSupplier(String supplier) { this.supplier = supplier; }

    public String getStockType() { return stockType; }
    public void setStockType(String stockType) { this.stockType = stockType; }

    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }
}
