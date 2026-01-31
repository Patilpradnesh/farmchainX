package com.farmchainx.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.farmchainx.backend.enums.OrderState;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crop_id", nullable = false)
    private Crop crop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderState orderState;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Constructors, getters, setters

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void transitionTo(OrderState newState, User performedBy) {
        if (!isValidTransition(newState)) {
            throw new IllegalStateException("Invalid transition from " + orderState + " to " + newState);
        }
        this.orderState = newState;
    }

    private boolean isValidTransition(OrderState newState) {
        switch (orderState) {
            case PLACED: return newState == OrderState.ACCEPTED || newState == OrderState.CANCELLED;
            case ACCEPTED: return newState == OrderState.SHIPPED;
            case SHIPPED: return newState == OrderState.COMPLETED;
            default: return false;
        }
    }
}
