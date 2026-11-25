
package PurchaseManagement;

import java.sql.SQLException;
import java.math.BigDecimal;
import java.time.LocalDate;

public class PurchaseService {

    private final PurchaseDAO dao;

    public PurchaseService() {
        this.dao = new PurchaseDAO();
    }

    public boolean addPurchase(Purchase purchase) throws SQLException {

        if (purchase == null) {
            throw new IllegalArgumentException("Purchase cannot be null.");
        }

        if (purchase.getSupplierId() <= 0) {
            throw new IllegalArgumentException("Invalid supplier ID.");
        }

        if (purchase.getInvoiceNumber() == null ||
            purchase.getInvoiceNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Invoice number is required.");
        }

        if (purchase.getPurchaseDate() == null ||
            purchase.getPurchaseDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Invalid purchase date.");
        }

        if (purchase.getItems() == null || purchase.getItems().isEmpty()) {
            throw new IllegalArgumentException("At least one product is required.");
        }

        for (PurchaseItem item : purchase.getItems()) {

            if (item.getProductId() <= 0) {
                throw new IllegalArgumentException("Invalid product ID.");
            }

            if (item.getQuantity() <= 0) {
                throw new IllegalArgumentException("Quantity must be positive.");
            }

            if (item.getUnitCost() == null ||
                item.getUnitCost().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Purchase price must be positive.");
            }

            if (item.getExpiryDate() != null &&
                item.getExpiryDate().isBefore(purchase.getPurchaseDate())) {
                throw new IllegalArgumentException("Expiry date cannot be before purchase date.");
            }
        }

        return dao.addPurchase(purchase);
    }
}
