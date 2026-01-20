package com.farmchainx.backend.service;

import com.farmchainx.backend.entity.*;
import com.farmchainx.backend.repository.*;
import com.farmchainx.backend.security.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final FarmerRepository farmerRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository,
                       FarmerRepository farmerRepository,
                       BCryptPasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.farmerRepository = farmerRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public void registerFarmer(String name,
                               String email,
                               String password,
                               String farmLocation,
                               String cropType) {

        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(Role.FARMER);
        user.setStatus(Status.PENDING);

        userRepository.save(user);

        Farmer farmer = new Farmer();
        farmer.setUser(user);
        farmer.setFarmLocation(farmLocation);
        farmer.setCropType(cropType);

        farmerRepository.save(farmer);
    }

    public String loginAndGetToken(String email, String rawPassword) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().name(),
                user.getStatus().name()
        );
    }

}
