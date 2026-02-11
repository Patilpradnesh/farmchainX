package com.farmchainx.backend.repository;

import com.farmchainx.backend.entity.Dispute;
import com.farmchainx.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface DisputeRepository extends JpaRepository<Dispute, Long> {
    List<Dispute> findByStatus(String status);

    @Query("SELECT d FROM Dispute d WHERE d.crop.id = :cropId")
    List<Dispute> findByCropId(@Param("cropId") Long cropId);

    @Query("SELECT d FROM Dispute d WHERE d.order.id = :orderId")
    List<Dispute> findByOrderId(@Param("orderId") Long orderId);

    List<Dispute> findByRaisedBy(User user);
    long countByStatus(String status);
}
