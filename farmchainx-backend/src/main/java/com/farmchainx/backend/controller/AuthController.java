package com.farmchainx.backend.controller;

import com.farmchainx.backend.dto.LoginRequest;
import com.farmchainx.backend.dto.RegisterRequest;
import com.farmchainx.backend.dto.AuthResponse;
import com.farmchainx.backend.enums.Role;
import com.farmchainx.backend.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {

        var result = authService.loginWithUser(request);

        return new AuthResponse(
                result.getToken(),
                result.getUser().getRole().name(),
                result.getUser().getStatus().name()
        );
    }

    @PostMapping("/register/farmer")
    public void registerFarmer(@RequestBody RegisterRequest request) {
        authService.register(request, Role.FARMER);
    }

    @PostMapping("/register/{role}")
    public void register(@PathVariable Role role,
                         @RequestBody RegisterRequest request) {
        authService.register(request, role);
    }
}
