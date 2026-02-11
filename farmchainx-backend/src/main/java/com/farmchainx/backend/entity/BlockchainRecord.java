package com.farmchainx.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "blockchain_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlockchainRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String transactionHash;

    private Long cropId;

    private Long blockNumber;

    private String transactionType; // CROP_REGISTRATION, OWNERSHIP_TRANSFER, etc.

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
