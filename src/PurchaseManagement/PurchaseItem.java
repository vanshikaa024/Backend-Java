
package PurchaseManagement;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PurchaseItem {

    private int productId;
    private int quantity;
    private BigDecimal unitCost;
    private String batchNumber;
    private LocalDate expiryDate;

    public PurchaseItem(int productId, int quantity,
                        BigDecimal unitCost, String batchNumber,
                        LocalDate expiryDate) {

        this.productId = productId;
        this.quantity = quantity;
        this.unitCost = unitCost;
        this.batchNumber = batchNumber;
        this.expiryDate = expiryDate;
    }

    public int getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitCost() {
        return unitCost;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public BigDecimal getSubtotal() {
        return unitCost.multiply(BigDecimal.valueOf(quantity));
    }
}

