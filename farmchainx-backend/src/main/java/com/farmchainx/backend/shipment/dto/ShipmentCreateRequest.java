package com.farmchainx.backend.shipment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentCreateRequest {
    private Long orderId;
    private String vehicleNumber;
    private String driverName;
    private String driverContact;
    private String destinationAddress;
    private Double destinationLatitude;
    private Double destinationLongitude;
    private LocalDateTime estimatedDeliveryAt;
    private String notes;
}
