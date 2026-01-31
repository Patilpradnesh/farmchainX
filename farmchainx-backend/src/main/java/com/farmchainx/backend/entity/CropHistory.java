package com.farmchainx.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.farmchainx.backend.enums.CropState;
import com.farmchainx.backend.enums.Role;

@Entity
@Table(name = "crop_history")
public class CropHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crop_id", nullable = false)
    private Crop crop;

    @Column(nullable = false)
    private String action; // e.g., "STATE_CHANGE"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CropState fromState;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CropState toState;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performed_by_id", nullable = false)
    private User performedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;

    // Constructor for immutability
    public CropHistory(Crop crop, String action, CropState fromState, CropState toState, User performedBy, Role role) {
        this.crop = crop;
        this.action = action;
        this.fromState = fromState;
        this.toState = toState;
        this.performedBy = performedBy;
        this.role = role;
    }

    // Getters only (no setters)

    public Long getId() { return id; }
    public Crop getCrop() { return crop; }
    public String getAction() { return action; }
    public CropState getFromState() { return fromState; }
    public CropState getToState() { return toState; }
    public User getPerformedBy() { return performedBy; }
    public Role getRole() { return role; }
    public LocalDateTime getTimestamp() { return timestamp; }

    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }
}
