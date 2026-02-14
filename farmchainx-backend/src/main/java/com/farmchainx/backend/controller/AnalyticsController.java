package com.farmchainx.backend.controller;

import com.farmchainx.backend.common.dto.ApiResponse;
import com.farmchainx.backend.dto.AnalyticsResponse;
import com.farmchainx.backend.service.AnalyticsService;
import com.farmchainx.backend.service.CropService;
import com.farmchainx.backend.dto.CropTraceResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final CropService cropService;

    public AnalyticsController(AnalyticsService analyticsService, CropService cropService) {
        this.analyticsService = analyticsService;
        this.cropService = cropService;
    }

    @GetMapping("/crops/total")
    public ResponseEntity<ApiResponse<Long>> getTotalCrops() {
        try {
            return ResponseEntity.ok(ApiResponse.success("Total crops retrieved", analyticsService.getTotalCrops()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/orders/total")
    public ResponseEntity<ApiResponse<Long>> getTotalOrders() {
        try {
            return ResponseEntity.ok(ApiResponse.success("Total orders retrieved", analyticsService.getTotalOrders()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/users/total")
    public ResponseEntity<ApiResponse<Long>> getTotalUsers() {
        try {
            return ResponseEntity.ok(ApiResponse.success("Total users retrieved", analyticsService.getTotalUsers()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/farmers/total")
    public ResponseEntity<ApiResponse<Long>> getTotalFarmers() {
        try {
            return ResponseEntity.ok(ApiResponse.success("Total farmers retrieved", analyticsService.getTotalFarmers()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/users/active")
    public ResponseEntity<ApiResponse<Long>> getActiveUsers() {
        try {
            return ResponseEntity.ok(ApiResponse.success("Active users retrieved", analyticsService.getActiveUsers()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/disputes/total")
    public ResponseEntity<ApiResponse<Long>> getTotalDisputes() {
        try {
            return ResponseEntity.ok(ApiResponse.success("Total disputes retrieved", analyticsService.getTotalDisputes()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/changes/state")
    public ResponseEntity<ApiResponse<Long>> getTotalStateChanges() {
        try {
            return ResponseEntity.ok(ApiResponse.success("Total state changes retrieved", analyticsService.getTotalStateChanges()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/changes/ownership")
    public ResponseEntity<ApiResponse<Long>> getTotalOwnershipTransfers() {
        try {
            return ResponseEntity.ok(ApiResponse.success("Total ownership transfers retrieved", analyticsService.getTotalOwnershipTransfers()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/comprehensive")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AnalyticsResponse>> getComprehensiveAnalytics() {
        try {
            return ResponseEntity.ok(ApiResponse.success("Comprehensive analytics retrieved", analyticsService.getComprehensiveAnalytics()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAnalyticsSummary() {
        try {
            Map<String, Object> summary = new HashMap<>();
            summary.put("totalCrops", analyticsService.getTotalCrops());
            summary.put("totalOrders", analyticsService.getTotalOrders());
            summary.put("totalUsers", analyticsService.getTotalUsers());
            summary.put("totalFarmers", analyticsService.getTotalFarmers());
            summary.put("activeUsers", analyticsService.getActiveUsers());
            summary.put("totalDisputes", analyticsService.getTotalDisputes());
            summary.put("stateChanges", analyticsService.getTotalStateChanges());
            summary.put("ownershipTransfers", analyticsService.getTotalOwnershipTransfers());
            return ResponseEntity.ok(ApiResponse.success("Analytics summary retrieved", summary));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/crops/trace/{hash}")
    public ResponseEntity<ApiResponse<CropTraceResponse>> traceCrop(@PathVariable String hash) {
        try {
            return ResponseEntity.ok(ApiResponse.success("Crop traced", cropService.traceCrop(hash)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/system/health")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSystemHealth() {
        try {
            Map<String, Object> health = new HashMap<>();
            health.put("status", "healthy");
            health.put("totalEntities", analyticsService.getTotalCrops() + analyticsService.getTotalOrders() + analyticsService.getTotalUsers());
            health.put("systemLoad", "normal");
            health.put("uptime", "99.9%");
            return ResponseEntity.ok(ApiResponse.success("System health retrieved", health));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // ----------------- New endpoints for frontend charts -----------------

    @GetMapping("/users/by-role")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> usersByRole(
            @RequestParam(required = false) String timeFrame,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate
    ) {
        try {
            var range = analyticsService.resolveDateRange(timeFrame, startDate, endDate);
            return ResponseEntity.ok(ApiResponse.success("Users by role retrieved", analyticsService.getUserCountByRole(range[0], range[1])));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/crops/by-region")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> cropsByRegion(
            @RequestParam(required = false) String timeFrame,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate
    ) {
        try {
            var range = analyticsService.resolveDateRange(timeFrame, startDate, endDate);
            return ResponseEntity.ok(ApiResponse.success("Crops by region retrieved", analyticsService.getCropStatsByRegion(range[0], range[1])));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/orders/overview")
    public ResponseEntity<ApiResponse<Map<String, Object>>> ordersOverview(
            @RequestParam(required = false) String timeFrame,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate
    ) {
        try {
            var range = analyticsService.resolveDateRange(timeFrame, startDate, endDate);
            return ResponseEntity.ok(ApiResponse.success("Orders overview retrieved", analyticsService.getOrderOverview(range[0], range[1])));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

}
