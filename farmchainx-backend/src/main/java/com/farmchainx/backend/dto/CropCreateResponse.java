package com.farmchainx.backend.dto;

public class CropCreateResponse {

    private Long id;
    private String hash;

    public CropCreateResponse(Long id, String hash) {
        this.id = id;
        this.hash = hash;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getHash() { return hash; }
    public void setHash(String hash) { this.hash = hash; }
}
