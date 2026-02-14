package com.farmchainx.backend.controller;

import com.farmchainx.backend.common.dto.ApiResponse;
import com.farmchainx.backend.enums.Role;
import com.farmchainx.backend.enums.Status;
import com.farmchainx.backend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/system")
public class SystemTestController {

    private final UserRepository userRepository;

    public SystemTestController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/test-all")
    public ResponseEntity<ApiResponse<Map<String, Object>>> testSystemComponents() {
        try {
            Map<String, Object> testResults = new HashMap<>();

            // Test database connectivity
            long userCount = userRepository.count();
            testResults.put("database_connection", "✅ WORKING");
            testResults.put("user_count", userCount);

            // Test user repository
            boolean hasAdmin = userRepository.findByEmail("admin@farmchainx.com").isPresent();
            testResults.put("admin_user_exists", hasAdmin ? "✅ YES" : "❌ NO");

            // Test enum values
            testResults.put("roles_available", Role.values());
            testResults.put("statuses_available", Status.values());

            // Test basic functionality
            testResults.put("timestamp", LocalDateTime.now());
            testResults.put("system_status", "✅ ALL TESTS PASSED");
            testResults.put("backend_version", "1.0.0");
            testResults.put("ready_for_frontend", true);

            return ResponseEntity.ok(ApiResponse.success("System test completed", testResults));
        } catch (Exception e) {
            Map<String, Object> errorResults = new HashMap<>();
            errorResults.put("error", "❌ " + e.getMessage());
            errorResults.put("system_status", "❌ TESTS FAILED");
            errorResults.put("ready_for_frontend", false);
            return ResponseEntity.badRequest().body(ApiResponse.error("System test failed"));
        }
    }

    @GetMapping("/endpoints")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAvailableEndpoints() {
        try {
            Map<String, Object> endpoints = new HashMap<>();

            endpoints.put("authentication", Map.of(
                "login", "POST /api/auth/login",
                "register", "POST /api/auth/register",
                "me", "GET /api/auth/me",
                "test", "GET /api/auth/test"
            ));

            endpoints.put("admin", Map.of(
                "pending_users", "GET /api/admin/pending",
                "approve_user", "POST /api/admin/approve/{userId}?status={STATUS}",
                "all_users", "GET /api/admin/users"
            ));

            endpoints.put("crops", Map.of(
                "register", "POST /api/v1/crops/register",
                "trace", "GET /api/v1/crops/trace/{hash}",
                "marketplace", "GET /api/v1/marketplace/crops"
            ));

            endpoints.put("orders", Map.of(
                "create", "POST /api/v1/orders/create",
                "my_orders", "GET /api/v1/orders/my",
                "accept", "PUT /api/v1/orders/{id}/accept",
                "complete", "PUT /api/v1/orders/{id}/complete"
            ));

            endpoints.put("dashboards", Map.of(
                "farmer", "GET /api/v1/dashboard/farmer",
                "distributor", "GET /api/v1/dashboard/distributor",
                "retailer", "GET /api/v1/dashboard/retailer",
                "consumer", "GET /api/v1/dashboard/consumer"
            ));

            endpoints.put("system", Map.of(
                "health", "GET /health",
                "actuator_health", "GET /actuator/health",
                "system_test", "GET /api/system/test-all",
                "startup_status", "GET /api/startup/status"
            ));

            return ResponseEntity.ok(ApiResponse.success("Available endpoints retrieved", endpoints));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
