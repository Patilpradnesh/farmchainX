package com.farmchainx.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "blockchain_records")
public class BlockchainRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String hash;
    private String data;
    private LocalDateTime createdAt;

    public BlockchainRecord() {}

    public BlockchainRecord(String hash, String data) {
        this.hash = hash;
        this.data = data;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getHash() { return hash; }
    public String getData() { return data; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
