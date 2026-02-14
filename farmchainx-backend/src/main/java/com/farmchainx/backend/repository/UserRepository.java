package com.farmchainx.backend.repository;

import com.farmchainx.backend.entity.User;
import com.farmchainx.backend.enums.Role;
import com.farmchainx.backend.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByStatus(Status status);
    List<User> findByRole(Role role);
    List<User> findByRoleAndStatus(Role role, Status status);
    long countByRole(Role role);
    long countByStatus(Status status);
    long countByRoleAndStatus(Role role, Status status);
    long countByRoleAndCreatedAtBetween(Role role, LocalDateTime start, LocalDateTime end);
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}