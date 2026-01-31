package com.farmchainx.backend.controller;

import com.farmchainx.backend.dto.CropCreateRequest;
import com.farmchainx.backend.dto.CropCreateResponse;
import com.farmchainx.backend.dto.CropRequest;
import com.farmchainx.backend.dto.CropTraceResponse;
import com.farmchainx.backend.entity.Crop;
import com.farmchainx.backend.service.CropService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/crops")
public class CropController {

    private final CropService cropService;

    public CropController(CropService cropService) {
        this.cropService = cropService;
    }

    @PostMapping("/register")
    public CropCreateResponse registerCrop(@RequestBody CropCreateRequest request) {
        Crop crop = cropService.registerCrop(request);
        return new CropCreateResponse(crop.getId(), crop.getBlockchainHash());
    }

    @PostMapping("/add")
    public ResponseEntity<Void> addCrop(@RequestBody CropRequest request, @RequestParam String farmerEmail) {
        cropService.addCrop(request, farmerEmail);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/trace/{hash}")
    public CropTraceResponse trace(@PathVariable String hash) {
        return cropService.traceCrop(hash);
    }
}
