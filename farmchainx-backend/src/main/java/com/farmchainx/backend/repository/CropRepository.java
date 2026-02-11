package com.farmchainx.backend.repository;

import com.farmchainx.backend.entity.Crop;
import com.farmchainx.backend.enums.CropState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CropRepository extends JpaRepository<Crop, Long> {
    @Query("SELECT c FROM Crop c WHERE c.currentOwner.email = :email")
    List<Crop> findByCurrentOwnerEmail(@Param("email") String email);

    Optional<Crop> findByBlockchainHash(String blockchainHash);
    long countByCropState(CropState cropState);
    List<Crop> findByCropState(CropState cropState);
}
