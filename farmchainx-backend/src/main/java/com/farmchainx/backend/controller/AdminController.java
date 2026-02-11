package com.farmchainx.backend.controller;

import com.farmchainx.backend.dto.UserDto;
import com.farmchainx.backend.entity.User;
import com.farmchainx.backend.enums.Role;
import com.farmchainx.backend.enums.Status;
import com.farmchainx.backend.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/pending")
    public List<UserDto> pendingUsers() {
        return adminService.getPendingUsers();
    }

    @PostMapping("/approve/{id}")
    public void approveUser(@PathVariable Long id,
                            @RequestParam Status status) {
        adminService.updateUserStatus(id, status);
    }
    @GetMapping("/users")
    public List<UserDto> allUsers() {
        return adminService.getAllUsers();
    }

    @PutMapping("/users/{userId}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long userId,
                                          @RequestBody java.util.Map<String, String> body) {
        String statusStr = body.get("status");
        if (statusStr == null) {
            return ResponseEntity.badRequest().body("Missing 'status' field");
        }
        Status status = Status.valueOf(statusStr);
        adminService.updateUserStatus(userId, status);

        User user = adminService.getUserById(userId);
        return ResponseEntity.ok(java.util.Map.of(
                "message", "User status updated",
                "userId", userId,
                "email", user.getEmail(),
                "newStatus", status.name()
        ));
    }

    @GetMapping("/users/by-role")
    public List<UserDto> usersByRole(@RequestParam Role role) {
        return adminService.getUsersByRole(role);
    }
}
