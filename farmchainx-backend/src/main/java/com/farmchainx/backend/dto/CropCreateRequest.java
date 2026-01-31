package com.farmchainx.backend.dto;

import java.time.LocalDateTime;

public class CropCreateRequest {

    private String cropName;
    private Double quantity;
    private String location;
    private LocalDateTime harvestDate;

    public String getCropName() { return cropName; }
    public void setCropName(String cropName) { this.cropName = cropName; }

    public Double getQuantity() { return quantity; }
    public void setQuantity(Double quantity) { this.quantity = quantity; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public LocalDateTime getHarvestDate() { return harvestDate; }
    public void setHarvestDate(LocalDateTime harvestDate) { this.harvestDate = harvestDate; }
}
