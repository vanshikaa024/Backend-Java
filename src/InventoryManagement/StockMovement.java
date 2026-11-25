package InventoryManagement;

import java.sql.Timestamp;

/**
 * Represents a stock movement record in stock_history.
 */
public class StockMovement {
    private final int productId;
    private final String transactionType;
    private final int quantity;
    private final int previousStock;
    private final int updatedStock;
    private final Integer purchaseId;
    private final Integer saleId;
    private final String remarks;
    private Timestamp createdAt;

    public StockMovement(int productId, String transactionType,
                         int quantity, int previousStock, int updatedStock,
                         Integer purchaseId, Integer saleId, String remarks) {
        this.productId = productId;
        this.transactionType = transactionType;
        this.quantity = quantity;
        this.previousStock = previousStock;
        this.updatedStock = updatedStock;
        this.purchaseId = purchaseId;
        this.saleId = saleId;
        this.remarks = remarks;
    }

    public int getProductId() {
        return productId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getPreviousStock() {
        return previousStock;
    }

    public int getUpdatedStock() {
        return updatedStock;
    }

    public Integer getPurchaseId() {
        return purchaseId;
    }

    public Integer getSaleId() {
        return saleId;
    }

    public String getRemarks() {
        return remarks;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
