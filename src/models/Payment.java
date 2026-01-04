// Payment.java
package models;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Objects;

/**
 * @author CJAY
 */
public class Payment {
  private int paymentID;
  private String billCode; // Updated to BillCode as foreign key
  private String paymentType;
  private BigDecimal amount;
  private Timestamp paymentDate;

  // Constructors
  public Payment() {}

  public Payment(String billCode, String paymentType, BigDecimal amount, Timestamp paymentDate) {
    this.billCode = billCode;
    this.paymentType = paymentType;
    this.amount = amount;
    this.paymentDate = paymentDate;
  }

  // Getters and Setters
  public int getPaymentID() {
    return paymentID;
  }

  public void setPaymentID(int paymentID) {
    this.paymentID = paymentID;
  }

  public String getBillCode() {
    return billCode;
  }

  public void setBillCode(String billCode) {
    this.billCode = billCode;
  }

  public String getPaymentType() {
    return paymentType;
  }

  public void setPaymentType(String paymentType) {
    this.paymentType = paymentType;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public Timestamp getPaymentDate() {
    return paymentDate;
  }

  public void setPaymentDate(Timestamp paymentDate) {
    this.paymentDate = paymentDate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Payment payment = (Payment) o;
    return paymentID == payment.paymentID && Objects.equals(billCode, payment.billCode) && Objects.equals(paymentType, payment.paymentType) && Objects.equals(amount, payment.amount) && Objects.equals(paymentDate, payment.paymentDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(paymentID, billCode, paymentType, amount, paymentDate);
  }
}