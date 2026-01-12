package com.farmchainx.backend.controller;

import com.farmchainx.backend.entity.Farmer;
import com.farmchainx.backend.repository.FarmerRepository;
import com.farmchainx.backend.service.AuthService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final FarmerRepository farmerRepository;

    public AuthController(AuthService authService,
                          FarmerRepository farmerRepository) {
        this.authService = authService;
        this.farmerRepository = farmerRepository;
    }

    @PostMapping("/register/farmer")
    public String registerFarmer(@RequestBody Map<String, String> req) {
        authService.registerFarmer(
                req.get("name"),
                req.get("email"),
                req.get("password"),
                req.get("farmLocation"),
                req.get("cropType")
        );
        return "Farmer registered";
    }

    @PostMapping("/login")
    public String login(@RequestBody Map<String, String> req) {
        return authService.loginAndGetToken(
                req.get("email"),
                req.get("password")
        );
    }

    // ✅ MISSING LOGIC — NOW ADDED
    @GetMapping("/farmers")
    public List<Farmer> getAllFarmers() {
        return farmerRepository.findAll();
    }
}
