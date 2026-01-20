package com.farmchainx.backend.controller;

import com.farmchainx.backend.entity.Farmer;
import com.farmchainx.backend.service.FarmerService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final FarmerService farmerService;

    public UserController(FarmerService farmerService) {
        this.farmerService = farmerService;
    }

    /**
     * Farmer onboarding (after registration)
     * Accessible to FARMER role
     */
    @PostMapping("/farmer/onboard")
    public Farmer onboardFarmer(
            @RequestParam Long userId,
            @RequestParam String farmLocation,
            @RequestParam String cropType
    ) {
        return farmerService.onboardFarmer(userId, farmLocation, cropType);
    }
}
