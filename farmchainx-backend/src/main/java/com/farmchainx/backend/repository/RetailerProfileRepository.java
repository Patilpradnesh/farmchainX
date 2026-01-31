package com.farmchainx.backend.repository;

import com.farmchainx.backend.entity.RetailerProfile;
import com.farmchainx.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RetailerProfileRepository
        extends JpaRepository<RetailerProfile, Long> {

    Optional<RetailerProfile> findByUser(User user);
}
