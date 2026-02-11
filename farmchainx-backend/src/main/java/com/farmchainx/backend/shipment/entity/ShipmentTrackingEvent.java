package com.farmchainx.backend.shipment.entity;

import com.farmchainx.backend.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Shipment Tracking Event - Records every location/status update
 */
@Entity
@Table(name = "shipment_tracking_events")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentTrackingEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "shipment_id", nullable = false)
    private Shipment shipment;

    @Column(nullable = false)
    private String eventType; // LOCATION_UPDATE, STATUS_CHANGE, CONDITION_ALERT

    @Column(nullable = false)
    private String description;

    private Double latitude;
    private Double longitude;
    private String locationName;

    private Double temperature;
    private Double humidity;

    @ManyToOne
    @JoinColumn(name = "recorded_by")
    private User recordedBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;

    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }
}
