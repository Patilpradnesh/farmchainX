package com.farmchainx.backend.dto;

public class CropCreateResponse {

    private String hash;

    public CropCreateResponse(String hash) {
        this.hash = hash;
    }

    public String getHash() {
        return hash;
    }
}
