
package Analytics;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnalyticsService {

    // Calculate total sales revenue
    public BigDecimal getTotalRevenue(Connection con) throws SQLException {

        String sql = "SELECT COALESCE(SUM(subtotal), 0) AS revenue " +
                "FROM sale_items";

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            rs.next();
            return rs.getBigDecimal("revenue");
        }
    }

    // Calculate gross profit from all sales
    public BigDecimal getGrossProfit(Connection con) throws SQLException {

        String sql = "SELECT COALESCE(SUM(quantity * " +
                "(selling_price - cost_price)), 0) AS profit " +
                "FROM sale_items";

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            rs.next();
            return rs.getBigDecimal("profit");
        }
    }

    // Get revenue generated on each date
    public List<Map<String, Object>> getSalesTrend(Connection con)
            throws SQLException {

        List<Map<String, Object>> trends = new ArrayList<>();

        String sql = "SELECT s.sale_date, " +
                "SUM(si.subtotal) AS daily_revenue " +
                "FROM sales s " +
                "JOIN sale_items si ON s.sale_id = si.sale_id " +
                "GROUP BY s.sale_date " +
                "ORDER BY s.sale_date";

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Map<String, Object> record = new HashMap<>();

                record.put("saleDate", rs.getDate("sale_date"));
                record.put("revenue", rs.getBigDecimal("daily_revenue"));

                trends.add(record);
            }
        }

        return trends;
    }

    // Find the products with the highest sales quantities
    public List<Map<String, Object>> getFastSellingProducts(
            Connection con, int limit) throws SQLException {

        return getSellingProducts(con, limit, "DESC");
    }

    // Find the products with the lowest sales quantities
    public List<Map<String, Object>> getSlowSellingProducts(
            Connection con, int limit) throws SQLException {

        return getSellingProducts(con, limit, "ASC");
    }

    // Common method used by fast-selling and slow-selling queries
    private List<Map<String, Object>> getSellingProducts(
            Connection con, int limit, String order) throws SQLException {

        if (limit <= 0) {
            throw new IllegalArgumentException("Limit must be greater than zero.");
        }

        if (!order.equals("ASC") && !order.equals("DESC")) {
            throw new IllegalArgumentException("Invalid sorting order.");
        }

        List<Map<String, Object>> products = new ArrayList<>();

        String sql = "SELECT p.product_id, p.product_name, " +
                "SUM(si.quantity) AS total_sold " +
                "FROM products p " +
                "JOIN sale_items si ON p.product_id = si.product_id " +
                "GROUP BY p.product_id, p.product_name " +
                "ORDER BY total_sold " + order +
                " LIMIT ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, limit);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    Map<String, Object> product = new HashMap<>();

                    product.put("productId", rs.getInt("product_id"));
                    product.put("productName", rs.getString("product_name"));
                    product.put("totalSold", rs.getInt("total_sold"));

                    products.add(product);
                }
            }
        }

        return products;
    }
}
