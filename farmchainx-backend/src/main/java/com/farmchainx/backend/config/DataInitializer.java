package com.farmchainx.backend.config;

import com.farmchainx.backend.entity.FarmerProfile;
import com.farmchainx.backend.entity.User;
import com.farmchainx.backend.enums.Role;
import com.farmchainx.backend.enums.Status;
import com.farmchainx.backend.repository.FarmerProfileRepository;
import com.farmchainx.backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final FarmerProfileRepository farmerProfileRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                          FarmerProfileRepository farmerProfileRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.farmerProfileRepository = farmerProfileRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            initializeDefaultUsers();
            System.out.println("🚀 FarmChainX Backend initialized successfully!");
        } catch (Exception e) {
            System.err.println("❌ Error during initialization: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initializeDefaultUsers() {
        // Create default admin user if not exists
        if (userRepository.findByEmail("admin@farmchainx.com").isEmpty()) {
            User admin = new User();
            admin.setEmail("admin@farmchainx.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            admin.setStatus(Status.APPROVED);
            userRepository.save(admin);
            System.out.println("✅ Default admin user created: admin@farmchainx.com / admin123");
        } else {
            System.out.println("ℹ️  Admin user already exists");
        }

        // Create test farmer if not exists
        createTestUser("farmer@farmchainx.com", "farmer123", Role.FARMER);

        // Create test distributor if not exists
        createTestUser("distributor@farmchainx.com", "distributor123", Role.DISTRIBUTOR);

        // Create test retailer if not exists
        createTestUser("retailer@farmchainx.com", "retailer123", Role.RETAILER);

        // Create test consumer if not exists
        createTestUser("consumer@farmchainx.com", "consumer123", Role.CONSUMER);
    }

    private void createTestUser(String email, String password, Role role) {
        if (userRepository.findByEmail(email).isEmpty()) {
            User user = new User();
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole(role);
            user.setStatus(Status.APPROVED);
            User savedUser = userRepository.save(user);

            // Create profile based on role
            createProfileForUser(savedUser, role);

            System.out.println("✅ Test " + role.name().toLowerCase() + " user created: " + email + " / " + password);
        } else {
            System.out.println("ℹ️  " + role.name().toLowerCase() + " user already exists: " + email);
        }
    }

    private void createProfileForUser(User user, Role role) {
        try {
            switch (role) {
                case FARMER -> {
                    if (farmerProfileRepository.findByUser(user).isEmpty()) {
                        FarmerProfile farmerProfile = new FarmerProfile();
                        farmerProfile.setUser(user);
                        farmerProfile.setVerificationStatus(Status.APPROVED);
                        farmerProfile.setFarmName("Test Farm");
                        farmerProfile.setLocation("Test Location");
                        farmerProfile.setLandArea(10.0);
                        farmerProfileRepository.save(farmerProfile);
                    }
                }
                case DISTRIBUTOR -> {
                    // Create distributor profile if needed
                    System.out.println("ℹ️  Distributor profile creation skipped for now");
                }
                case RETAILER -> {
                    // Create retailer profile if needed
                    System.out.println("ℹ️  Retailer profile creation skipped for now");
                }
                case CONSUMER -> {
                    // Create consumer profile if needed
                    System.out.println("ℹ️  Consumer profile creation skipped for now");
                }
                case ADMIN -> {
                    // Admin doesn't need profile
                }
            }
        } catch (Exception e) {
            System.err.println("⚠️  Could not create profile for " + role.name() + ": " + e.getMessage());
        }
    }
}
