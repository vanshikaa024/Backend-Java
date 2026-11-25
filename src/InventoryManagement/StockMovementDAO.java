package InventoryManagement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access object for stock_history audit logs.
 */
public class StockMovementDAO {

    public void save(Connection con, StockMovement movement) throws SQLException {
        String sql = "INSERT INTO stock_history "
                + "(product_id, transaction_type, quantity, previous_stock, updated_stock, purchase_id, sale_id, remarks) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, movement.getProductId());
            ps.setString(2, movement.getTransactionType());
            ps.setInt(3, movement.getQuantity());
            ps.setInt(4, movement.getPreviousStock());
            ps.setInt(5, movement.getUpdatedStock());

            if (movement.getPurchaseId() == null) {
                ps.setNull(6, Types.INTEGER);
            } else {
                ps.setInt(6, movement.getPurchaseId());
            }

            if (movement.getSaleId() == null) {
                ps.setNull(7, Types.INTEGER);
            } else {
                ps.setInt(7, movement.getSaleId());
            }

            ps.setString(8, movement.getRemarks());
            ps.executeUpdate();
        }
    }

    public List<StockMovement> findByProductId(Connection con, int productId) throws SQLException {
        List<StockMovement> movements = new ArrayList<>();
        String sql = "SELECT * FROM stock_history WHERE product_id = ? ORDER BY created_at DESC, history_id DESC";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    StockMovement movement = new StockMovement(
                            rs.getInt("product_id"),
                            rs.getString("transaction_type"),
                            rs.getInt("quantity"),
                            rs.getInt("previous_stock"),
                            rs.getInt("updated_stock"),
                            (Integer) rs.getObject("purchase_id"),
                            (Integer) rs.getObject("sale_id"),
                            rs.getString("remarks")
                    );
                    movement.setCreatedAt(rs.getTimestamp("created_at"));
                    movements.add(movement);
                }
            }
        }
        return movements;
    }
}
