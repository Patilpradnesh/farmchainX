package com.farmchainx.backend.service;

import com.farmchainx.backend.config.JwtUtil;
import com.farmchainx.backend.dto.LoginRequest;
import com.farmchainx.backend.dto.RegisterRequest;
import com.farmchainx.backend.entity.*;
import com.farmchainx.backend.enums.Role;
import com.farmchainx.backend.enums.Status;
import com.farmchainx.backend.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.farmchainx.backend.dto.LoginResult;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final FarmerProfileRepository farmerProfileRepository;
    private final DistributorProfileRepository distributorProfileRepository;
    private final RetailerProfileRepository retailerProfileRepository;
    private final ConsumerProfileRepository consumerProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(
            UserRepository userRepository,
            FarmerProfileRepository farmerProfileRepository,
            DistributorProfileRepository distributorProfileRepository,
            RetailerProfileRepository retailerProfileRepository,
            ConsumerProfileRepository consumerProfileRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil
    ) {
        this.userRepository = userRepository;
        this.farmerProfileRepository = farmerProfileRepository;
        this.distributorProfileRepository = distributorProfileRepository;
        this.retailerProfileRepository = retailerProfileRepository;
        this.consumerProfileRepository = consumerProfileRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Login → return RAW JWT string (frontend requirement)
     */
    public String login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return jwtUtil.generateToken(user);
    }

    public LoginResult loginWithUser(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user);

        return new LoginResult(token, user);
    }


    /**
     * Universal registration for all roles
     * All users start with PENDING status
     * Returns LoginResult with JWT token and user data
     */
    @Transactional
    public LoginResult register(RegisterRequest request, Role role) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);
        user.setStatus(Status.PENDING);

        userRepository.save(user);

        // Create role-specific profile
        switch (role) {
            case FARMER -> {
                FarmerProfile profile = new FarmerProfile();
                profile.setUser(user);
                profile.setFarmName(request.getName());
                profile.setLocation(request.getLocation());
                profile.setVerificationStatus(Status.PENDING);
                farmerProfileRepository.save(profile);
            }
            case DISTRIBUTOR -> {
                DistributorProfile profile = new DistributorProfile();
                profile.setUser(user);
                profile.setCompanyName(request.getName());
                distributorProfileRepository.save(profile);
            }
            case RETAILER -> {
                RetailerProfile profile = new RetailerProfile();
                profile.setUser(user);
                profile.setShopName(request.getName());
                profile.setLocation(request.getLocation());
                retailerProfileRepository.save(profile);
            }
            case CONSUMER -> {
                ConsumerProfile profile = new ConsumerProfile();
                profile.setUser(user);
                profile.setFullName(request.getName());
                consumerProfileRepository.save(profile);
            }
            case ADMIN -> {
                // Admin profile not required
            }
        }

        // Generate JWT token and return result
        String token = jwtUtil.generateToken(user);
        return new LoginResult(token, user);
    }
}
