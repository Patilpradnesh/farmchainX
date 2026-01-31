package com.farmchainx.backend.repository;

import com.farmchainx.backend.entity.DistributorProfile;
import com.farmchainx.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DistributorProfileRepository
        extends JpaRepository<DistributorProfile, Long> {

    Optional<DistributorProfile> findByUser(User user);
}
