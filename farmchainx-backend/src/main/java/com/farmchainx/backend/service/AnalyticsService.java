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
