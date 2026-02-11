package com.farmchainx.backend.shipment.entity;

import com.farmchainx.backend.entity.Order;
import com.farmchainx.backend.entity.User;
import com.farmchainx.backend.shipment.enums.ShipmentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Shipment Entity - Tracks physical movement of crops through supply chain
 */
@Entity
@Table(name = "shipments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "carrier_id")
    private User carrier; // Driver/logistics person

    @Column(nullable = false, unique = true)
    private String trackingNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShipmentStatus status;

    private String vehicleNumber;
    private String driverName;
    private String driverContact;

    // Current location
    private Double currentLatitude;
    private Double currentLongitude;
    private String currentLocation;

    // Destination
    private Double destinationLatitude;
    private Double destinationLongitude;
    private String destinationAddress;

    // Condition monitoring (IoT-ready)
    private Double currentTemperature;
    private Double currentHumidity;
    private String conditionStatus; // GOOD, WARNING, CRITICAL

    // Timeline
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime dispatchedAt;
    private LocalDateTime estimatedDeliveryAt;
    private LocalDateTime actualDeliveryAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Additional info
    @Column(length = 1000)
    private String notes;

    private String qrCode; // QR code for package identification

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = ShipmentStatus.CREATED;
        }
        if (trackingNumber == null) {
            trackingNumber = generateTrackingNumber();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    private String generateTrackingNumber() {
        return "FXC" + System.currentTimeMillis() + (int)(Math.random() * 1000);
    }

    // Business methods
    public void updateLocation(Double latitude, Double longitude, String locationName) {
        this.currentLatitude = latitude;
        this.currentLongitude = longitude;
        this.currentLocation = locationName;
    }

    public void updateCondition(Double temperature, Double humidity) {
        this.currentTemperature = temperature;
        this.currentHumidity = humidity;

        // Simple condition logic
        if (temperature != null && (temperature < 0 || temperature > 35)) {
            this.conditionStatus = "CRITICAL";
        } else if (temperature != null && (temperature < 5 || temperature > 30)) {
            this.conditionStatus = "WARNING";
        } else {
            this.conditionStatus = "GOOD";
        }
    }

    public void dispatch() {
        this.status = ShipmentStatus.IN_TRANSIT;
        this.dispatchedAt = LocalDateTime.now();
    }

    public void markDelivered() {
        this.status = ShipmentStatus.DELIVERED;
        this.actualDeliveryAt = LocalDateTime.now();
    }
}
