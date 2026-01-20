package com.farmchainx.backend.controller;

import com.farmchainx.backend.entity.Status;
import com.farmchainx.backend.entity.User;
import com.farmchainx.backend.repository.UserRepository;
import com.farmchainx.backend.service.FarmerService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final FarmerService farmerService;
    private final UserRepository userRepository;

    public AdminController(FarmerService farmerService,
                           UserRepository userRepository) {
        this.farmerService = farmerService;
        this.userRepository = userRepository;
    }

    /**
     * 🔒 INTERNAL CHECK:
     * Ensure logged-in user is an APPROVED ADMIN
     */
    private User validateAdmin() {

        String email = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        User admin = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Admin user not found"));

        // ✅ FIX: enum comparison (NOT string)
        if (admin.getRole() != com.farmchainx.backend.entity.Role.ADMIN) {
            throw new RuntimeException("Access denied: ADMIN role required");
        }

        if (admin.getStatus() != Status.APPROVED) {
            throw new RuntimeException("Admin is not approved");
        }

        return admin;
    }


    /**
     * ✅ Approve a user (Farmer)
     * ADMIN only
     */
    @PutMapping("/approve")
    public String approveUser(@RequestParam Long userId) {
        validateAdmin();
        farmerService.approveUser(userId);
        return "User approved successfully";
    }

    /**
     * ✅ Get all users with PENDING status
     */
    @GetMapping("/pending")
    public List<User> getPendingUsers() {
        validateAdmin();
        return userRepository.findByStatus(Status.PENDING);
    }

    /**
     * ✅ Get APPROVED users by role
     */
    @GetMapping("/users/by-role")
    public List<User> getUsersByRole(@RequestParam String role) {
        validateAdmin();
        return userRepository.findByRoleAndStatus(role.toUpperCase(), Status.APPROVED);
    }
}
