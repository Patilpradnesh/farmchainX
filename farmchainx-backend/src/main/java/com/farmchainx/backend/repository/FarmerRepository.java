package com.farmchainx.backend.repository;

import com.farmchainx.backend.entity.Farmer;
import com.farmchainx.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FarmerRepository extends JpaRepository<Farmer, Long> {

    Optional<Farmer> findByUser(User user);

}
