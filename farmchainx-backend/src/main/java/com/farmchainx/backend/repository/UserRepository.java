package com.farmchainx.backend.repository;

import com.farmchainx.backend.entity.Status;
import com.farmchainx.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    // ✅ REQUIRED for AdminController
    List<User> findByStatus(Status status);

    // ✅ REQUIRED for role-based admin listing
    List<User> findByRoleAndStatus(String role, Status status);
}
