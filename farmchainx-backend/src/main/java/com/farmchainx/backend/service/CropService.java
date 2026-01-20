package com.farmchainx.backend.service;

import com.farmchainx.backend.entity.Crop;
import com.farmchainx.backend.entity.Farmer;
import com.farmchainx.backend.entity.Role;
import com.farmchainx.backend.entity.User;
import com.farmchainx.backend.repository.CropRepository;
import com.farmchainx.backend.repository.FarmerRepository;
import com.farmchainx.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CropService {

    private final CropRepository cropRepository;
    private final FarmerRepository farmerRepository;
    private final UserRepository userRepository;
    private final BlockchainService blockchainService;

    public CropService(
            CropRepository cropRepository,
            FarmerRepository farmerRepository,
            UserRepository userRepository,
            BlockchainService blockchainService
    ) {
        this.cropRepository = cropRepository;
        this.farmerRepository = farmerRepository;
        this.userRepository = userRepository;
        this.blockchainService = blockchainService;
    }

    public Crop addCrop(String email, String cropName, int quantity, LocalDate harvestDate) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ✅ ENUM comparison (correct)
        if (user.getRole() != Role.FARMER) {
            throw new RuntimeException("Only farmers can add crops");
        }

        Farmer farmer = farmerRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Farmer profile not found"));

        Crop crop = new Crop();
        crop.setCropName(cropName);
        crop.setQuantity(quantity);
        crop.setHarvestDate(harvestDate);
        crop.setFarmer(farmer);

        // ✅ FIXED LINE (enum → string)
        String blockchainData =
                user.getEmail() + "|" +
                        user.getRole().name() + "|" +
                        cropName + "|" +
                        quantity + "|" +
                        harvestDate;

        String hash = blockchainService.registerOnBlockchain(blockchainData);
        crop.setBlockchainHash(hash);

        return cropRepository.save(crop);
    }

    public List<Crop> getAllCrops() {
        return cropRepository.findAll();
    }

    public List<Crop> getMyCrops(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Farmer farmer = farmerRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Farmer profile not found"));

        return cropRepository.findByFarmer(farmer);
    }

    public Crop getCrop(Long id) {
        return cropRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Crop not found"));
    }

    public void uploadCertificate(Long cropId, String path) {
        Crop crop = getCrop(cropId);
        crop.setCertificatePath(path);
        cropRepository.save(crop);
    }
}
