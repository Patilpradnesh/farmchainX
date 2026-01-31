package com.farmchainx.backend.dto;

public class CropTraceResponse {

    private String cropName;
    private String hash;
    private String status;
    private String createdAt;
    private String farmerEmail;

    public CropTraceResponse(String cropName,
                             String hash,
                             String status,
                             String createdAt,
                             String farmerEmail) {
        this.cropName = cropName;
        this.hash = hash;
        this.status = status;
        this.createdAt = createdAt;
        this.farmerEmail = farmerEmail;
    }

    public String getCropName() { return cropName; }
    public String getHash() { return hash; }
    public String getStatus() { return status; }
    public String getCreatedAt() { return createdAt; }
    public String getFarmerEmail() { return farmerEmail; }
}
