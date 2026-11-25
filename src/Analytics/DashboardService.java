package Analytics;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Aggregates high-level inventory metrics for the terminal dashboard.
 */
public class DashboardService {

    public Map<String, Object> getDashboardStats(Connection con) throws SQLException {
        Map<String, Object> stats = new HashMap<>();

        String sql = "SELECT "
                + "COUNT(*) AS total_products, "
                + "COALESCE(SUM(stock), 0) AS available_stock, "
                + "COALESCE(SUM(stock * price), 0) AS inventory_value, "
                + "COALESCE(SUM(stock * cost_price), 0) AS invested_amount, "
                + "COALESCE(SUM(CASE WHEN stock <= min_stock THEN 1 ELSE 0 END), 0) AS low_stock, "
                + "COALESCE(SUM(CASE WHEN expiry_date < CURDATE() THEN 1 ELSE 0 END), 0) AS expired, "
                + "COALESCE(SUM(CASE WHEN expiry_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 30 DAY) THEN 1 ELSE 0 END), 0) AS near_expiry, "
                + "COALESCE(SUM(CASE WHEN UPPER(product_condition) = 'DAMAGED' THEN 1 ELSE 0 END), 0) AS damaged "
                + "FROM products";

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                stats.put("totalProducts", rs.getInt("total_products"));
                stats.put("availableStock", rs.getInt("available_stock"));
                stats.put("inventoryValue", rs.getBigDecimal("inventory_value"));
                stats.put("investedAmount", rs.getBigDecimal("invested_amount"));
                stats.put("lowStockProducts", rs.getInt("low_stock"));
                stats.put("expiredProducts", rs.getInt("expired"));
                stats.put("nearExpiryProducts", rs.getInt("near_expiry"));
                stats.put("damagedProducts", rs.getInt("damaged"));
            }
        }
        return stats;
    }
}
