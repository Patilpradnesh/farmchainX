package com.farmchainx.backend.controller;

import com.farmchainx.backend.dto.CropCreateRequest;
import com.farmchainx.backend.dto.CropCreateResponse;
import com.farmchainx.backend.dto.CropRequest;
import com.farmchainx.backend.dto.CropTraceResponse;
import com.farmchainx.backend.entity.Crop;
import com.farmchainx.backend.service.CropService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/crops")
public class CropController {

    private final CropService cropService;

    public CropController(CropService cropService) {
        this.cropService = cropService;
    }

    @PostMapping
    public CropCreateResponse registerCrop(@RequestBody CropCreateRequest request) {
        String hash = cropService.registerCrop(request);
        return new CropCreateResponse(hash);
    }

    @GetMapping("/trace/{hash}")
    public CropTraceResponse trace(@PathVariable String hash) {
        return cropService.traceCrop(hash);
    }
}
