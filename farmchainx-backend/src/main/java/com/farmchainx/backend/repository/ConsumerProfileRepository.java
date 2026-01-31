package com.farmchainx.backend.repository;

import com.farmchainx.backend.entity.ConsumerProfile;
import com.farmchainx.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConsumerProfileRepository
        extends JpaRepository<ConsumerProfile, Long> {

    Optional<ConsumerProfile> findByUser(User user);
}
