package com.farmchainx.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "retailer_profiles")
public class RetailerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    private String shopName;
    private String location;

    // ===== Getters & Setters =====

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getShopName() {
        return shopName;
    }

    public String getLocation() {
        return location;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
