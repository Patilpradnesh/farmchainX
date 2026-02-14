package com.farmchainx.backend.controller;

import com.farmchainx.backend.common.dto.ApiResponse;
import com.farmchainx.backend.dto.LoginRequest;
import com.farmchainx.backend.dto.LoginResult;
import com.farmchainx.backend.dto.RegisterRequest;
import com.farmchainx.backend.dto.UserDto;
import com.farmchainx.backend.enums.Role;
import com.farmchainx.backend.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResult>> login(@Valid @RequestBody LoginRequest request) {
        try {
            LoginResult result = authService.loginWithUser(request);
            return ResponseEntity.ok(ApiResponse.success("Login successful", result));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/register/{role}")
    public ResponseEntity<ApiResponse<LoginResult>> register(@PathVariable String role, @Valid @RequestBody RegisterRequest request) {
        try {
            Role r = Role.valueOf(role.toUpperCase());
            LoginResult result = authService.register(request, r);
            return ResponseEntity.ok(ApiResponse.success("Registration successful", result));
        } catch (IllegalArgumentException iae) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Invalid role: " + role));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDto>> me() {
        try {
            UserDto user = authService.getCurrentUserDto();
            return ResponseEntity.ok(ApiResponse.success("User info retrieved", user));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/test")
    public ResponseEntity<ApiResponse<String>> test() {
        return ResponseEntity.ok(ApiResponse.success("Auth endpoints working"));
    }
}
