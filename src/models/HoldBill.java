/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

public class HoldBill {
  private int holdID;
  private String billCode; // Updated to BillCode as foreign key
  private BigDecimal amountPaid;
  private Date paymentDate;
  private BigDecimal remainingBalance;

   

  // Getters and Setters
  public int getHoldID() {
    return holdID;
  }

  public void setHoldID(int holdID) {
    this.holdID = holdID;
  }

  public String getBillCode() {
    return billCode;
  }

  public void setBillCode(String billCode) {
    this.billCode = billCode;
  }

  public BigDecimal getAmountPaid() {
    return amountPaid;
  }

  public void setAmountPaid(BigDecimal amountPaid) {
    this.amountPaid = amountPaid;
  }

  public Date getPaymentDate() {
    return paymentDate;
  }

  public void setPaymentDate(Date paymentDate) {
    this.paymentDate = paymentDate;
  }

  public BigDecimal getRemainingBalance() {
    return remainingBalance;
  }

  public void setRemainingBalance(BigDecimal remainingBalance) {
    this.remainingBalance = remainingBalance;
  }
}