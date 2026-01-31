package com.farmchainx.backend.repository;

import com.farmchainx.backend.entity.Crop;
import com.farmchainx.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CropRepository extends JpaRepository<Crop, Long> {

    List<Crop> findByFarmer(User farmer);
    List<Crop> findByFarmerEmail(String email);

    Optional<Crop> findByBlockchainHash(String blockchainHash);
}
