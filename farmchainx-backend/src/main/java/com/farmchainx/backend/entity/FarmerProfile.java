package com.farmchainx.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "farmer_profiles")
public class FarmerProfile extends User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

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
}
