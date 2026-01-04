package models;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

public class CheckPayment {

    public CheckPayment(int aInt, String string, Timestamp timestamp, BigDecimal bigDecimal, String string1, String string2, String string3, Date date) {
    }
    private int paymentId;
    private String billId;
    private Timestamp paymentDate;
    private BigDecimal amount;
    private String method;
    private String bank;
    private String chequeNo;
    private Date chequeDate;

    // Getters and Setters
    public int getPaymentId() { return paymentId; }
    public void setPaymentId(int paymentId) { this.paymentId = paymentId; }

    public String getBillId() { return billId; }
    public void setBillId(String billId) { this.billId = billId; }

    public Timestamp getPaymentDate() { return paymentDate; }
    public void setPaymentDate(Timestamp paymentDate) { this.paymentDate = paymentDate; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public String getBank() { return bank; }
    public void setBank(String bank) { this.bank = bank; }

    public String getChequeNo() { return chequeNo; }
    public void setChequeNo(String chequeNo) { this.chequeNo = chequeNo; }

    public Date getChequeDate() { return chequeDate; }
    public void setChequeDate(Date chequeDate) { this.chequeDate = chequeDate; }
}
