package com.farmchainx.backend.entity;
import com.farmchainx.backend.enums.Status;

import jakarta.persistence.*;

@Entity
@Table(name = "farmer_profiles")
public class FarmerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "id", unique = true)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status verificationStatus;


    private String farmName;
    private String location;
    private Double landArea;

    // ===== Getters & Setters =====

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getFarmName() {
        return farmName;
    }

    public String getLocation() {
        return location;
    }

    public Double getLandArea() {
        return landArea;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setFarmName(String farmName) {
        this.farmName = farmName;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setLandArea(Double landArea) {
        this.landArea = landArea;
    }

    public Status getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(Status verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

}
