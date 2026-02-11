package com.farmchainx.backend.dto;

import java.util.List;

public class RetailerDashboardResponse {
    private String email;
    private String status;
    private RetailStats stats;
    private List<InventoryItem> inventory;
    private List<CustomerOrder> recentOrders;

    public RetailerDashboardResponse(String email, String status, RetailStats stats,
                                    List<InventoryItem> inventory, List<CustomerOrder> recentOrders) {
        this.email = email;
        this.status = status;
        this.stats = stats;
        this.inventory = inventory;
        this.recentOrders = recentOrders;
    }

    // Getters
    public String getEmail() { return email; }
    public String getStatus() { return status; }
    public RetailStats getStats() { return stats; }
    public List<InventoryItem> getInventory() { return inventory; }
    public List<CustomerOrder> getRecentOrders() { return recentOrders; }

    public static class RetailStats {
        private long totalInventoryItems;
        private long soldItems;
        private long pendingOrders;
        private double totalRevenue;
        private long customersServed;

        public RetailStats(long totalInventoryItems, long soldItems, long pendingOrders,
                          double totalRevenue, long customersServed) {
            this.totalInventoryItems = totalInventoryItems;
            this.soldItems = soldItems;
            this.pendingOrders = pendingOrders;
            this.totalRevenue = totalRevenue;
            this.customersServed = customersServed;
        }

        // Getters
        public long getTotalInventoryItems() { return totalInventoryItems; }
        public long getSoldItems() { return soldItems; }
        public long getPendingOrders() { return pendingOrders; }
        public double getTotalRevenue() { return totalRevenue; }
        public long getCustomersServed() { return customersServed; }
    }

    public static class InventoryItem {
        private Long cropId;
        private String cropName;
        private String supplierEmail;
        private String status;
        private String receivedDate;
        private double quantity;

        public InventoryItem(Long cropId, String cropName, String supplierEmail,
                           String status, String receivedDate, double quantity) {
            this.cropId = cropId;
            this.cropName = cropName;
            this.supplierEmail = supplierEmail;
            this.status = status;
            this.receivedDate = receivedDate;
            this.quantity = quantity;
        }

        // Getters
        public Long getCropId() { return cropId; }
        public String getCropName() { return cropName; }
        public String getSupplierEmail() { return supplierEmail; }
        public String getStatus() { return status; }
        public String getReceivedDate() { return receivedDate; }
        public double getQuantity() { return quantity; }
    }

    public static class CustomerOrder {
        private Long orderId;
        private String customerEmail;
        private String cropName;
        private String status;
        private String orderDate;

        public CustomerOrder(Long orderId, String customerEmail, String cropName,
                           String status, String orderDate) {
            this.orderId = orderId;
            this.customerEmail = customerEmail;
            this.cropName = cropName;
            this.status = status;
            this.orderDate = orderDate;
        }

        // Getters
        public Long getOrderId() { return orderId; }
        public String getCustomerEmail() { return customerEmail; }
        public String getCropName() { return cropName; }
        public String getStatus() { return status; }
        public String getOrderDate() { return orderDate; }
    }
}
