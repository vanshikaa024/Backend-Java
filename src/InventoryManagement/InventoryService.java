package InventoryManagement;

import NotificationManagement.NotificationDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides inventory alert queries and generates stock/expiry/damage notifications.
 */
public class InventoryService {

    public List<Map<String, Object>> getLowStockProducts(Connection con) throws SQLException {
        return getProducts(con, "SELECT * FROM products WHERE stock <= min_stock ORDER BY stock ASC");
    }

    public List<Map<String, Object>> getExpiredProducts(Connection con) throws SQLException {
        return getProducts(con, "SELECT * FROM products WHERE expiry_date < CURDATE() ORDER BY expiry_date ASC");
    }

    public List<Map<String, Object>> getNearExpiryProducts(Connection con, int warningDays) throws SQLException {
        if (warningDays < 0) {
            throw new IllegalArgumentException("Warning days cannot be negative.");
        }
        String sql = "SELECT * FROM products WHERE expiry_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL ? DAY) ORDER BY expiry_date ASC";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, warningDays);
            try (ResultSet rs = ps.executeQuery()) {
                return convertToList(rs);
            }
        }
    }

    public List<Map<String, Object>> getDamagedProducts(Connection con) throws SQLException {
        return getProducts(con, "SELECT * FROM products WHERE UPPER(product_condition) = 'DAMAGED' ORDER BY product_id");
    }

    public void generateNotifications(Connection con, int warningDays) throws SQLException {
        NotificationDAO notificationDAO = new NotificationDAO();

        for (Map<String, Object> product : getLowStockProducts(con)) {
            notificationDAO.createIfUnreadNotExists(
                    con,
                    (Integer) product.get("product_id"),
                    "LOW_STOCK",
                    product.get("product_name") + ": stock (" + product.get("stock") + ") is at or below minimum (" + product.get("min_stock") + ")."
            );
        }

        for (Map<String, Object> product : getExpiredProducts(con)) {
            notificationDAO.createIfUnreadNotExists(
                    con,
                    (Integer) product.get("product_id"),
                    "EXPIRED",
                    product.get("product_name") + " expired on " + product.get("expiry_date") + "."
            );
        }

        for (Map<String, Object> product : getNearExpiryProducts(con, warningDays)) {
            notificationDAO.createIfUnreadNotExists(
                    con,
                    (Integer) product.get("product_id"),
                    "EXPIRY",
                    product.get("product_name") + " expires on " + product.get("expiry_date") + "."
            );
        }

        for (Map<String, Object> product : getDamagedProducts(con)) {
            notificationDAO.createIfUnreadNotExists(
                    con,
                    (Integer) product.get("product_id"),
                    "DAMAGED",
                    product.get("product_name") + " is marked as damaged."
            );
        }
    }

    private List<Map<String, Object>> getProducts(Connection con, String sql) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return convertToList(rs);
        }
    }

    private List<Map<String, Object>> convertToList(ResultSet rs) throws SQLException {
        List<Map<String, Object>> products = new ArrayList<>();
        while (rs.next()) {
            Map<String, Object> product = new HashMap<>();
            product.put("product_id", rs.getInt("product_id"));
            product.put("product_name", rs.getString("product_name"));
            product.put("stock", rs.getInt("stock"));
            product.put("min_stock", rs.getInt("min_stock"));
            product.put("expiry_date", rs.getDate("expiry_date"));
            product.put("product_condition", rs.getString("product_condition"));
            products.add(product);
        }
        return products;
    }
}
