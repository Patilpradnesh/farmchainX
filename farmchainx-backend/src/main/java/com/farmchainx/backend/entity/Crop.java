package com.farmchainx.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.farmchainx.backend.enums.CropState;
import com.farmchainx.backend.enums.Role;

@Entity
@Table(name = "crops")
public class Crop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String cropName;

    @Column(nullable = false)
    private Double quantity;

    @Column(nullable = false)
    private LocalDateTime harvestDate;

    @Column(nullable = false)
    private String location;

    private String certificateRef; // IPFS-ready

    @Column(nullable = false, unique = true)
    private String blockchainHash;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_owner_id", nullable = false)
    private User currentOwner;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role currentOwnerRole;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CropState cropState;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Constructors, getters, setters


    public Crop() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCropName() { return cropName; }
    public void setCropName(String cropName) { this.cropName = cropName; }

    public Double getQuantity() { return quantity; }
    public void setQuantity(Double quantity) { this.quantity = quantity; }

    public LocalDateTime getHarvestDate() { return harvestDate; }
    public void setHarvestDate(LocalDateTime harvestDate) { this.harvestDate = harvestDate; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getCertificateRef() { return certificateRef; }
    public void setCertificateRef(String certificateRef) { this.certificateRef = certificateRef; }

    public String getBlockchainHash() { return blockchainHash; }
    public void setBlockchainHash(String blockchainHash) { this.blockchainHash = blockchainHash; }

    public User getCurrentOwner() { return currentOwner; }
    public void setCurrentOwner(User currentOwner) { this.currentOwner = currentOwner; }

    public Role getCurrentOwnerRole() { return currentOwnerRole; }
    public void setCurrentOwnerRole(Role currentOwnerRole) { this.currentOwnerRole = currentOwnerRole; }

    public CropState getCropState() { return cropState; }
    public void setCropState(CropState cropState) { this.cropState = cropState; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public void transitionTo(CropState newState, User performedBy) {
        if (!isValidTransition(newState)) {
            throw new IllegalStateException("Invalid transition from " + cropState + " to " + newState);
        }
        // History logging handled in service
        this.cropState = newState;
    }

    private boolean isValidTransition(CropState newState) {
        switch (cropState) {
            case CREATED: return newState == CropState.LISTED;
            case LISTED: return newState == CropState.ORDERED;
            case ORDERED: return newState == CropState.SHIPPED;
            case SHIPPED: return newState == CropState.DELIVERED;
            case DELIVERED: return newState == CropState.CLOSED;
            default: return false;
        }
    }
}
