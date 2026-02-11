package com.farmchainx.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import com.farmchainx.backend.enums.CropState;
import com.farmchainx.backend.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "crops")
public class Crop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Crop name is required")
    private String cropName;

    @Column(nullable = false)
    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    private Double quantity;

    @Column(nullable = false)
    @NotNull(message = "Harvest date is required")
    private LocalDateTime harvestDate;

    @Column(nullable = false)
    @NotBlank(message = "Location is required")
    private String location;

    private String certificateRef; // IPFS-ready

    @Column(nullable = false, unique = true)
    private String blockchainHash;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_owner_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password"})
    private User currentOwner;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role currentOwnerRole;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CropState cropState;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Version
    private Long version;




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

    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public Long getVersion() { return version; }


    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
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
            case LISTED: return newState == CropState.ORDERED || newState == CropState.CREATED; // Allow unlisting
            case ORDERED: return newState == CropState.SHIPPED || newState == CropState.LISTED; // Allow cancellation
            case SHIPPED: return newState == CropState.DELIVERED;
            case DELIVERED: return newState == CropState.CLOSED;
            case CLOSED: return false; // Terminal state
            default: return false;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Crop)) return false;
        Crop crop = (Crop) o;
        return id != null && id.equals(crop.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Crop{" +
                "id=" + id +
                ", cropName='" + cropName + '\'' +
                ", quantity=" + quantity +
                ", cropState=" + cropState +
                ", currentOwner=" + (currentOwner != null ? currentOwner.getId() : null) +
                ", createdAt=" + createdAt +
                '}';
    }
}
