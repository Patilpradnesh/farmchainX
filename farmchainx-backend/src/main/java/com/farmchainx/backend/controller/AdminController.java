package com.farmchainx.backend.controller;

import com.farmchainx.backend.dto.UserDto;
import com.farmchainx.backend.enums.Role;
import com.farmchainx.backend.enums.Status;
import com.farmchainx.backend.service.AdminService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority('ADMIN')")
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

    @GetMapping("/users/by-role")
    public List<UserDto> usersByRole(@RequestParam Role role) {
        return adminService.getUsersByRole(role);
    }
}
