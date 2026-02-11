package com.farmchainx.backend.shipment.enums;

/**
 * Shipment lifecycle states
 */
public enum ShipmentStatus {
    CREATED,        // Shipment created but not dispatched
    IN_TRANSIT,     // On the way to destination
    OUT_FOR_DELIVERY, // Near destination
    DELIVERED,      // Delivered to recipient
    CONFIRMED,      // Recipient confirmed receipt
    CANCELLED,      // Shipment cancelled
    DELAYED,        // Delayed in transit
    LOST            // Shipment lost (rare)
}
