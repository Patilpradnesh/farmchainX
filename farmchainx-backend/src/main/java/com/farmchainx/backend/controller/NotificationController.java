package com.farmchainx.backend.controller;

import com.farmchainx.backend.common.dto.ApiResponse;
import com.farmchainx.backend.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<NotificationService.Notification>>> getMyNotifications() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            return ResponseEntity.ok(ApiResponse.success("My notifications retrieved", notificationService.getUserNotifications(email)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/unread")
    public ResponseEntity<ApiResponse<List<NotificationService.Notification>>> getUnreadNotifications() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            return ResponseEntity.ok(ApiResponse.success("Unread notifications retrieved", notificationService.getUnreadNotifications(email)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<String>> markAsRead(@PathVariable String notificationId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            notificationService.markAsRead(email, notificationId);
            return ResponseEntity.ok(ApiResponse.success("Notification marked as read", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/mark-all-read")
    public ResponseEntity<ApiResponse<String>> markAllAsRead() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            notificationService.markAllAsRead(email);
            return ResponseEntity.ok(ApiResponse.success("All notifications marked as read", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/count/unread")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> getUnreadCount() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            int count = notificationService.getUnreadNotifications(email).size();
            return ResponseEntity.ok(ApiResponse.success("Unread count retrieved", Map.of("count", count)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
