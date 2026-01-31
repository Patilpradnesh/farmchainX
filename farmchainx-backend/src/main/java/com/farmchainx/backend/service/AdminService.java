package com.farmchainx.backend.service;

import com.farmchainx.backend.dto.UserDto;
import com.farmchainx.backend.entity.User;
import com.farmchainx.backend.enums.Role;
import com.farmchainx.backend.enums.Status;
import com.farmchainx.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    private final UserRepository userRepository;

    public AdminService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDto> getPendingUsers() {
             return userRepository.findByStatus(Status.PENDING)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public void updateUserStatus(Long userId, Status status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setStatus(status);
        userRepository.save(user);
    }

    public List<UserDto> getUsersByRole(Role role) {
        return userRepository.findByRole(role)
                .stream()
                .map(this::toDto)
                .toList();
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
