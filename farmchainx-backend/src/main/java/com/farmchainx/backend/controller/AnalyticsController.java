package com.farmchainx.backend.controller;

import com.farmchainx.backend.dto.AnalyticsResponse;
import com.farmchainx.backend.service.AnalyticsService;
import com.farmchainx.backend.service.CropService;
import com.farmchainx.backend.dto.CropTraceResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.HashMap;
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
    public ResponseEntity<Long> getTotalCrops() {
        return ResponseEntity.ok(analyticsService.getTotalCrops());
    }

    @GetMapping("/orders/total")
    public ResponseEntity<Long> getTotalOrders() {
        return ResponseEntity.ok(analyticsService.getTotalOrders());
    }

    @GetMapping("/users/total")
    public ResponseEntity<Long> getTotalUsers() {
        return ResponseEntity.ok(analyticsService.getTotalUsers());
    }

    @GetMapping("/farmers/total")
    public ResponseEntity<Long> getTotalFarmers() {
        return ResponseEntity.ok(analyticsService.getTotalFarmers());
    }

    @GetMapping("/users/active")
    public ResponseEntity<Long> getActiveUsers() {
        return ResponseEntity.ok(analyticsService.getActiveUsers());
    }

    @GetMapping("/disputes/total")
    public ResponseEntity<Long> getTotalDisputes() {
        return ResponseEntity.ok(analyticsService.getTotalDisputes());
    }

    @GetMapping("/changes/state")
    public ResponseEntity<Long> getTotalStateChanges() {
        return ResponseEntity.ok(analyticsService.getTotalStateChanges());
    }

    @GetMapping("/changes/ownership")
    public ResponseEntity<Long> getTotalOwnershipTransfers() {
        return ResponseEntity.ok(analyticsService.getTotalOwnershipTransfers());
    }

    @GetMapping("/comprehensive")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AnalyticsResponse> getComprehensiveAnalytics() {
        return ResponseEntity.ok(analyticsService.getComprehensiveAnalytics());
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getAnalyticsSummary() {
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalCrops", analyticsService.getTotalCrops());
        summary.put("totalOrders", analyticsService.getTotalOrders());
        summary.put("totalUsers", analyticsService.getTotalUsers());
        summary.put("totalFarmers", analyticsService.getTotalFarmers());
        summary.put("activeUsers", analyticsService.getActiveUsers());
        summary.put("totalDisputes", analyticsService.getTotalDisputes());
        summary.put("stateChanges", analyticsService.getTotalStateChanges());
        summary.put("ownershipTransfers", analyticsService.getTotalOwnershipTransfers());
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/crops/trace/{hash}")
    public ResponseEntity<CropTraceResponse> traceCrop(@PathVariable String hash) {
        return ResponseEntity.ok(cropService.traceCrop(hash));
    }

    @GetMapping("/system/health")
    public ResponseEntity<Map<String, Object>> getSystemHealth() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "healthy");
        health.put("totalEntities", analyticsService.getTotalCrops() + analyticsService.getTotalOrders() + analyticsService.getTotalUsers());
        health.put("systemLoad", "normal");
        health.put("uptime", "99.9%");
        return ResponseEntity.ok(health);
    }
}

