package InventoryManagement;

import Database.DBConnection;
import NotificationManagement.Notification;
import NotificationManagement.NotificationDAO;
import UI.ConsoleUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Terminal menu for inventory alerts, stock movement audit logs, and notifications.
 */
public class InventoryMenu {

    private static final InventoryService inventoryService = new InventoryService();
    private static final StockMovementDAO movementDAO = new StockMovementDAO();
    private static final NotificationDAO notificationDAO = new NotificationDAO();

    public static void run(Scanner sc) {
        int choice;
        do {
            System.out.println("\n========== INVENTORY & NOTIFICATIONS ==========");
            System.out.println("1. View Low Stock Products");
            System.out.println("2. View Expired Products");
            System.out.println("3. View Near-Expiry Products");
            System.out.println("4. View Damaged Products");
            System.out.println("5. View Stock History by Product ID");
            System.out.println("6. Generate & View Unread Notifications");
            System.out.println("7. Mark Notification as Read");
            System.out.println("8. Back");
            choice = ConsoleUtils.readInt(sc, "Enter your choice: ");

            try (Connection con = DBConnection.getConnection()) {
                switch (choice) {
                    case 1 -> printProductAlerts("LOW STOCK PRODUCTS", inventoryService.getLowStockProducts(con));
                    case 2 -> printProductAlerts("EXPIRED PRODUCTS", inventoryService.getExpiredProducts(con));
                    case 3 -> {
                        int days = ConsoleUtils.readNonNegativeInt(sc, "Enter warning window (days): ");
                        printProductAlerts("NEAR-EXPIRY PRODUCTS (" + days + " days)",
                                inventoryService.getNearExpiryProducts(con, days));
                    }
                    case 4 -> printProductAlerts("DAMAGED PRODUCTS", inventoryService.getDamagedProducts(con));
                    case 5 -> viewStockHistory(sc, con);
                    case 6 -> generateAndShowNotifications(sc, con);
                    case 7 -> markNotificationRead(sc, con);
                    case 8 -> System.out.println("Returning to main menu...");
                    default -> System.out.println("Invalid choice.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Validation Error: " + e.getMessage());
            } catch (SQLException e) {
                System.out.println("Inventory operation failed: " + e.getMessage());
            }
        } while (choice != 8);
    }

    private static void printProductAlerts(String title, List<Map<String, Object>> products) {
        System.out.println("\n========== " + title + " ==========");
        if (products.isEmpty()) {
            System.out.println("None.");
            return;
        }
        for (Map<String, Object> p : products) {
            System.out.printf("ID: %s | %s | Stock: %s (Min: %s) | Expiry: %s | Condition: %s%n",
                    p.get("product_id"), p.get("product_name"), p.get("stock"),
                    p.get("min_stock"), p.get("expiry_date"), p.get("product_condition"));
        }
    }

    private static void viewStockHistory(Scanner sc, Connection con) throws SQLException {
        int productId = ConsoleUtils.readPositiveInt(sc, "Enter Product ID: ");
        List<StockMovement> movements = movementDAO.findByProductId(con, productId);
        System.out.println("\n========== STOCK HISTORY FOR PRODUCT #" + productId + " ==========");
        if (movements.isEmpty()) {
            System.out.println("No stock movements recorded.");
            return;
        }
        for (StockMovement m : movements) {
            System.out.printf("%s | Type: %-10s | Qty: %d | %d -> %d | Purchase: %s | Sale: %s | %s%n",
                    m.getCreatedAt(), m.getTransactionType(), m.getQuantity(),
                    m.getPreviousStock(), m.getUpdatedStock(),
                    m.getPurchaseId(), m.getSaleId(), m.getRemarks());
        }
    }

    private static void generateAndShowNotifications(Scanner sc, Connection con) throws SQLException {
        int warningDays = ConsoleUtils.readNonNegativeInt(sc, "Enter expiry warning days (e.g. 30): ");
        inventoryService.generateNotifications(con, warningDays);
        List<Notification> unread = notificationDAO.findUnread(con);
        System.out.println("\n========== UNREAD NOTIFICATIONS ==========");
        if (unread.isEmpty()) {
            System.out.println("No unread notifications.");
            return;
        }
        for (Notification n : unread) {
            System.out.printf("Notification #%d | [%s] Product #%s | %s | %s%n",
                    n.getNotificationId(), n.getNotificationType(), n.getProductId(),
                    n.getMessage(), n.getCreatedAt());
        }
    }

    private static void markNotificationRead(Scanner sc, Connection con) throws SQLException {
        int id = ConsoleUtils.readPositiveInt(sc, "Enter Notification ID to mark as read: ");
        if (notificationDAO.markAsRead(con, id)) {
            System.out.println("Notification marked as read.");
        } else {
            System.out.println("Notification not found.");
        }
    }
}
