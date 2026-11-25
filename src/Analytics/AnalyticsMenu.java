package Analytics;

import Database.DBConnection;
import UI.ConsoleUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Terminal menu for inventory dashboard KPIs and sales analytics.
 */
public class AnalyticsMenu {

    private static final DashboardService dashboardService = new DashboardService();
    private static final AnalyticsService analyticsService = new AnalyticsService();

    public static void run(Scanner sc) {
        int choice;
        do {
            System.out.println("\n========== ANALYTICS & DASHBOARD ==========");
            System.out.println("1. Inventory Dashboard Summary");
            System.out.println("2. Total Revenue & Gross Profit");
            System.out.println("3. Daily Sales Trend");
            System.out.println("4. Fast-Selling Products");
            System.out.println("5. Slow-Selling Products");
            System.out.println("6. Back");
            choice = ConsoleUtils.readInt(sc, "Enter your choice: ");

            try (Connection con = DBConnection.getConnection()) {
                switch (choice) {
                    case 1 -> showDashboard(con);
                    case 2 -> showRevenueAndProfit(con);
                    case 3 -> showSalesTrend(con);
                    case 4 -> showTopProducts(sc, con, true);
                    case 5 -> showTopProducts(sc, con, false);
                    case 6 -> System.out.println("Returning to main menu...");
                    default -> System.out.println("Invalid choice.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Validation Error: " + e.getMessage());
            } catch (SQLException e) {
                System.out.println("Analytics query failed: " + e.getMessage());
            }
        } while (choice != 6);
    }

    private static void showDashboard(Connection con) throws SQLException {
        Map<String, Object> stats = dashboardService.getDashboardStats(con);
        System.out.println("\n========== INVENTORY DASHBOARD ==========");
        System.out.println("Total Products:       " + stats.get("totalProducts"));
        System.out.println("Available Stock:      " + stats.get("availableStock"));
        System.out.println("Inventory Value:      " + stats.get("inventoryValue"));
        System.out.println("Invested Amount:      " + stats.get("investedAmount"));
        System.out.println("Low Stock Products:   " + stats.get("lowStockProducts"));
        System.out.println("Expired Products:     " + stats.get("expiredProducts"));
        System.out.println("Near-Expiry Products: " + stats.get("nearExpiryProducts"));
        System.out.println("Damaged Products:     " + stats.get("damagedProducts"));
    }

    private static void showRevenueAndProfit(Connection con) throws SQLException {
        System.out.println("\n========== FINANCIAL SUMMARY ==========");
        System.out.println("Total Revenue: " + analyticsService.getTotalRevenue(con));
        System.out.println("Gross Profit:  " + analyticsService.getGrossProfit(con));
    }

    private static void showSalesTrend(Connection con) throws SQLException {
        List<Map<String, Object>> trends = analyticsService.getSalesTrend(con);
        System.out.println("\n========== DAILY SALES TREND ==========");
        if (trends.isEmpty()) {
            System.out.println("No sales recorded yet.");
            return;
        }
        for (Map<String, Object> row : trends) {
            System.out.printf("Date: %s | Revenue: %s%n", row.get("saleDate"), row.get("revenue"));
        }
    }

    private static void showTopProducts(Scanner sc, Connection con, boolean fast) throws SQLException {
        int limit = ConsoleUtils.readPositiveInt(sc, "Enter number of products to show: ");
        List<Map<String, Object>> items = fast
                ? analyticsService.getFastSellingProducts(con, limit)
                : analyticsService.getSlowSellingProducts(con, limit);

        System.out.println(fast ? "\n========== FAST-SELLING PRODUCTS ==========" : "\n========== SLOW-SELLING PRODUCTS ==========");
        if (items.isEmpty()) {
            System.out.println("No sales data available.");
            return;
        }
        for (Map<String, Object> row : items) {
            System.out.printf("Product #%s | %s | Total Sold: %s%n",
                    row.get("productId"), row.get("productName"), row.get("totalSold"));
        }
    }
}
