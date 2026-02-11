package com.farmchainx.backend.dto;

import java.util.List;
import java.util.Map;

public class AnalyticsResponse {
    private SystemStats systemStats;
    private List<ChartData> cropStateDistribution;
    private List<ChartData> orderStatusDistribution;
    private List<TimeSeriesData> monthlyTrends;
    private Map<String, Object> additionalMetrics;

    public AnalyticsResponse(SystemStats systemStats, List<ChartData> cropStateDistribution,
                           List<ChartData> orderStatusDistribution, List<TimeSeriesData> monthlyTrends,
                           Map<String, Object> additionalMetrics) {
        this.systemStats = systemStats;
        this.cropStateDistribution = cropStateDistribution;
        this.orderStatusDistribution = orderStatusDistribution;
        this.monthlyTrends = monthlyTrends;
        this.additionalMetrics = additionalMetrics;
    }

    // Getters
    public SystemStats getSystemStats() { return systemStats; }
    public List<ChartData> getCropStateDistribution() { return cropStateDistribution; }
    public List<ChartData> getOrderStatusDistribution() { return orderStatusDistribution; }
    public List<TimeSeriesData> getMonthlyTrends() { return monthlyTrends; }
    public Map<String, Object> getAdditionalMetrics() { return additionalMetrics; }

    public static class SystemStats {
        private long totalUsers;
        private long totalFarmers;
        private long totalCrops;
        private long totalOrders;
        private long totalDisputes;
        private long activeUsers;

        public SystemStats(long totalUsers, long totalFarmers, long totalCrops,
                          long totalOrders, long totalDisputes, long activeUsers) {
            this.totalUsers = totalUsers;
            this.totalFarmers = totalFarmers;
            this.totalCrops = totalCrops;
            this.totalOrders = totalOrders;
            this.totalDisputes = totalDisputes;
            this.activeUsers = activeUsers;
        }

        // Getters
        public long getTotalUsers() { return totalUsers; }
        public long getTotalFarmers() { return totalFarmers; }
        public long getTotalCrops() { return totalCrops; }
        public long getTotalOrders() { return totalOrders; }
        public long getTotalDisputes() { return totalDisputes; }
        public long getActiveUsers() { return activeUsers; }
    }

    public static class ChartData {
        private String label;
        private long value;
        private String color;

        public ChartData(String label, long value, String color) {
            this.label = label;
            this.value = value;
            this.color = color;
        }

        // Getters
        public String getLabel() { return label; }
        public long getValue() { return value; }
        public String getColor() { return color; }
    }

    public static class TimeSeriesData {
        private String period;
        private long crops;
        private long orders;
        private long users;

        public TimeSeriesData(String period, long crops, long orders, long users) {
            this.period = period;
            this.crops = crops;
            this.orders = orders;
            this.users = users;
        }

        // Getters
        public String getPeriod() { return period; }
        public long getCrops() { return crops; }
        public long getOrders() { return orders; }
        public long getUsers() { return users; }
    }
}
