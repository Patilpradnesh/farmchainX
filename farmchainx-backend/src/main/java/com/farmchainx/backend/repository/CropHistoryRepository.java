package com.farmchainx.backend.repository;

import com.farmchainx.backend.entity.CropHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface CropHistoryRepository extends JpaRepository<CropHistory, Long> {
    List<CropHistory> findByCropIdOrderByTimestampAsc(Long cropId);

    @Query("SELECT COUNT(h) FROM CropHistory h WHERE h.action = 'STATE_CHANGE'")
    Long countStateChanges();

    @Query("SELECT COUNT(h) FROM CropHistory h WHERE h.action = 'OWNERSHIP_TRANSFER'")
    Long countOwnershipTransfers();
}
