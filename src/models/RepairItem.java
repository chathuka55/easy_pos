package models;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class RepairItem {
    private int repairItemId;
    private String repairId;
    private String itemName;
    private String warranty;
    private int quantity;
    private BigDecimal price;
    private BigDecimal total;           // Subtotal before discount
    private BigDecimal discount;        // Discount value (10 for 10% or 100 for Rs.100)
    private String discountType;        // "NONE", "PERCENTAGE", "FIXED"
    private BigDecimal discountAmount;  // Calculated discount amount in rupees
    private BigDecimal finalTotal;      // Total after discount

    // Constructor
    public RepairItem() {
        this.discount = BigDecimal.ZERO;
        this.discountAmount = BigDecimal.ZERO;
        this.discountType = "NONE";
        this.finalTotal = BigDecimal.ZERO;
    }

    // Calculate totals automatically
    public void calculateTotals() {
        // Calculate subtotal
        if (price != null && quantity > 0) {
            total = price.multiply(BigDecimal.valueOf(quantity))
                        .setScale(2, RoundingMode.HALF_UP);
        } else {
            total = BigDecimal.ZERO;
        }

        // Calculate discount amount
        if (discount != null && discount.compareTo(BigDecimal.ZERO) > 0 && 
            discountType != null && !discountType.equals("NONE")) {
            
            if (discountType.equals("PERCENTAGE")) {
                // Percentage discount
                discountAmount = total.multiply(discount)
                                     .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            } else if (discountType.equals("FIXED")) {
                // Fixed amount discount
                discountAmount = discount;
            } else {
                discountAmount = BigDecimal.ZERO;
            }
        } else {
            discountAmount = BigDecimal.ZERO;
        }

        // Ensure discount doesn't exceed total
        if (discountAmount.compareTo(total) > 0) {
            discountAmount = total;
        }

        // Calculate final total
        finalTotal = total.subtract(discountAmount).setScale(2, RoundingMode.HALF_UP);
    }

    // Get formatted discount for display in table
    public String getDiscountDisplay() {
        if (discountType == null || discountType.equals("NONE") || 
            discount == null || discount.compareTo(BigDecimal.ZERO) == 0) {
            return "-";
        }
        
        if (discountType.equals("PERCENTAGE")) {
            return discount.setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString() + "%";
        } else if (discountType.equals("FIXED")) {
            return "Rs. " + discount.setScale(2, RoundingMode.HALF_UP).toPlainString();
        }
        
        return "-";
    }

    // Getters and Setters
    
    public int getRepairItemId() {
        return repairItemId;
    }

    public void setRepairItemId(int repairItemId) {
        this.repairItemId = repairItemId;
    }

    public String getRepairId() {
        return repairId;
    }

    public void setRepairId(String repairId) {
        this.repairId = repairId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getWarranty() {
        return warranty;
    }

    public void setWarranty(String warranty) {
        this.warranty = warranty;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        calculateTotals();
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
        calculateTotals();
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
        calculateTotals();
    }

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
        calculateTotals();
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getFinalTotal() {
        return finalTotal;
    }

    public void setFinalTotal(BigDecimal finalTotal) {
        this.finalTotal = finalTotal;
    }
}