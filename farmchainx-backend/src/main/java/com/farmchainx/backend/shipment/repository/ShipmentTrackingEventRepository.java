package com.farmchainx.backend.shipment.repository;

import com.farmchainx.backend.shipment.entity.ShipmentTrackingEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShipmentTrackingEventRepository extends JpaRepository<ShipmentTrackingEvent, Long> {

    List<ShipmentTrackingEvent> findByShipmentIdOrderByTimestampDesc(Long shipmentId);

    List<ShipmentTrackingEvent> findByShipmentIdAndEventType(Long shipmentId, String eventType);
}
