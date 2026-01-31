package com.farmchainx.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class CropRequest {

    @NotBlank
    private String name;

    // Optional: path or URL to certificate file
    private String certificatePath;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCertificatePath() {
        return certificatePath;
    }

    public void setCertificatePath(String certificatePath) {
        this.certificatePath = certificatePath;
    }
}
