package com.farmchainx.backend.service;

import com.farmchainx.backend.entity.Farmer;
import com.farmchainx.backend.entity.User;
import com.farmchainx.backend.repository.FarmerRepository;
import com.farmchainx.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import com.farmchainx.backend.entity.Status;

@Service
public class FarmerService {

    private final FarmerRepository farmerRepository;
    private final UserRepository userRepository;

    public FarmerService(FarmerRepository farmerRepository,
                         UserRepository userRepository) {
        this.farmerRepository = farmerRepository;
        this.userRepository = userRepository;
    }

    public Farmer onboardFarmer(Long userId, String farmLocation, String cropType) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Farmer farmer = new Farmer();
        farmer.setUser(user);
        farmer.setFarmLocation(farmLocation);
        farmer.setCropType(cropType);

        return farmerRepository.save(farmer);
    }

    public void approveUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setStatus(Status.APPROVED);
        userRepository.save(user);
    }

}
