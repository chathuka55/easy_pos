/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

/**
 *
 * @author CJAY
 */

import java.util.Date;

public class PurchaseHistory {
    private int historyID;         // Unique ID for purchase history
    private String billCode;       // Associated bill code
    private String customerName;   // Customer's name
    private String customerType;   // Retail or Wholesale
    private String purchasedItems; // Items purchased (comma-separated names or IDs)
    private double totalPayment;   // Total amount paid
    private double outstandingAmount; // Outstanding payment amount
    private double discount;       // Discount applied
    private Date purchaseDate;     // Date of purchase
    private String returnType;     // Return type, if applicable
    private int returnQuantity;    // Quantity of returned items

    // Getters and Setters
    public int getHistoryID() { return historyID; }
    public void setHistoryID(int historyID) { this.historyID = historyID; }
    public String getBillCode() { return billCode; }
    public void setBillCode(String billCode) { this.billCode = billCode; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getCustomerType() { return customerType; }
    public void setCustomerType(String customerType) { this.customerType = customerType; }
    public String getPurchasedItems() { return purchasedItems; }
    public void setPurchasedItems(String purchasedItems) { this.purchasedItems = purchasedItems; }
    public double getTotalPayment() { return totalPayment; }
    public void setTotalPayment(double totalPayment) { this.totalPayment = totalPayment; }
    public double getOutstandingAmount() { return outstandingAmount; }
    public void setOutstandingAmount(double outstandingAmount) { this.outstandingAmount = outstandingAmount; }
    public double getDiscount() { return discount; }
    public void setDiscount(double discount) { this.discount = discount; }
    public Date getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(Date purchaseDate) { this.purchaseDate = purchaseDate; }
    public String getReturnType() { return returnType; }
    public void setReturnType(String returnType) { this.returnType = returnType; }
    public int getReturnQuantity() { return returnQuantity; }
    public void setReturnQuantity(int returnQuantity) { this.returnQuantity = returnQuantity; }
}