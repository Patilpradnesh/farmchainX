package com.farmchainx.backend.controller;

import com.farmchainx.backend.common.dto.ApiResponse;
import com.farmchainx.backend.dto.*;
import com.farmchainx.backend.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/farmer")
    public ResponseEntity<ApiResponse<FarmerDashboardResponse>> farmerDashboard() {
        try {
            return ResponseEntity.ok(ApiResponse.success("Farmer dashboard retrieved", dashboardService.getFarmerDashboard()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/distributor")
    public ResponseEntity<ApiResponse<DistributorDashboardResponse>> distributorDashboard() {
        try {
            return ResponseEntity.ok(ApiResponse.success("Distributor dashboard retrieved", dashboardService.getDistributorDashboard()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/retailer")
    public ResponseEntity<ApiResponse<RetailerDashboardResponse>> retailerDashboard() {
        try {
            return ResponseEntity.ok(ApiResponse.success("Retailer dashboard retrieved", dashboardService.getRetailerDashboard()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/consumer")
    public ResponseEntity<ApiResponse<ConsumerDashboardResponse>> consumerDashboard() {
        try {
            return ResponseEntity.ok(ApiResponse.success("Consumer dashboard retrieved", dashboardService.getConsumerDashboard()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

}
