
package NotificationManagement;

import java.sql.Timestamp;

public class Notification {

    private int notificationId;
    private Integer productId;
    private String notificationType;
    private String message;
    private boolean isRead;
    private Timestamp createdAt;

    public Notification(int notificationId, Integer productId,
                        String notificationType, String message,
                        boolean isRead, Timestamp createdAt) {

        this.notificationId = notificationId;
        this.productId = productId;
        this.notificationType = notificationType;
        this.message = message;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public Integer getProductId() {
        return productId;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public String getMessage() {
        return message;
    }

    public boolean isRead() {
        return isRead;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }
}
