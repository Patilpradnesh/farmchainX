package com.farmchainx.backend.dto;

import java.util.List;

public class FarmerDashboardResponse {

    private String email;
    private String status;
    private List<CropSummary> crops;

    public FarmerDashboardResponse(String email,
                                   String status,
                                   List<CropSummary> crops) {
        this.email = email;
        this.status = status;
        this.crops = crops;
    }

    public String getEmail() { return email; }
    public String getStatus() { return status; }
    public List<CropSummary> getCrops() { return crops; }

    public static class CropSummary {
        private Long id;
        private String cropName;
        private String hash;

        public CropSummary(Long id,
                           String cropName,
                           String hash) {
            this.id = id;
            this.cropName = cropName;
            this.hash = hash;
        }

        public Long getId() { return id; }
        public String getCropName() { return cropName; }
        public String getHash() { return hash; }
    }
}
