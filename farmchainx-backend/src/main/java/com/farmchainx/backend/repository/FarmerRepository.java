package com.farmchainx.backend.repository;

import com.farmchainx.backend.entity.Farmer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FarmerRepository extends JpaRepository<Farmer, Long> {
}
