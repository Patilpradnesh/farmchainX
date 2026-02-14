package com.farmchainx.backend.controller;

import com.farmchainx.backend.common.dto.ApiResponse;
import com.farmchainx.backend.dto.CropCreateRequest;
import com.farmchainx.backend.dto.CropCreateResponse;
import com.farmchainx.backend.dto.CropRequest;
import com.farmchainx.backend.dto.CropTraceResponse;
import com.farmchainx.backend.entity.Crop;
import com.farmchainx.backend.service.CropService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/crops")
public class CropController {

    private final CropService cropService;

    public CropController(CropService cropService) {
        this.cropService = cropService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<CropCreateResponse>> registerCrop(@Valid @RequestBody CropCreateRequest request) {
        System.out.println("🌾 CROP REGISTRATION REQUEST RECEIVED:");
        System.out.println("   Crop Name: " + request.getCropName());
        System.out.println("   Quantity: " + request.getQuantity());
        System.out.println("   Location: " + request.getLocation());
        System.out.println("   Harvest Date: " + request.getHarvestDate());

        try {
            Crop crop = cropService.registerCrop(request);
            System.out.println("✅ CROP REGISTRATION SUCCESS - ID: " + crop.getId());
            CropCreateResponse response = new CropCreateResponse(crop.getId(), crop.getBlockchainHash());
            return ResponseEntity.ok(ApiResponse.success("Crop registered successfully", response));
        } catch (RuntimeException e) {
            System.err.println("❌ CROP REGISTRATION RUNTIME ERROR: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            System.err.println("❌ CROP REGISTRATION ERROR: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(ApiResponse.error("Internal server error: " + e.getMessage()));
        }
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<Void>> addCrop(@RequestBody CropRequest request, @RequestParam String farmerEmail) {
        try {
            cropService.addCrop(request, farmerEmail);
            return ResponseEntity.ok(ApiResponse.success("Crop added successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/test")
    public ResponseEntity<ApiResponse<Map<String, String>>> testEndpoint() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth != null ? auth.getName() : "No authentication";

        Map<String, String> data = new HashMap<>();
        data.put("message", "Crop controller is working");
        data.put("timestamp", LocalDateTime.now().toString());
        data.put("authenticated_user", email);

        System.out.println("🧪 TEST ENDPOINT CALLED - User: " + email);
        return ResponseEntity.ok(ApiResponse.success("Test successful", data));
    }

    @GetMapping("/trace/{hash}")
    public ResponseEntity<ApiResponse<CropTraceResponse>> trace(@PathVariable String hash) {
        try {
            CropTraceResponse response = cropService.traceCrop(hash);
            return ResponseEntity.ok(ApiResponse.success("Crop traced successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
