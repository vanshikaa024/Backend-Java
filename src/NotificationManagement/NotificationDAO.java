
package NotificationManagement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {

    // Create a notification only if an unread notification does not already exist
    public void createIfUnreadNotExists(
            Connection con, Integer productId,
            String notificationType, String message) throws SQLException {

        String sql = "INSERT INTO notifications " +
                "(product_id, notification_type, message) " +
                "SELECT ?, ?, ? WHERE NOT EXISTS (" +
                "SELECT 1 FROM notifications " +
                "WHERE product_id <=> ? " +
                "AND notification_type = ? AND is_read = FALSE)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            if (productId == null) {
                ps.setNull(1, java.sql.Types.INTEGER);
                ps.setNull(4, java.sql.Types.INTEGER);
            } else {
                ps.setInt(1, productId);
                ps.setInt(4, productId);
            }

            ps.setString(2, notificationType);
            ps.setString(3, message);
            ps.setString(5, notificationType);

            ps.executeUpdate();
        }
    }

    // Retrieve all unread notifications
    public List<Notification> findUnread(Connection con) throws SQLException {

        List<Notification> notifications = new ArrayList<>();

        String sql = "SELECT * FROM notifications " +
                "WHERE is_read = FALSE ORDER BY created_at DESC";

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Notification notification = new Notification(
                        rs.getInt("notification_id"),
                        (Integer) rs.getObject("product_id"),
                        rs.getString("notification_type"),
                        rs.getString("message"),
                        rs.getBoolean("is_read"),
                        rs.getTimestamp("created_at")
                );

                notifications.add(notification);
            }
        }

        return notifications;
    }

    // Mark a notification as read
    public void markAsRead(Connection con, int notificationId)
            throws SQLException {

        String sql = "UPDATE notifications SET is_read = TRUE " +
                "WHERE notification_id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, notificationId);
            ps.executeUpdate();
        }
    }
}
