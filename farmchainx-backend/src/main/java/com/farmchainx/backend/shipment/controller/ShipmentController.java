package com.farmchainx.backend.shipment.controller;

import com.farmchainx.backend.common.dto.ApiResponse;
import com.farmchainx.backend.shipment.dto.ShipmentCreateRequest;
import com.farmchainx.backend.shipment.dto.ShipmentResponse;
import com.farmchainx.backend.shipment.service.ShipmentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Shipment Management Controller - Enterprise-grade logistics tracking
 */
@RestController
@RequestMapping("/api/v1/shipments")
public class ShipmentController {

    private final ShipmentService shipmentService;

    public ShipmentController(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

    /**
     * Create new shipment for an order
     * Access: DISTRIBUTOR, RETAILER
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('DISTRIBUTOR', 'RETAILER', 'ADMIN')")
    public ResponseEntity<ApiResponse<ShipmentResponse>> createShipment(
            @Valid @RequestBody ShipmentCreateRequest request) {
        try {
            ShipmentResponse shipment = shipmentService.createShipment(request);
            return ResponseEntity.ok(ApiResponse.success("Shipment created successfully", shipment));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to create shipment: " + e.getMessage()));
        }
    }

    /**
     * Dispatch shipment (mark as in transit)
     */
    @PutMapping("/{id}/dispatch")
    @PreAuthorize("hasAnyRole('DISTRIBUTOR', 'RETAILER', 'ADMIN')")
    public ResponseEntity<ApiResponse<ShipmentResponse>> dispatchShipment(@PathVariable Long id) {
        try {
            ShipmentResponse shipment = shipmentService.dispatchShipment(id);
            return ResponseEntity.ok(ApiResponse.success("Shipment dispatched", shipment));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Update shipment location (GPS tracking)
     */
    @PutMapping("/{id}/location")
    public ResponseEntity<ApiResponse<ShipmentResponse>> updateLocation(
            @PathVariable Long id,
            @RequestBody Map<String, Object> locationData) {
        try {
            Double latitude = ((Number) locationData.get("latitude")).doubleValue();
            Double longitude = ((Number) locationData.get("longitude")).doubleValue();
            String locationName = (String) locationData.get("locationName");

            ShipmentResponse shipment = shipmentService.updateLocation(id, latitude, longitude, locationName);
            return ResponseEntity.ok(ApiResponse.success("Location updated", shipment));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Update shipment condition (IoT sensor data)
     */
    @PutMapping("/{id}/condition")
    public ResponseEntity<ApiResponse<ShipmentResponse>> updateCondition(
            @PathVariable Long id,
            @RequestBody Map<String, Object> conditionData) {
        try {
            Double temperature = ((Number) conditionData.get("temperature")).doubleValue();
            Double humidity = ((Number) conditionData.get("humidity")).doubleValue();

            ShipmentResponse shipment = shipmentService.updateCondition(id, temperature, humidity);
            return ResponseEntity.ok(ApiResponse.success("Condition updated", shipment));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Mark shipment as delivered
     */
    @PutMapping("/{id}/deliver")
    @PreAuthorize("hasAnyRole('DISTRIBUTOR', 'RETAILER', 'ADMIN')")
    public ResponseEntity<ApiResponse<ShipmentResponse>> markDelivered(@PathVariable Long id) {
        try {
            ShipmentResponse shipment = shipmentService.markDelivered(id);
            return ResponseEntity.ok(ApiResponse.success("Shipment marked as delivered", shipment));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Track shipment by tracking number (Public - for QR scan)
     */
    @GetMapping("/track/{trackingNumber}")
    public ResponseEntity<ApiResponse<ShipmentResponse>> trackShipment(
            @PathVariable String trackingNumber) {
        try {
            ShipmentResponse shipment = shipmentService.getShipmentByTrackingNumber(trackingNumber);
            return ResponseEntity.ok(ApiResponse.success(shipment));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Shipment not found"));
        }
    }

    /**
     * Get shipment by order ID
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<ShipmentResponse>> getShipmentByOrder(@PathVariable Long orderId) {
        try {
            ShipmentResponse shipment = shipmentService.getShipmentByOrderId(orderId);
            return ResponseEntity.ok(ApiResponse.success(shipment));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Shipment not found for this order"));
        }
    }

    /**
     * Get all active shipments
     */
    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('DISTRIBUTOR', 'RETAILER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<ShipmentResponse>>> getActiveShipments() {
        try {
            List<ShipmentResponse> shipments = shipmentService.getActiveShipments();
            return ResponseEntity.ok(ApiResponse.success("Active shipments retrieved", shipments));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}
