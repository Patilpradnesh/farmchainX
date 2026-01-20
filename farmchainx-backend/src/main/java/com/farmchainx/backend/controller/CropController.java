package com.farmchainx.backend.controller;

import com.farmchainx.backend.entity.BlockchainRecord;
import com.farmchainx.backend.entity.Crop;
import com.farmchainx.backend.service.BlockchainService;
import com.farmchainx.backend.service.CropService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/crops")
public class CropController {

    private final CropService cropService;
    private final BlockchainService blockchainService;

    public CropController(CropService cropService, BlockchainService blockchainService) {
        this.cropService = cropService;
        this.blockchainService = blockchainService;
    }

    @PostMapping
    public Crop addCrop(@RequestBody Map<String, String> req, Authentication auth) {

        return cropService.addCrop(
                auth.getName(),                         // email from JWT
                req.get("cropName"),
                Integer.parseInt(req.get("quantity")),
                LocalDate.parse(req.get("harvestDate"))
        );
    }

    @GetMapping
    public List<Crop> getAllCrops() {
        return cropService.getAllCrops();
    }

    @GetMapping("/my")
    public List<Crop> getMyCrops(Authentication auth) {
        return cropService.getMyCrops(auth.getName());
    }

    @GetMapping("/{id}")
    public Crop getCrop(@PathVariable Long id) {
        return cropService.getCrop(id);
    }

    @PostMapping("/{id}/certificate")
    public String uploadCertificate(
            @PathVariable Long id,
            @RequestBody Map<String, String> req
    ) {
        cropService.uploadCertificate(id, req.get("path"));
        return "Certificate uploaded";
    }

    @GetMapping("/{id}/traceability")
    public BlockchainRecord trace(@PathVariable Long id) {
        Crop crop = cropService.getCrop(id);
        return blockchainService.getRecord(crop.getBlockchainHash());
    }
}
