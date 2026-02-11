package com.farmchainx.backend.shipment.dto;

import com.farmchainx.backend.shipment.enums.ShipmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentResponse {
    private Long id;
    private String trackingNumber;
    private ShipmentStatus status;

    // Order info
    private Long orderId;
    private String cropName;
    private Double quantity;

    // Driver info
    private String vehicleNumber;
    private String driverName;
    private String driverContact;

    // Location
    private LocationInfo currentLocation;
    private LocationInfo destination;

    // Condition
    private Double currentTemperature;
    private Double currentHumidity;
    private String conditionStatus;

    // Timeline
    private LocalDateTime createdAt;
    private LocalDateTime dispatchedAt;
    private LocalDateTime estimatedDeliveryAt;
    private LocalDateTime actualDeliveryAt;

    // Tracking events
    private List<TrackingEventInfo> trackingEvents;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LocationInfo {
        private Double latitude;
        private Double longitude;
        private String address;
        private LocalDateTime timestamp;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrackingEventInfo {
        private String eventType;
        private String description;
        private Double latitude;
        private Double longitude;
        private String locationName;
        private LocalDateTime timestamp;
    }
}
