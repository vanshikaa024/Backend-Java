
package PurchaseManagement;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PurchaseMenu {

    static Scanner sc = new Scanner(System.in);
    static PurchaseService service = new PurchaseService();

    public static void main(String[] args) {

        int choice;

        do {
            System.out.println("\n========== SMARTSHELF PURCHASE MANAGEMENT ==========");
            System.out.println("1. Create Purchase");
            System.out.println("2. Exit");
            System.out.print("Enter your choice: ");

            try {
                choice = Integer.parseInt(sc.nextLine());

                switch (choice) {
                    case 1:
                        createPurchase();
                        break;

                    case 2:
                        System.out.println("Exiting Purchase Management...");
                        break;

                    default:
                        System.out.println("Invalid choice.");
                }

            } catch (NumberFormatException e) {
                System.out.println("Enter a valid number.");
                choice = 0;

            } catch (IllegalArgumentException e) {
                System.out.println("Validation Error: " + e.getMessage());
                choice = 0;

            } catch (SQLException e) {
                System.out.println("Purchase operation failed: " + e.getMessage());
                choice = 0;
            }

        } while (choice != 2);

        sc.close();
    }

    private static int readPositiveInt(String message) {

        while (true) {
            System.out.print(message);

            try {
                int value = Integer.parseInt(sc.nextLine().trim());

                if (value > 0) {
                    return value;
                }

                System.out.println("Value must be greater than zero.");

            } catch (NumberFormatException e) {
                System.out.println("Enter a valid number.");
            }
        }
    }

    private static BigDecimal readPositiveAmount(String message) {

        while (true) {
            System.out.print(message);

            try {
                BigDecimal value = new BigDecimal(sc.nextLine().trim());

                if (value.compareTo(BigDecimal.ZERO) > 0) {
                    return value;
                }

                System.out.println("Amount must be greater than zero.");

            } catch (NumberFormatException e) {
                System.out.println("Enter a valid amount.");
            }
        }
    }

    private static LocalDate readExpiryDate() {

        while (true) {
            System.out.print("Enter Expiry Date (YYYY-MM-DD) or press Enter to skip: ");

            String input = sc.nextLine().trim();

            if (input.isEmpty()) {
                return null;
            }

            try {
                return LocalDate.parse(input);

            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format.");
            }
        }
    }

    private static void createPurchase() throws SQLException {

        System.out.println("\n========== CREATE PURCHASE ==========");

        int supplierId = readPositiveInt("Enter Supplier ID: ");

        System.out.print("Enter Invoice Number: ");
        String invoiceNumber = sc.nextLine().trim();

        List<PurchaseItem> items = new ArrayList<>();

        String more;

        do {
            System.out.println("\n---------- PRODUCT DETAILS ----------");

            int productId = readPositiveInt("Enter Product ID: ");

            int quantity = readPositiveInt("Enter Quantity: ");

            BigDecimal unitCost = readPositiveAmount("Enter Purchase Price Per Unit: ");

            System.out.print("Enter Batch Number: ");
            String batchNumber = sc.nextLine().trim();

            LocalDate expiryDate = readExpiryDate();

            items.add(new PurchaseItem(
                productId,
                quantity,
                unitCost,
                batchNumber.isEmpty() ? null : batchNumber,
                expiryDate
            ));

            System.out.print("Add another product? (Y/N): ");
            more = sc.nextLine().trim();

        } while (more.equalsIgnoreCase("Y"));

        Purchase purchase = new Purchase(
            supplierId,
            invoiceNumber,
            LocalDate.now(),
            items
        );

        System.out.println("\n========== PURCHASE SUMMARY ==========");
        System.out.println("Supplier ID: " + supplierId);
        System.out.println("Invoice Number: " + invoiceNumber);
        System.out.println("Purchase Date: " + purchase.getPurchaseDate());

        BigDecimal total = BigDecimal.ZERO;

        for (PurchaseItem item : items) {

            System.out.println("--------------------------------");
            System.out.println("Product ID: " + item.getProductId());
            System.out.println("Quantity: " + item.getQuantity());
            System.out.println("Unit Cost: " + item.getUnitCost());
            System.out.println("Subtotal: " + item.getSubtotal());

            total = total.add(item.getSubtotal());
        }

        System.out.println("--------------------------------");
        System.out.println("Total Purchase Amount: " + total);

        System.out.print("\nConfirm Purchase? (Y/N): ");
        String confirmation = sc.nextLine().trim();

        if (confirmation.equalsIgnoreCase("Y")) {

            if (service.addPurchase(purchase)) {
                System.out.println("\nPurchase added successfully!");
                System.out.println("Purchase ID: " + purchase.getPurchaseId());
                System.out.println("Total Amount: " + purchase.getTotalAmount());
                System.out.println("Product stock updated successfully!");
            }

        } else {
            System.out.println("Purchase cancelled.");
        }
    }
}
