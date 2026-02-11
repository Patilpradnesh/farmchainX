package com.farmchainx.backend.dto;

import java.time.LocalDateTime;

public class OrderCreateRequest {
    private Long cropId;
    private Double requestedQuantity;
    private Double offeredPrice;
    private String deliveryAddress;
    private LocalDateTime requestedDeliveryDate;
    private String notes;

    // Constructors
    public OrderCreateRequest() {}

    public OrderCreateRequest(Long cropId, Double requestedQuantity, Double offeredPrice,
                             String deliveryAddress, LocalDateTime requestedDeliveryDate, String notes) {
        this.cropId = cropId;
        this.requestedQuantity = requestedQuantity;
        this.offeredPrice = offeredPrice;
        this.deliveryAddress = deliveryAddress;
        this.requestedDeliveryDate = requestedDeliveryDate;
        this.notes = notes;
    }

    // Getters and Setters
    public Long getCropId() { return cropId; }
    public void setCropId(Long cropId) { this.cropId = cropId; }

    public Double getRequestedQuantity() { return requestedQuantity; }
    public void setRequestedQuantity(Double requestedQuantity) { this.requestedQuantity = requestedQuantity; }

    public Double getOfferedPrice() { return offeredPrice; }
    public void setOfferedPrice(Double offeredPrice) { this.offeredPrice = offeredPrice; }

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public LocalDateTime getRequestedDeliveryDate() { return requestedDeliveryDate; }
    public void setRequestedDeliveryDate(LocalDateTime requestedDeliveryDate) { this.requestedDeliveryDate = requestedDeliveryDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
