package PurchaseManagement;

import Database.DBConnection;
import InventoryManagement.StockMovement;
import InventoryManagement.StockMovementDAO;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data access object for purchases, purchase items, stock updates, and audit logs.
 */
public class PurchaseDAO {

    public boolean addPurchase(Purchase purchase) throws SQLException {
        String purchaseSQL =
                "INSERT INTO purchases (supplier_id, invoice_number, purchase_date, total_amount) VALUES (?, ?, ?, ?)";
        String itemSQL =
                "INSERT INTO purchase_items (purchase_id, product_id, quantity, unit_cost, batch_number, expiry_date) VALUES (?, ?, ?, ?, ?, ?)";
        String lockProductSQL =
                "SELECT stock FROM products WHERE product_id = ? FOR UPDATE";
        String stockSQL =
                "UPDATE products SET stock = ? WHERE product_id = ?";

        StockMovementDAO movementDAO = new StockMovementDAO();

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                BigDecimal total = BigDecimal.ZERO;
                for (PurchaseItem item : purchase.getItems()) {
                    total = total.add(item.getSubtotal());
                }
                purchase.setTotalAmount(total);

                int purchaseId;
                try (PreparedStatement ps = con.prepareStatement(purchaseSQL, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, purchase.getSupplierId());
                    ps.setString(2, purchase.getInvoiceNumber());
                    ps.setDate(3, java.sql.Date.valueOf(purchase.getPurchaseDate()));
                    ps.setBigDecimal(4, total);
                    ps.executeUpdate();

                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (!rs.next()) {
                            throw new SQLException("Purchase ID generation failed.");
                        }
                        purchaseId = rs.getInt(1);
                        purchase.setPurchaseId(purchaseId);
                    }
                }

                try (PreparedStatement itemPS = con.prepareStatement(itemSQL);
                     PreparedStatement lockPS = con.prepareStatement(lockProductSQL);
                     PreparedStatement stockPS = con.prepareStatement(stockSQL)) {

                    for (PurchaseItem item : purchase.getItems()) {
                        lockPS.setInt(1, item.getProductId());
                        int previousStock;
                        try (ResultSet prs = lockPS.executeQuery()) {
                            if (!prs.next()) {
                                throw new SQLException("Product not found: " + item.getProductId());
                            }
                            previousStock = prs.getInt("stock");
                        }

                        int updatedStock = previousStock + item.getQuantity();

                        itemPS.setInt(1, purchaseId);
                        itemPS.setInt(2, item.getProductId());
                        itemPS.setInt(3, item.getQuantity());
                        itemPS.setBigDecimal(4, item.getUnitCost());
                        itemPS.setString(5, item.getBatchNumber());
                        if (item.getExpiryDate() == null) {
                            itemPS.setNull(6, Types.DATE);
                        } else {
                            itemPS.setDate(6, java.sql.Date.valueOf(item.getExpiryDate()));
                        }
                        itemPS.executeUpdate();

                        stockPS.setInt(1, updatedStock);
                        stockPS.setInt(2, item.getProductId());
                        stockPS.executeUpdate();

                        movementDAO.save(con, new StockMovement(
                                item.getProductId(),
                                "PURCHASE",
                                item.getQuantity(),
                                previousStock,
                                updatedStock,
                                purchaseId,
                                null,
                                "Purchase Invoice: " + purchase.getInvoiceNumber()
                        ));
                    }
                }

                con.commit();
                return true;
            } catch (SQLException | RuntimeException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    public List<Map<String, Object>> getRecentPurchases() throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT pu.purchase_id, pu.supplier_id, COALESCE(s.supplier_name, 'Unknown') AS supplier_name, "
                + "pu.invoice_number, pu.purchase_date, pu.total_amount "
                + "FROM purchases pu LEFT JOIN suppliers s ON pu.supplier_id = s.supplier_id "
                + "ORDER BY pu.purchase_id DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("purchaseId", rs.getInt("purchase_id"));
                row.put("supplierId", rs.getInt("supplier_id"));
                row.put("supplierName", rs.getString("supplier_name"));
                row.put("invoiceNumber", rs.getString("invoice_number"));
                row.put("purchaseDate", rs.getDate("purchase_date"));
                row.put("totalAmount", rs.getBigDecimal("total_amount"));
                list.add(row);
            }
        }
        return list;
    }

    public List<Map<String, Object>> getPurchaseItems(int purchaseId) throws SQLException {
        List<Map<String, Object>> items = new ArrayList<>();
        String sql = "SELECT pi.product_id, COALESCE(p.product_name, 'Unknown') AS product_name, "
                + "pi.quantity, pi.unit_cost, pi.batch_number, pi.expiry_date "
                + "FROM purchase_items pi LEFT JOIN products p ON pi.product_id = p.product_id "
                + "WHERE pi.purchase_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, purchaseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("productId", rs.getInt("product_id"));
                    row.put("productName", rs.getString("product_name"));
                    row.put("quantity", rs.getInt("quantity"));
                    row.put("unitCost", rs.getBigDecimal("unit_cost"));
                    row.put("batchNumber", rs.getString("batch_number"));
                    row.put("expiryDate", rs.getDate("expiry_date"));
                    items.add(row);
                }
            }
        }
        return items;
    }
}
