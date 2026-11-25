
package PurchaseManagement;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class Purchase {

    private Integer purchaseId;
    private int supplierId;
    private String invoiceNumber;
    private LocalDate purchaseDate;
    private BigDecimal totalAmount;
    private List<PurchaseItem> items;

    public Purchase(int supplierId, String invoiceNumber,
                    LocalDate purchaseDate, List<PurchaseItem> items) {

        this.supplierId = supplierId;
        this.invoiceNumber = invoiceNumber;
        this.purchaseDate = purchaseDate;
        this.items = items;
    }

    public Integer getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(Integer purchaseId) {
        this.purchaseId = purchaseId;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<PurchaseItem> getItems() {
        return items;
    }
}

