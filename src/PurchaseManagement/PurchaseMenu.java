package PurchaseManagement;

import Database.DBConnection;
import UI.ConsoleUtils;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Terminal menu for recording and viewing stock purchases.
 */
public class PurchaseMenu {

    private static final PurchaseService service = new PurchaseService();
    private static final PurchaseDAO dao = new PurchaseDAO();

    public static void main(String[] args) {
        try (Connection con = DBConnection.getConnection()) {
            System.out.println("\nDatabase Connected: " + con.getCatalog());
        } catch (SQLException e) {
            System.out.println("Database Connection Failed: " + e.getMessage());
            return;
        }
        try (Scanner sc = new Scanner(System.in)) {
            run(sc);
        }
    }

    public static void run(Scanner sc) {
        int choice;
        do {
            System.out.println("\n========== PURCHASE MANAGEMENT ==========");
            System.out.println("1. Create Purchase");
            System.out.println("2. View All Purchases");
            System.out.println("3. View Purchase Items by Purchase ID");
            System.out.println("4. Back");
            choice = ConsoleUtils.readInt(sc, "Enter your choice: ");

            try {
                switch (choice) {
                    case 1 -> createPurchase(sc);
                    case 2 -> viewPurchases();
                    case 3 -> viewPurchaseItems(sc);
                    case 4 -> System.out.println("Returning to main menu...");
                    default -> System.out.println("Invalid choice.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Validation Error: " + e.getMessage());
            } catch (SQLException e) {
                System.out.println("Purchase operation failed: " + e.getMessage());
            }
        } while (choice != 4);
    }

    private static void createPurchase(Scanner sc) throws SQLException {
        System.out.println("\n========== CREATE PURCHASE ==========");
        int supplierId = ConsoleUtils.readPositiveInt(sc, "Enter Supplier ID: ");
        String invoiceNumber = ConsoleUtils.readNonEmpty(sc, "Enter Invoice Number: ");

        List<PurchaseItem> items = new ArrayList<>();
        do {
            System.out.println("\n---------- PRODUCT DETAILS ----------");
            int productId = ConsoleUtils.readPositiveInt(sc, "Enter Product ID: ");
            int quantity = ConsoleUtils.readPositiveInt(sc, "Enter Quantity: ");
            BigDecimal unitCost = ConsoleUtils.readPositiveAmount(sc, "Enter Purchase Price Per Unit: ");
            System.out.print("Enter Batch Number (optional): ");
            String batchNumber = sc.nextLine().trim();
            LocalDate expiryDate = ConsoleUtils.readOptionalDate(sc, "Enter Expiry Date (YYYY-MM-DD) or press Enter to skip: ");

            items.add(new PurchaseItem(
                    productId,
                    quantity,
                    unitCost,
                    batchNumber.isEmpty() ? null : batchNumber,
                    expiryDate
            ));
        } while (ConsoleUtils.confirm(sc, "Add another product? (Y/N): "));

        Purchase purchase = new Purchase(supplierId, invoiceNumber, LocalDate.now(), items);

        System.out.println("\n========== PURCHASE SUMMARY ==========");
        System.out.println("Supplier ID:    " + supplierId);
        System.out.println("Invoice Number: " + invoiceNumber);
        System.out.println("Purchase Date:  " + purchase.getPurchaseDate());

        BigDecimal total = BigDecimal.ZERO;
        for (PurchaseItem item : items) {
            System.out.println("--------------------------------");
            System.out.println("Product ID: " + item.getProductId()
                    + " | Qty: " + item.getQuantity()
                    + " | Unit Cost: " + item.getUnitCost()
                    + " | Subtotal: " + item.getSubtotal());
            total = total.add(item.getSubtotal());
        }
        System.out.println("--------------------------------");
        System.out.println("Total Purchase Amount: " + total);

        if (ConsoleUtils.confirm(sc, "\nConfirm Purchase? (Y/N): ")) {
            if (service.addPurchase(purchase)) {
                System.out.println("\nPurchase added successfully!");
                System.out.println("Purchase ID:  " + purchase.getPurchaseId());
                System.out.println("Total Amount: " + purchase.getTotalAmount());
                System.out.println("Product stock & stock_history updated.");
            }
        } else {
            System.out.println("Purchase cancelled.");
        }
    }

    private static void viewPurchases() throws SQLException {
        List<Map<String, Object>> purchases = dao.getRecentPurchases();
        System.out.println("\n========== PURCHASE HISTORY ==========");
        if (purchases.isEmpty()) {
            System.out.println("No purchases recorded.");
            return;
        }
        for (Map<String, Object> p : purchases) {
            System.out.printf("ID: %d | Supplier: %s (#%d) | Invoice: %s | Date: %s | Total: %s%n",
                    p.get("purchaseId"), p.get("supplierName"), p.get("supplierId"),
                    p.get("invoiceNumber"), p.get("purchaseDate"), p.get("totalAmount"));
        }
    }

    private static void viewPurchaseItems(Scanner sc) throws SQLException {
        int purchaseId = ConsoleUtils.readPositiveInt(sc, "Enter Purchase ID: ");
        List<Map<String, Object>> items = dao.getPurchaseItems(purchaseId);
        if (items.isEmpty()) {
            System.out.println("No items found for purchase ID " + purchaseId);
            return;
        }
        System.out.println("\n---------- ITEMS FOR PURCHASE #" + purchaseId + " ----------");
        for (Map<String, Object> item : items) {
            System.out.printf("Product #%d (%s) | Qty: %d | Unit Cost: %s | Batch: %s | Expiry: %s%n",
                    item.get("productId"), item.get("productName"), item.get("quantity"),
                    item.get("unitCost"), item.get("batchNumber"), item.get("expiryDate"));
        }
    }
}
