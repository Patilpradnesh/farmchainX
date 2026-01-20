package com.farmchainx.backend.repository;

import com.farmchainx.backend.entity.Crop;
import com.farmchainx.backend.entity.Farmer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CropRepository extends JpaRepository<Crop, Long> {
    List<Crop> findByFarmer(Farmer farmer);
}
