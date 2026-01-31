package com.farmchainx.backend.repository;

import com.farmchainx.backend.entity.CropHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CropHistoryRepository extends JpaRepository<CropHistory, Long> {
    List<CropHistory> findByCropIdOrderByTimestampAsc(Long cropId);
}
