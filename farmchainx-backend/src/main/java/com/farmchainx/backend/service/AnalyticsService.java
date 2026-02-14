package com.farmchainx.backend.service;

import com.farmchainx.backend.dto.AnalyticsResponse;
import com.farmchainx.backend.enums.CropState;
import com.farmchainx.backend.enums.OrderState;
import com.farmchainx.backend.enums.Role;
import com.farmchainx.backend.enums.Status;
import com.farmchainx.backend.repository.CropRepository;
import com.farmchainx.backend.repository.CropHistoryRepository;
import com.farmchainx.backend.repository.OrderRepository;
import com.farmchainx.backend.repository.UserRepository;
import com.farmchainx.backend.repository.DisputeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    private final CropRepository cropRepository;
    private final CropHistoryRepository cropHistoryRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final DisputeRepository disputeRepository;

    public AnalyticsService(CropRepository cropRepository, CropHistoryRepository cropHistoryRepository,
                           OrderRepository orderRepository, UserRepository userRepository,
                           DisputeRepository disputeRepository) {
        this.cropRepository = cropRepository;
        this.cropHistoryRepository = cropHistoryRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.disputeRepository = disputeRepository;
    }

    public Long getTotalCrops() {
        return cropRepository.count();
    }

    public Long getTotalStateChanges() {
        return cropHistoryRepository.countStateChanges();
    }

    public Long getTotalOwnershipTransfers() {
        return cropHistoryRepository.countOwnershipTransfers();
    }

    public Long getTotalOrders() {
        return orderRepository.count();
    }

    public Long getTotalUsers() {
        return userRepository.count();
    }

    public Long getTotalFarmers() {
        return userRepository.countByRole(Role.FARMER);
    }

    public Long getActiveUsers() {
        return userRepository.countByStatus(Status.APPROVED);
    }

    public Long getTotalDisputes() {
        return disputeRepository.count();
    }

    public AnalyticsResponse getComprehensiveAnalytics() {
        // System stats
        AnalyticsResponse.SystemStats systemStats = new AnalyticsResponse.SystemStats(
                getTotalUsers(),
                getTotalFarmers(),
                getTotalCrops(),
                getTotalOrders(),
                getTotalDisputes(),
                getActiveUsers()
        );

        // Crop state distribution
        List<AnalyticsResponse.ChartData> cropStateDistribution = Arrays.stream(CropState.values())
                .map(state -> new AnalyticsResponse.ChartData(
                        state.name(),
                        cropRepository.countByCropState(state),
                        getColorForCropState(state)
                ))
                .collect(Collectors.toList());

        // Order status distribution
        List<AnalyticsResponse.ChartData> orderStatusDistribution = Arrays.stream(OrderState.values())
                .map(state -> new AnalyticsResponse.ChartData(
                        state.name(),
                        orderRepository.countByOrderState(state),
                        getColorForOrderState(state)
                ))
                .collect(Collectors.toList());

        // Monthly trends (placeholder - would need actual time-based queries)
        List<AnalyticsResponse.TimeSeriesData> monthlyTrends = generateMonthlyTrends();

        // Additional metrics
        Map<String, Object> additionalMetrics = new HashMap<>();
        additionalMetrics.put("averageCropsPerFarmer", getTotalFarmers() > 0 ? getTotalCrops() / getTotalFarmers() : 0);
        additionalMetrics.put("orderCompletionRate", calculateOrderCompletionRate());
        additionalMetrics.put("disputeRate", calculateDisputeRate());
        additionalMetrics.put("userGrowthRate", 5.2); // Placeholder
        additionalMetrics.put("systemHealth", "Excellent");

        return new AnalyticsResponse(
                systemStats,
                cropStateDistribution,
                orderStatusDistribution,
                monthlyTrends,
                additionalMetrics
        );
    }

    // ------------------- New chart-friendly methods -------------------

    // Resolve named timeframe to start/end LocalDateTime
    public LocalDateTime[] resolveDateRange(String timeFrame, LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null) {
            return new LocalDateTime[]{startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX)};
        }
        LocalDate today = LocalDate.now();
        return switch (timeFrame == null ? "" : timeFrame.toLowerCase()) {
            case "today" -> new LocalDateTime[]{today.atStartOfDay(), today.atTime(LocalTime.MAX)};
            case "week" -> new LocalDateTime[]{today.minusDays(7).atStartOfDay(), today.atTime(LocalTime.MAX)};
            case "month" -> new LocalDateTime[]{today.minusMonths(1).atStartOfDay(), today.atTime(LocalTime.MAX)};
            case "year" -> new LocalDateTime[]{today.minusYears(1).atStartOfDay(), today.atTime(LocalTime.MAX)};
            default -> new LocalDateTime[]{today.minusMonths(1).atStartOfDay(), today.atTime(LocalTime.MAX)}; // default 1 month
        };
    }

    // Returns list of role-count pairs for charting
    public List<Map<String, Object>> getUserCountByRole(LocalDateTime start, LocalDateTime end) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Role role : Role.values()) {
            long cnt = userRepository.countByRoleAndCreatedAtBetween(role, start, end);
            Map<String, Object> m = new HashMap<>();
            m.put("role", role.name());
            m.put("count", cnt);
            result.add(m);
        }
        return result;
    }

    // Returns crop stats grouped by region and cropName
    public List<Map<String, Object>> getCropStatsByRegion(LocalDateTime start, LocalDateTime end) {
        List<Object[]> rows = cropRepository.findCropStatsByRegionAndName(start, end);
        List<Map<String, Object>> out = new ArrayList<>();
        for (Object[] r : rows) {
            String region = (String) r[0];
            String cropName = (String) r[1];
            Long cnt = (Long) r[2];
            Double totalQty = (Double) r[3];
            Map<String, Object> m = new HashMap<>();
            m.put("region", region);
            m.put("cropName", cropName);
            m.put("count", cnt);
            m.put("totalQuantity", totalQty);
            out.add(m);
        }
        return out;
    }

    // Returns order stats grouped by region and buyer role
    public Map<String, Object> getOrderOverview(LocalDateTime start, LocalDateTime end) {
        Map<String, Object> out = new HashMap<>();
        List<Object[]> byRegion = orderRepository.findOrderStatsByRegion(start, end);
        List<Map<String, Object>> regionStats = new ArrayList<>();
        for (Object[] r : byRegion) {
            String region = (String) r[0];
            Long cnt = (Long) r[1];
            Double totalValue = (Double) r[2];
            Map<String, Object> m = new HashMap<>();
            m.put("region", region);
            m.put("orders", cnt);
            m.put("totalValue", totalValue);
            regionStats.add(m);
        }

        List<Object[]> byRole = orderRepository.findOrderStatsByBuyerRole(start, end);
        List<Map<String, Object>> roleStats = new ArrayList<>();
        for (Object[] r : byRole) {
            Object roleObj = r[0];
            String role = roleObj != null ? roleObj.toString() : "UNKNOWN";
            Long cnt = (Long) r[1];
            Double totalValue = (Double) r[2];
            Map<String, Object> m = new HashMap<>();
            m.put("role", role);
            m.put("orders", cnt);
            m.put("totalValue", totalValue);
            roleStats.add(m);
        }

        out.put("byRegion", regionStats);
        out.put("byBuyerRole", roleStats);
        return out;
    }

    // -----------------------------------------------------------------

    private String getColorForCropState(CropState state) {
        return switch (state) {
            case CREATED -> "#3B82F6";    // Blue
            case LISTED -> "#10B981";     // Green
            case ORDERED -> "#F59E0B";    // Yellow
            case SHIPPED -> "#8B5CF6";    // Purple
            case DELIVERED -> "#06B6D4";  // Cyan
            case CLOSED -> "#6B7280";     // Gray
        };
    }

    private String getColorForOrderState(OrderState state) {
        return switch (state) {
            case PLACED -> "#3B82F6";     // Blue
            case ACCEPTED -> "#10B981";   // Green
            case SHIPPED -> "#8B5CF6";    // Purple
            case COMPLETED -> "#059669";  // Dark Green
            case CANCELLED -> "#EF4444";  // Red
        };
    }

    private List<AnalyticsResponse.TimeSeriesData> generateMonthlyTrends() {
        // This is a placeholder. In a real implementation, you would query the database
        // for actual monthly data over the past 12 months
        List<AnalyticsResponse.TimeSeriesData> trends = new ArrayList<>();
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                          "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

        for (String month : months) {
            trends.add(new AnalyticsResponse.TimeSeriesData(
                    month,
                    (long) (Math.random() * 100 + 50),  // Random data for crops
                    (long) (Math.random() * 80 + 30),   // Random data for orders
                    (long) (Math.random() * 20 + 10)    // Random data for users
            ));
        }

        return trends;
    }

    private double calculateOrderCompletionRate() {
        long totalOrders = getTotalOrders();
        if (totalOrders == 0) return 0.0;

        long completedOrders = orderRepository.countByOrderState(OrderState.COMPLETED);
        return (double) completedOrders / totalOrders * 100;
    }

    private double calculateDisputeRate() {
        long totalOrders = getTotalOrders();
        if (totalOrders == 0) return 0.0;

        long totalDisputes = getTotalDisputes();
        return (double) totalDisputes / totalOrders * 100;
    }
}
