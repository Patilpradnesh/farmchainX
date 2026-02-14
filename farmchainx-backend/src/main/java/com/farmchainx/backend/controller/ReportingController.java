package com.farmchainx.backend.controller;

import com.farmchainx.backend.common.dto.ApiResponse;
import com.farmchainx.backend.service.AnalyticsService;
import com.farmchainx.backend.repository.*;
import com.farmchainx.backend.enums.Status;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/reports")
@PreAuthorize("hasRole('ADMIN')")
public class ReportingController {

    private final AnalyticsService analyticsService;
    private final UserRepository userRepository;
    private final CropRepository cropRepository;
    private final OrderRepository orderRepository;
    private final DisputeRepository disputeRepository;

    public ReportingController(AnalyticsService analyticsService,
                              UserRepository userRepository,
                              CropRepository cropRepository,
                              OrderRepository orderRepository,
                              DisputeRepository disputeRepository) {
        this.analyticsService = analyticsService;
        this.userRepository = userRepository;
        this.cropRepository = cropRepository;
        this.orderRepository = orderRepository;
        this.disputeRepository = disputeRepository;
    }

    @GetMapping("/system-overview")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSystemOverview() {
        try {
            Map<String, Object> overview = new HashMap<>();

            overview.put("totalUsers", analyticsService.getTotalUsers());
            overview.put("totalFarmers", analyticsService.getTotalFarmers());
            overview.put("activeUsers", analyticsService.getActiveUsers());
            overview.put("totalCrops", analyticsService.getTotalCrops());
            overview.put("totalOrders", analyticsService.getTotalOrders());
            overview.put("totalDisputes", analyticsService.getTotalDisputes());
            overview.put("stateChanges", analyticsService.getTotalStateChanges());
            overview.put("ownershipTransfers", analyticsService.getTotalOwnershipTransfers());

            return ResponseEntity.ok(ApiResponse.success("System overview retrieved", overview));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/user-statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserStatistics() {
        try {
            Map<String, Object> stats = new HashMap<>();

            stats.put("byRole", Map.of(
                    "FARMER", userRepository.countByRole(com.farmchainx.backend.enums.Role.FARMER),
                    "DISTRIBUTOR", userRepository.countByRole(com.farmchainx.backend.enums.Role.DISTRIBUTOR),
                    "RETAILER", userRepository.countByRole(com.farmchainx.backend.enums.Role.RETAILER),
                    "CONSUMER", userRepository.countByRole(com.farmchainx.backend.enums.Role.CONSUMER),
                    "ADMIN", userRepository.countByRole(com.farmchainx.backend.enums.Role.ADMIN)
            ));

            stats.put("byStatus", Map.of(
                    "PENDING", userRepository.countByStatus(Status.PENDING),
                    "APPROVED", userRepository.countByStatus(Status.APPROVED),
                    "REJECTED", userRepository.countByStatus(Status.REJECTED),
                    "SUSPENDED", userRepository.countByStatus(Status.SUSPENDED)
            ));

            return ResponseEntity.ok(ApiResponse.success("User statistics retrieved", stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/crop-statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCropStatistics() {
        try {
            Map<String, Object> stats = new HashMap<>();

            stats.put("byState", Map.of(
                    "CREATED", cropRepository.countByCropState(com.farmchainx.backend.enums.CropState.CREATED),
                    "LISTED", cropRepository.countByCropState(com.farmchainx.backend.enums.CropState.LISTED),
                    "ORDERED", cropRepository.countByCropState(com.farmchainx.backend.enums.CropState.ORDERED),
                    "SHIPPED", cropRepository.countByCropState(com.farmchainx.backend.enums.CropState.SHIPPED),
                    "DELIVERED", cropRepository.countByCropState(com.farmchainx.backend.enums.CropState.DELIVERED),
                    "CLOSED", cropRepository.countByCropState(com.farmchainx.backend.enums.CropState.CLOSED)
            ));

            return ResponseEntity.ok(ApiResponse.success("Crop statistics retrieved", stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/order-statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getOrderStatistics() {
        try {
            Map<String, Object> stats = new HashMap<>();

            stats.put("byState", Map.of(
                    "PLACED", orderRepository.countByOrderState(com.farmchainx.backend.enums.OrderState.PLACED),
                    "ACCEPTED", orderRepository.countByOrderState(com.farmchainx.backend.enums.OrderState.ACCEPTED),
                    "SHIPPED", orderRepository.countByOrderState(com.farmchainx.backend.enums.OrderState.SHIPPED),
                    "COMPLETED", orderRepository.countByOrderState(com.farmchainx.backend.enums.OrderState.COMPLETED),
                    "CANCELLED", orderRepository.countByOrderState(com.farmchainx.backend.enums.OrderState.CANCELLED)
            ));

            return ResponseEntity.ok(ApiResponse.success("Order statistics retrieved", stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/dispute-statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDisputeStatistics() {
        try {
            Map<String, Object> stats = new HashMap<>();

            stats.put("byStatus", Map.of(
                    "OPEN", disputeRepository.countByStatus("OPEN"),
                    "RESOLVED", disputeRepository.countByStatus("RESOLVED"),
                    "ESCALATED", disputeRepository.countByStatus("ESCALATED"),
                    "CLOSED", disputeRepository.countByStatus("CLOSED")
            ));

            return ResponseEntity.ok(ApiResponse.success("Dispute statistics retrieved", stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/performance-metrics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPerformanceMetrics() {
        try {
            Map<String, Object> metrics = new HashMap<>();

            long totalOrders = analyticsService.getTotalOrders();
            long completedOrders = orderRepository.countByOrderState(com.farmchainx.backend.enums.OrderState.COMPLETED);
            long cancelledOrders = orderRepository.countByOrderState(com.farmchainx.backend.enums.OrderState.CANCELLED);
            long totalDisputes = analyticsService.getTotalDisputes();

            double orderCompletionRate = totalOrders > 0 ? (double) completedOrders / totalOrders * 100 : 0;
            double orderCancellationRate = totalOrders > 0 ? (double) cancelledOrders / totalOrders * 100 : 0;
            double disputeRate = totalOrders > 0 ? (double) totalDisputes / totalOrders * 100 : 0;

            metrics.put("orderCompletionRate", Math.round(orderCompletionRate * 100.0) / 100.0);
            metrics.put("orderCancellationRate", Math.round(orderCancellationRate * 100.0) / 100.0);
            metrics.put("disputeRate", Math.round(disputeRate * 100.0) / 100.0);
            metrics.put("totalTransactions", totalOrders);
            metrics.put("successfulTransactions", completedOrders);

            return ResponseEntity.ok(ApiResponse.success("Performance metrics retrieved", metrics));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/activity-summary")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getActivitySummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            // This is a simplified version. In production, you'd filter by date ranges
            Map<String, Object> summary = new HashMap<>();

            summary.put("newUsersRegistered", userRepository.countByStatus(com.farmchainx.backend.enums.Status.PENDING));
            summary.put("cropsCreated", cropRepository.countByCropState(com.farmchainx.backend.enums.CropState.CREATED));
            summary.put("cropsListed", cropRepository.countByCropState(com.farmchainx.backend.enums.CropState.LISTED));
            summary.put("ordersPlaced", orderRepository.countByOrderState(com.farmchainx.backend.enums.OrderState.PLACED));
            summary.put("ordersCompleted", orderRepository.countByOrderState(com.farmchainx.backend.enums.OrderState.COMPLETED));
            summary.put("disputesRaised", disputeRepository.countByStatus("OPEN"));
            summary.put("disputesResolved", disputeRepository.countByStatus("RESOLVED"));

            return ResponseEntity.ok(ApiResponse.success("Activity summary retrieved", summary));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/export/csv")
    public ResponseEntity<ApiResponse<String>> exportDataAsCSV(@RequestParam String dataType) {
        try {
            // This is a placeholder for CSV export functionality
            String csvData = "id,name,status,created_at\n" +
                            "1,Sample Data,ACTIVE," + LocalDateTime.now() + "\n";

            return ResponseEntity.ok(ApiResponse.success("CSV export generated", csvData));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
