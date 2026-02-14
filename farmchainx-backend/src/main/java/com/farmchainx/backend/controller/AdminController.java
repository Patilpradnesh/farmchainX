package com.farmchainx.backend.controller;

import com.farmchainx.backend.common.dto.ApiResponse;
import com.farmchainx.backend.dto.UserDto;
import com.farmchainx.backend.entity.User;
import com.farmchainx.backend.enums.Role;
import com.farmchainx.backend.enums.Status;
import com.farmchainx.backend.service.AdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<UserDto>>> pendingUsers() {
        List<UserDto> list = adminService.getPendingUsers();
        return ResponseEntity.ok(ApiResponse.success("Pending users fetched", list));
    }

    @PostMapping("/approve/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> approveUser(@PathVariable Long id,
                                         @RequestParam(required = false) Status status,
                                         @RequestBody(required = false) Map<String, String> body) {
        try {
            // Priority: explicit query param > request body
            if (status == null && body != null && body.containsKey("status")) {
                try {
                    status = Status.valueOf(body.get("status"));
                } catch (IllegalArgumentException e) {
                    logger.warn("Invalid status value in body for approve/{}, value={}", id, body.get("status"));
                    return ResponseEntity.badRequest().body(ApiResponse.error("Invalid status value: " + body.get("status") + ". Allowed: PENDING, APPROVED, REJECTED, SUSPENDED"));
                }
            }

            // If still null, default to APPROVED (convenience) — log this clearly
            if (status == null) {
                status = Status.APPROVED;
                logger.warn("/api/admin/approve/{} called without status parameter -> defaulting to {}", id, status);
            } else {
                logger.info("/api/admin/approve/{} called with status={}", id, status);
            }

            adminService.updateUserStatus(id, status);

            User user = adminService.getUserById(id);
            Map<String, Object> resp = Map.of(
                    "userId", id,
                    "email", user.getEmail(),
                    "newStatus", status.name()
            );
            return ResponseEntity.ok(ApiResponse.success("User status updated", resp));
        } catch (Exception e) {
            logger.error("Error while approving user {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(500).body(ApiResponse.error("Internal server error: " + e.getMessage()));
        }
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserDto>>> allUsers() {
        List<UserDto> list = adminService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success("All users fetched", list));
    }

    @PutMapping("/users/{userId}/status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateStatus(@PathVariable Long userId,
                                          @RequestBody java.util.Map<String, String> body) {
        String statusStr = body.get("status");
        if (statusStr == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Missing 'status' field"));
        }
        Status status;
        try {
            status = Status.valueOf(statusStr);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Invalid status value: " + statusStr));
        }
        adminService.updateUserStatus(userId, status);

        User user = adminService.getUserById(userId);
        Map<String, Object> resp = Map.of(
                "userId", userId,
                "email", user.getEmail(),
                "newStatus", status.name()
        );
        return ResponseEntity.ok(ApiResponse.success("User status updated", resp));
    }

    @GetMapping("/users/by-role")
    public ResponseEntity<ApiResponse<List<UserDto>>> usersByRole(@RequestParam Role role) {
        List<UserDto> list = adminService.getUsersByRole(role);
        return ResponseEntity.ok(ApiResponse.success("Users by role fetched", list));
    }
}
