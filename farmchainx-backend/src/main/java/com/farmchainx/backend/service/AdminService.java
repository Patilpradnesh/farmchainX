package com.farmchainx.backend.service;

import com.farmchainx.backend.dto.UserDto;
import com.farmchainx.backend.entity.User;
import com.farmchainx.backend.enums.Role;
import com.farmchainx.backend.enums.Status;
import com.farmchainx.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final PermissionService permissionService;
    private final AuditService auditService;

    public AdminService(UserRepository userRepository, PermissionService permissionService, AuditService auditService) {
        this.userRepository = userRepository;
        this.permissionService = permissionService;
        this.auditService = auditService;
    }

    public List<UserDto> getPendingUsers() {
        checkAdmin();
        return userRepository.findByStatus(Status.PENDING)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public void updateUserStatus(Long userId, Status status) {
        checkAdmin();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setStatus(status);
        userRepository.save(user);
        auditService.logAction("USER_STATUS_UPDATE", getCurrentUser(), user);
    }

    public List<UserDto> getUsersByRole(Role role) {
        checkAdmin();
        return userRepository.findByRole(role)
                .stream()
                .map(this::toDto)
                .toList();
    }

    private void checkAdmin() {
        User currentUser = getCurrentUser();
        if (currentUser.getRole() != Role.ADMIN) {
            throw new RuntimeException("Access denied: Admin role required");
        }
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Current user not found"));
    }

    private UserDto toDto(User user) {
        return new UserDto(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                user.getStatus()
        );
    }
}
