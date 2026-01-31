package com.farmchainx.backend.service;

import com.farmchainx.backend.dto.CropCreateRequest;
import com.farmchainx.backend.dto.CropCreateResponse;
import com.farmchainx.backend.dto.CropRequest;
import com.farmchainx.backend.dto.CropTraceResponse;
import com.farmchainx.backend.entity.Crop;
import com.farmchainx.backend.entity.FarmerProfile;
import com.farmchainx.backend.entity.User;
import com.farmchainx.backend.enums.Role;
import com.farmchainx.backend.enums.Status;
import com.farmchainx.backend.repository.CropRepository;
import com.farmchainx.backend.repository.FarmerProfileRepository;
import com.farmchainx.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import com.farmchainx.backend.enums.CropState;
import com.farmchainx.backend.entity.CropHistory;
import com.farmchainx.backend.repository.CropHistoryRepository;

import java.util.List;
import java.util.UUID;

@Service
public class CropService {

    private final CropRepository cropRepository;
    private final UserRepository userRepository;
    private final FarmerProfileRepository farmerProfileRepository;
    private final CropHistoryRepository cropHistoryRepository;

    public CropService(
            CropRepository cropRepository,
            UserRepository userRepository,
            FarmerProfileRepository farmerProfileRepository,
            CropHistoryRepository cropHistoryRepository
    ) {
        this.cropRepository = cropRepository;
        this.userRepository = userRepository;
        this.farmerProfileRepository = farmerProfileRepository;
        this.cropHistoryRepository = cropHistoryRepository;
    }

    public void logCropHistory(Crop crop, String action, CropState fromState, CropState toState, User performedBy, Role role) {
        CropHistory history = new CropHistory(crop, action, fromState, toState, performedBy, role);
        cropHistoryRepository.save(history);
    }


    public void addCrop(CropRequest request, String farmerEmail) {
        User farmerUser = userRepository.findByEmail(farmerEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (farmerUser.getRole() != Role.FARMER || farmerUser.getStatus() != Status.APPROVED) {
            throw new RuntimeException("Only approved farmers can add crops");
        }

        FarmerProfile farmer = farmerProfileRepository.findByUser(farmerUser)
                .orElseThrow(() -> new RuntimeException("Farmer profile not found"));

        Crop crop = new Crop();
        crop.setCropName(request.getName());
        crop.setQuantity(0.0); // Assuming default or from request
        crop.setLocation(""); // Assuming default or from request
        crop.setHarvestDate(null); // Assuming default or from request
        crop.setCertificateRef(request.getCertificatePath());
        crop.setBlockchainHash(UUID.randomUUID().toString());
        crop.setCurrentOwner(farmerUser);
        crop.setCurrentOwnerRole(Role.FARMER);
        crop.setCropState(CropState.CREATED);
        cropRepository.save(crop);
        logCropHistory(crop, "CREATED", null, CropState.CREATED, farmerUser, Role.FARMER);
    }

    @Transactional
    public Crop registerCrop(CropCreateRequest request) {
        // 1) Extract logged-in user email from JWT
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        // 2) Load User entity
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));

        // 3) Load FarmerProfile
        FarmerProfile farmer = farmerProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Farmer profile not found for user: " + email));

        // 4) Build Crop entity
        Crop crop = new Crop();
        crop.setCropName(request.getCropName());
        crop.setQuantity(request.getQuantity());
        crop.setLocation(request.getLocation());
        crop.setHarvestDate(request.getHarvestDate());
        crop.setCropState(CropState.CREATED);
        crop.setCurrentOwner(user);
        crop.setCurrentOwnerRole(Role.FARMER);

        // 5) Generate blockchain hash
        String hash = UUID.randomUUID().toString().replace("-", "");
        crop.setBlockchainHash(hash);

        Crop savedCrop = cropRepository.save(crop);
        logCropHistory(savedCrop, "CREATED", null, CropState.CREATED, user, Role.FARMER);
        return savedCrop;
    }




    public CropTraceResponse traceCrop(String hash) {
        Crop crop = cropRepository.findByBlockchainHash(hash)
                .orElseThrow(() -> new RuntimeException("Crop not found"));

        List<CropHistory> history = cropHistoryRepository.findByCropIdOrderByTimestampAsc(crop.getId());

        return new CropTraceResponse(
                crop.getCropName(),
                crop.getBlockchainHash(),
                crop.getCropState().name(),
                crop.getCreatedAt().toString(),
                crop.getCurrentOwner().getEmail(),
                history.stream().map(h -> h.getAction() + " at " + h.getTimestamp()).toList()
        );
    }

}
