package com.farmchainx.backend.controller;

import com.farmchainx.backend.common.dto.ApiResponse;
import com.farmchainx.backend.entity.Crop;
import com.farmchainx.backend.entity.User;
import com.farmchainx.backend.enums.CropState;
import com.farmchainx.backend.repository.CropRepository;
import com.farmchainx.backend.repository.UserRepository;
import com.farmchainx.backend.service.CropService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/marketplace")
public class MarketplaceController {

    private final CropRepository cropRepository;
    private final UserRepository userRepository;

    public MarketplaceController(CropRepository cropRepository, UserRepository userRepository) {
        this.cropRepository = cropRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/crops")
    public ResponseEntity<ApiResponse<Page<Crop>>> getAvailableCrops(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String cropName,
            @RequestParam(required = false) String location) {

        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ?
                       Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            // In a real implementation, you'd use Specification for complex queries
            Page<Crop> crops = cropRepository.findAll(pageable);

            return ResponseEntity.ok(ApiResponse.success("Crops retrieved", crops));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/crops/listed")
    public ResponseEntity<ApiResponse<List<Crop>>> getListedCrops() {
        try {
            List<Crop> listedCrops = cropRepository.findByCropState(CropState.LISTED);
            return ResponseEntity.ok(ApiResponse.success("Listed crops retrieved", listedCrops));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/crops/search")
    public ResponseEntity<ApiResponse<List<Crop>>> searchCrops(@RequestParam String query) {
        try {
            // Simple search implementation
            List<Crop> allCrops = cropRepository.findByCropState(CropState.LISTED);
            List<Crop> filteredCrops = allCrops.stream()
                    .filter(crop -> crop.getCropName().toLowerCase().contains(query.toLowerCase()) ||
                                   crop.getLocation().toLowerCase().contains(query.toLowerCase()))
                    .toList();
            return ResponseEntity.ok(ApiResponse.success("Crops searched", filteredCrops));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/crops/by-farmer/{farmerEmail}")
    public ResponseEntity<ApiResponse<List<Crop>>> getCropsByFarmer(@PathVariable String farmerEmail) {
        try {
            List<Crop> crops = cropRepository.findByCurrentOwnerEmail(farmerEmail);
            return ResponseEntity.ok(ApiResponse.success("Crops by farmer retrieved", crops));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/farmers")
    public ResponseEntity<ApiResponse<List<User>>> getActiveFarmers() {
        try {
            List<User> farmers = userRepository.findByRoleAndStatus(
                    com.farmchainx.backend.enums.Role.FARMER,
                    com.farmchainx.backend.enums.Status.APPROVED
            );
            return ResponseEntity.ok(ApiResponse.success("Active farmers retrieved", farmers));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMarketplaceStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalListedCrops", cropRepository.countByCropState(CropState.LISTED));
            stats.put("totalCreatedCrops", cropRepository.countByCropState(CropState.CREATED));
            stats.put("totalOrderedCrops", cropRepository.countByCropState(CropState.ORDERED));
            stats.put("totalActiveFarmers", userRepository.countByRoleAndStatus(
                    com.farmchainx.backend.enums.Role.FARMER,
                    com.farmchainx.backend.enums.Status.APPROVED
            ));
            stats.put("totalDistributors", userRepository.countByRole(com.farmchainx.backend.enums.Role.DISTRIBUTOR));
            stats.put("totalRetailers", userRepository.countByRole(com.farmchainx.backend.enums.Role.RETAILER));

            return ResponseEntity.ok(ApiResponse.success("Marketplace stats retrieved", stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/crops/{cropId}/list")
    public ResponseEntity<ApiResponse<String>> listCropForSale(@PathVariable Long cropId,
                                                               @RequestParam(required = false) Double price,
                                                               @RequestParam(required = false) String description) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        try {
            Crop crop = cropRepository.findById(cropId)
                    .orElseThrow(() -> new RuntimeException("Crop not found"));

            if (!crop.getCurrentOwner().getEmail().equals(email)) {
                return ResponseEntity.status(403).body(ApiResponse.error("Not authorized to list this crop"));
            }

            if (crop.getCropState() != CropState.CREATED) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Crop must be in CREATED state to list"));
            }

            // Update crop state to LISTED
            crop.setCropState(CropState.LISTED);
            cropRepository.save(crop);

            return ResponseEntity.ok(ApiResponse.success("Crop listed successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/crops/{cropId}/unlist")
    public ResponseEntity<ApiResponse<String>> unlistCrop(@PathVariable Long cropId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        try {
            Crop crop = cropRepository.findById(cropId)
                    .orElseThrow(() -> new RuntimeException("Crop not found"));

            if (!crop.getCurrentOwner().getEmail().equals(email)) {
                return ResponseEntity.status(403).body(ApiResponse.error("Not authorized to unlist this crop"));
            }

            if (crop.getCropState() != CropState.LISTED) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Crop is not currently listed"));
            }

            crop.setCropState(CropState.CREATED);
            cropRepository.save(crop);

            return ResponseEntity.ok(ApiResponse.success("Crop unlisted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/my-listings")
    public ResponseEntity<ApiResponse<List<Crop>>> getMyListings() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();

            List<Crop> myListings = cropRepository.findByCurrentOwnerEmail(email).stream()
                    .filter(crop -> crop.getCropState() == CropState.LISTED)
                    .toList();

            return ResponseEntity.ok(ApiResponse.success("My listings retrieved", myListings));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
