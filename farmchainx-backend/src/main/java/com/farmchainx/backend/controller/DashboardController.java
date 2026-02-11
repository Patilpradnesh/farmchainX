package com.farmchainx.backend.controller;

import com.farmchainx.backend.dto.*;
import com.farmchainx.backend.service.DashboardService;
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
    public FarmerDashboardResponse farmerDashboard() {
        return dashboardService.getFarmerDashboard();
    }

    @GetMapping("/distributor")
    public DistributorDashboardResponse distributorDashboard() {
        return dashboardService.getDistributorDashboard();
    }

    @GetMapping("/retailer")
    public RetailerDashboardResponse retailerDashboard() {
        return dashboardService.getRetailerDashboard();
    }

    @GetMapping("/consumer")
    public ConsumerDashboardResponse consumerDashboard() {
        return dashboardService.getConsumerDashboard();
    }

}
