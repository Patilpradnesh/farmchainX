package com.farmchainx.backend.service;

import com.farmchainx.backend.entity.User;
import com.farmchainx.backend.enums.Role;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    // In-memory storage for demo purposes. In production, use database
    private final Map<String, List<Notification>> userNotifications = new ConcurrentHashMap<>();

    public void sendOrderNotification(User user, String orderAction, String orderId, String cropName) {
        String message = String.format("Order %s for crop '%s' (ID: %s)", orderAction, cropName, orderId);
        createNotification(user.getEmail(), "ORDER_UPDATE", message, "info");
    }

    public void sendDisputeNotification(User user, String disputeAction, String disputeId) {
        String message = String.format("Dispute %s (ID: %s)", disputeAction, disputeId);
        createNotification(user.getEmail(), "DISPUTE_UPDATE", message, "warning");
    }

    public void sendCropNotification(User user, String cropAction, String cropId, String cropName) {
        String message = String.format("Crop '%s' %s (ID: %s)", cropName, cropAction, cropId);
        createNotification(user.getEmail(), "CROP_UPDATE", message, "success");
    }

    public void sendStatusNotification(User user, String newStatus) {
        String message = String.format("Your account status has been updated to: %s", newStatus);
        createNotification(user.getEmail(), "STATUS_UPDATE", message, "info");
    }

    public void sendSystemNotification(String message, Role targetRole) {
        // This would send to all users with specific role
        logger.info("System notification to {} users: {}", targetRole, message);
        // Implementation would query users by role and send notifications
    }

    public List<Notification> getUserNotifications(String userEmail) {
        return userNotifications.getOrDefault(userEmail, new ArrayList<>());
    }

    public List<Notification> getUnreadNotifications(String userEmail) {
        return getUserNotifications(userEmail).stream()
                .filter(n -> !n.isRead())
                .toList();
    }

    public void markAsRead(String userEmail, String notificationId) {
        List<Notification> notifications = userNotifications.get(userEmail);
        if (notifications != null) {
            notifications.stream()
                    .filter(n -> n.getId().equals(notificationId))
                    .findFirst()
                    .ifPresent(n -> n.setRead(true));
        }
    }

    public void markAllAsRead(String userEmail) {
        List<Notification> notifications = userNotifications.get(userEmail);
        if (notifications != null) {
            notifications.forEach(n -> n.setRead(true));
        }
    }

    private void createNotification(String userEmail, String type, String message, String priority) {
        Notification notification = new Notification(
                java.util.UUID.randomUUID().toString(),
                type,
                message,
                priority,
                LocalDateTime.now(),
                false
        );

        userNotifications.computeIfAbsent(userEmail, k -> new ArrayList<>()).add(notification);
        logger.info("Notification created for {}: {}", userEmail, message);
    }

    public static class Notification {
        private String id;
        private String type;
        private String message;
        private String priority;
        private LocalDateTime createdAt;
        private boolean read;

        public Notification(String id, String type, String message, String priority, LocalDateTime createdAt, boolean read) {
            this.id = id;
            this.type = type;
            this.message = message;
            this.priority = priority;
            this.createdAt = createdAt;
            this.read = read;
        }

        // Getters and setters
        public String getId() { return id; }
        public String getType() { return type; }
        public String getMessage() { return message; }
        public String getPriority() { return priority; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public boolean isRead() { return read; }
        public void setRead(boolean read) { this.read = read; }
    }
}
