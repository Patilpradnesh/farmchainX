package com.farmchainx.backend.dto;

import java.util.List;

public class ConsumerDashboardResponse {
    private String email;
    private String status;
    private ConsumerStats stats;
    private List<PurchaseHistory> purchaseHistory;
    private List<TraceableItem> traceableItems;

    public ConsumerDashboardResponse(String email, String status, ConsumerStats stats,
                                    List<PurchaseHistory> purchaseHistory, List<TraceableItem> traceableItems) {
        this.email = email;
        this.status = status;
        this.stats = stats;
        this.purchaseHistory = purchaseHistory;
        this.traceableItems = traceableItems;
    }

    // Getters
    public String getEmail() { return email; }
    public String getStatus() { return status; }
    public ConsumerStats getStats() { return stats; }
    public List<PurchaseHistory> getPurchaseHistory() { return purchaseHistory; }
    public List<TraceableItem> getTraceableItems() { return traceableItems; }

    public static class ConsumerStats {
        private long totalPurchases;
        private long activeOrders;
        private long completedOrders;
        private double totalSpent;
        private long itemsTracked;

        public ConsumerStats(long totalPurchases, long activeOrders, long completedOrders,
                           double totalSpent, long itemsTracked) {
            this.totalPurchases = totalPurchases;
            this.activeOrders = activeOrders;
            this.completedOrders = completedOrders;
            this.totalSpent = totalSpent;
            this.itemsTracked = itemsTracked;
        }

        // Getters
        public long getTotalPurchases() { return totalPurchases; }
        public long getActiveOrders() { return activeOrders; }
        public long getCompletedOrders() { return completedOrders; }
        public double getTotalSpent() { return totalSpent; }
        public long getItemsTracked() { return itemsTracked; }
    }

    public static class PurchaseHistory {
        private Long orderId;
        private String cropName;
        private String retailerEmail;
        private String status;
        private String purchaseDate;
        private String blockchainHash;

        public PurchaseHistory(Long orderId, String cropName, String retailerEmail,
                             String status, String purchaseDate, String blockchainHash) {
            this.orderId = orderId;
            this.cropName = cropName;
            this.retailerEmail = retailerEmail;
            this.status = status;
            this.purchaseDate = purchaseDate;
            this.blockchainHash = blockchainHash;
        }

        // Getters
        public Long getOrderId() { return orderId; }
        public String getCropName() { return cropName; }
        public String getRetailerEmail() { return retailerEmail; }
        public String getStatus() { return status; }
        public String getPurchaseDate() { return purchaseDate; }
        public String getBlockchainHash() { return blockchainHash; }
    }

    public static class TraceableItem {
        private String blockchainHash;
        private String cropName;
        private String currentStatus;
        private String originFarm;
        private String harvestDate;
        private String lastUpdate;

        public TraceableItem(String blockchainHash, String cropName, String currentStatus,
                           String originFarm, String harvestDate, String lastUpdate) {
            this.blockchainHash = blockchainHash;
            this.cropName = cropName;
            this.currentStatus = currentStatus;
            this.originFarm = originFarm;
            this.harvestDate = harvestDate;
            this.lastUpdate = lastUpdate;
        }

        // Getters
        public String getBlockchainHash() { return blockchainHash; }
        public String getCropName() { return cropName; }
        public String getCurrentStatus() { return currentStatus; }
        public String getOriginFarm() { return originFarm; }
        public String getHarvestDate() { return harvestDate; }
        public String getLastUpdate() { return lastUpdate; }
    }
}
