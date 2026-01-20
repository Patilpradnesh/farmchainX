package com.farmchainx.backend.service;

import com.farmchainx.backend.entity.Farmer;
import com.farmchainx.backend.entity.Status;
import com.farmchainx.backend.entity.User;
import com.farmchainx.backend.repository.FarmerRepository;
import com.farmchainx.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FarmerService {

    private final FarmerRepository farmerRepository;
    private final UserRepository userRepository;

    public FarmerService(FarmerRepository farmerRepository,
                         UserRepository userRepository) {
        this.farmerRepository = farmerRepository;
        this.userRepository = userRepository;
    }

    // =========================
    // FARMER ONBOARDING
    // =========================
    public Farmer onboardFarmer(Long userId, String farmLocation, String cropType) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Farmer farmer = new Farmer();
        farmer.setUser(user);
        farmer.setFarmLocation(farmLocation);
        farmer.setCropType(cropType);

        return farmerRepository.save(farmer);
    }

    // =========================
    // ADMIN OPERATIONS
    // =========================

    // 1️⃣ View all farmers
    public List<Farmer> getAllFarmers() {
        return farmerRepository.findAll();
    }

    // 2️⃣ View only pending farmers
    public List<User> getPendingFarmers() {
        return userRepository.findByRoleAndStatus("FARMER", Status.PENDING);
    }

    // 3️⃣ Approve farmer
    public void approveUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setStatus(Status.APPROVED);
        userRepository.save(user);
    }

    // 4️⃣ Reject farmer
    public void rejectUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setStatus(Status.REJECTED);
        userRepository.save(user);
    }
}
