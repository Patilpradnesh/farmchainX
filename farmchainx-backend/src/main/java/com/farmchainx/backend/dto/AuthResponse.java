package com.farmchainx.backend.dto;

public class AuthResponse {

    private String token;
    private String role;
    private String status;

    public AuthResponse(String token, String role, String status) {
        this.token = token;
        this.role = role;
        this.status = status;
    }

    public String getToken() {
        return token;
    }

    public String getRole() {
        return role;
    }

    public String getStatus() {
        return status;
    }
}
