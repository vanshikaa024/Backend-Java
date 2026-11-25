
package PurchaseManagement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.math.BigDecimal;

import Database.DBConnection;

public class PurchaseDAO {

    public boolean addPurchase(Purchase purchase) throws SQLException {

        String purchaseSQL =
            "INSERT INTO purchases (supplier_id, invoice_number, purchase_date, total_amount) VALUES (?, ?, ?, ?)";

        String itemSQL =
            "INSERT INTO purchase_items (purchase_id, product_id, quantity, unit_cost, batch_number, expiry_date) VALUES (?, ?, ?, ?, ?, ?)";

        String stockSQL =
            "UPDATE products SET stock = stock + ? WHERE product_id = ?";

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            try {
                BigDecimal total = BigDecimal.ZERO;

                for (PurchaseItem item : purchase.getItems()) {
                    total = total.add(item.getSubtotal());
                }

                purchase.setTotalAmount(total);

                int purchaseId;

                try (PreparedStatement ps = con.prepareStatement(
                        purchaseSQL, Statement.RETURN_GENERATED_KEYS)) {

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
                     PreparedStatement stockPS = con.prepareStatement(stockSQL)) {

                    for (PurchaseItem item : purchase.getItems()) {

                        itemPS.setInt(1, purchaseId);
                        itemPS.setInt(2, item.getProductId());
                        itemPS.setInt(3, item.getQuantity());
                        itemPS.setBigDecimal(4, item.getUnitCost());
                        itemPS.setString(5, item.getBatchNumber());

                        if (item.getExpiryDate() == null) {
                            itemPS.setNull(6, java.sql.Types.DATE);
                        } else {
                            itemPS.setDate(6, java.sql.Date.valueOf(item.getExpiryDate()));
                        }

                        itemPS.executeUpdate();

                        stockPS.setInt(1, item.getQuantity());
                        stockPS.setInt(2, item.getProductId());

                        if (stockPS.executeUpdate() == 0) {
                            throw new SQLException(
                                "Product not found: " + item.getProductId()
                            );
                        }
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
}

