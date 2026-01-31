package com.farmchainx.backend.controller;

import com.farmchainx.backend.dto.FarmerDashboardResponse;
import com.farmchainx.backend.dto.GenericDashboardResponse;
import com.farmchainx.backend.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
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
    public GenericDashboardResponse distributorDashboard() {
        return new GenericDashboardResponse("Distributor dashboard not implemented yet");
    }

    @GetMapping("/retailer")
    public GenericDashboardResponse retailerDashboard() {
        return new GenericDashboardResponse("Retailer dashboard not implemented yet");
    }

    @GetMapping("/consumer")
    public GenericDashboardResponse consumerDashboard() {
        return new GenericDashboardResponse("Consumer dashboard not implemented yet");
    }

}
