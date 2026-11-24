
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

public class InventoryService {

    // Find products whose stock is at or below their minimum stock level
    public List<Map<String, Object>> getLowStockProducts(Connection con)
            throws SQLException {

        String sql = "SELECT * FROM products WHERE stock <= min_stock";

        return getProducts(con, sql);
    }

    // Find products that have already expired
    public List<Map<String, Object>> getExpiredProducts(Connection con)
            throws SQLException {

        String sql = "SELECT * FROM products WHERE expiry_date < CURDATE()";

        return getProducts(con, sql);
    }

    // Find products expiring within the given number of days
    public List<Map<String, Object>> getNearExpiryProducts(
            Connection con, int warningDays) throws SQLException {

        if (warningDays < 0) {
            throw new IllegalArgumentException("Warning days cannot be negative.");
        }

        String sql = "SELECT * FROM products " +
                "WHERE expiry_date BETWEEN CURDATE() " +
                "AND DATE_ADD(CURDATE(), INTERVAL ? DAY)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, warningDays);

            try (ResultSet rs = ps.executeQuery()) {
                return convertToList(rs);
            }
        }
    }

    // Find products marked as damaged
    public List<Map<String, Object>> getDamagedProducts(Connection con)
            throws SQLException {

        String sql = "SELECT * FROM products " +
                "WHERE UPPER(product_condition) = 'DAMAGED'";

        return getProducts(con, sql);
    }

    // Generate notifications for inventory-related problems
    public void generateNotifications(Connection con, int warningDays)
            throws SQLException {

        NotificationDAO notificationDAO = new NotificationDAO();

        for (Map<String, Object> product : getLowStockProducts(con)) {
            notificationDAO.createIfUnreadNotExists(
                    con,
                    (Integer) product.get("product_id"),
                    "LOW_STOCK",
                    "Stock is below or equal to the minimum level."
            );
        }

        for (Map<String, Object> product : getExpiredProducts(con)) {
            notificationDAO.createIfUnreadNotExists(
                    con,
                    (Integer) product.get("product_id"),
                    "EXPIRED",
                    "This product has expired."
            );
        }

        for (Map<String, Object> product : getNearExpiryProducts(con, warningDays)) {
            notificationDAO.createIfUnreadNotExists(
                    con,
                    (Integer) product.get("product_id"),
                    "EXPIRY",
                    "This product is approaching its expiry date."
            );
        }

        for (Map<String, Object> product : getDamagedProducts(con)) {
            notificationDAO.createIfUnreadNotExists(
                    con,
                    (Integer) product.get("product_id"),
                    "DAMAGED",
                    "This product is marked as damaged."
            );
        }
    }

    // Execute a product query and return its results
    private List<Map<String, Object>> getProducts(Connection con, String sql)
            throws SQLException {

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            return convertToList(rs);
        }
    }

    // Convert database rows into a list of Java maps
    private List<Map<String, Object>> convertToList(ResultSet rs)
            throws SQLException {

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
