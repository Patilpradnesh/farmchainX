package com.farmchainx.backend.repository;

import com.farmchainx.backend.entity.Crop;
import com.farmchainx.backend.enums.CropState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CropRepository extends JpaRepository<Crop, Long> {
    @Query("SELECT c FROM Crop c WHERE c.currentOwner.email = :email")
    List<Crop> findByCurrentOwnerEmail(@Param("email") String email);

    Optional<Crop> findByBlockchainHash(String blockchainHash);
    long countByCropState(CropState cropState);
    List<Crop> findByCropState(CropState cropState);

    // New: aggregate by region and crop name with counts and total quantity
    @Query("SELECT c.location AS region, c.cropName AS cropName, COUNT(c) AS cnt, SUM(c.quantity) AS totalQty " +
           "FROM Crop c WHERE c.createdAt BETWEEN :start AND :end GROUP BY c.location, c.cropName")
    List<Object[]> findCropStatsByRegionAndName(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
