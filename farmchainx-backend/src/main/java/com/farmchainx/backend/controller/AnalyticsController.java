package com.farmchainx.backend.controller;

import com.farmchainx.backend.service.AnalyticsService;
import com.farmchainx.backend.service.CropService;
import com.farmchainx.backend.dto.CropTraceResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

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

    @GetMapping("/changes/state")
    public ResponseEntity<Long> getTotalStateChanges() {
        return ResponseEntity.ok(analyticsService.getTotalStateChanges());
    }

    @GetMapping("/transfers/ownership")
    public ResponseEntity<Long> getTotalOwnershipTransfers() {
        return ResponseEntity.ok(analyticsService.getTotalOwnershipTransfers());
    }

    @GetMapping("/orders/total")
    public ResponseEntity<Long> getTotalOrders() {
        return ResponseEntity.ok(analyticsService.getTotalOrders());
    }

    @GetMapping("/trace/{hash}")
    public ResponseEntity<CropTraceResponse> traceCrop(@PathVariable String hash) {
        return ResponseEntity.ok(cropService.traceCrop(hash));
    }
}
