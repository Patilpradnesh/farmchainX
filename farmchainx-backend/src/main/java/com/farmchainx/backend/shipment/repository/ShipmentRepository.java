package com.farmchainx.backend.shipment.repository;

import com.farmchainx.backend.shipment.entity.Shipment;
import com.farmchainx.backend.shipment.enums.ShipmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {

    Optional<Shipment> findByTrackingNumber(String trackingNumber);

    Optional<Shipment> findByOrderId(Long orderId);

    List<Shipment> findByStatus(ShipmentStatus status);

    List<Shipment> findByCarrierId(Long carrierId);

    List<Shipment> findByStatusIn(List<ShipmentStatus> statuses);
}
