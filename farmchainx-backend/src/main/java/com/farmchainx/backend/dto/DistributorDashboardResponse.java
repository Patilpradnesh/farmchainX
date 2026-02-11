package com.farmchainx.backend.dto;

import java.util.List;

public class DistributorDashboardResponse {
    private String email;
    private String status;
    private DashboardStats stats;
    private List<PurchaseSummary> recentPurchases;
    private List<SaleSummary> recentSales;

    public DistributorDashboardResponse(String email, String status, DashboardStats stats,
                                        List<PurchaseSummary> recentPurchases, List<SaleSummary> recentSales) {
        this.email = email;
        this.status = status;
        this.stats = stats;
        this.recentPurchases = recentPurchases;
        this.recentSales = recentSales;
    }

    // Getters
    public String getEmail() { return email; }
    public String getStatus() { return status; }
    public DashboardStats getStats() { return stats; }
    public List<PurchaseSummary> getRecentPurchases() { return recentPurchases; }
    public List<SaleSummary> getRecentSales() { return recentSales; }

    public static class DashboardStats {
        private long totalPurchases;
        private long totalSales;
        private long activeOrders;
        private double totalRevenue;

        public DashboardStats(long totalPurchases, long totalSales, long activeOrders, double totalRevenue) {
            this.totalPurchases = totalPurchases;
            this.totalSales = totalSales;
            this.activeOrders = activeOrders;
            this.totalRevenue = totalRevenue;
        }

        // Getters
        public long getTotalPurchases() { return totalPurchases; }
        public long getTotalSales() { return totalSales; }
        public long getActiveOrders() { return activeOrders; }
        public double getTotalRevenue() { return totalRevenue; }
    }

    public static class PurchaseSummary {
        private Long orderId;
        private String cropName;
        private String farmerEmail;
        private String status;
        private String purchaseDate;

        public PurchaseSummary(Long orderId, String cropName, String farmerEmail, String status, String purchaseDate) {
            this.orderId = orderId;
            this.cropName = cropName;
            this.farmerEmail = farmerEmail;
            this.status = status;
            this.purchaseDate = purchaseDate;
        }

        // Getters
        public Long getOrderId() { return orderId; }
        public String getCropName() { return cropName; }
        public String getFarmerEmail() { return farmerEmail; }
        public String getStatus() { return status; }
        public String getPurchaseDate() { return purchaseDate; }
    }

    public static class SaleSummary {
        private Long orderId;
        private String cropName;
        private String buyerEmail;
        private String status;
        private String saleDate;

        public SaleSummary(Long orderId, String cropName, String buyerEmail, String status, String saleDate) {
            this.orderId = orderId;
            this.cropName = cropName;
            this.buyerEmail = buyerEmail;
            this.status = status;
            this.saleDate = saleDate;
        }

        // Getters
        public Long getOrderId() { return orderId; }
        public String getCropName() { return cropName; }
        public String getBuyerEmail() { return buyerEmail; }
        public String getStatus() { return status; }
        public String getSaleDate() { return saleDate; }
    }
}
