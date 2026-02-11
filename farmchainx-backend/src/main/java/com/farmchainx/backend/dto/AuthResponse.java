package com.farmchainx.backend.dto;

public class AuthResponse {

    private String token;
    private String role;
    private String status;
    private String email;
    private String name;

    public AuthResponse(String token, String role, String status) {
        this.token = token;
        this.role = role;
        this.status = status;
    }

    public AuthResponse(String token, String role, String status, String email, String name) {
        this.token = token;
        this.role = role;
        this.status = status;
        this.email = email;
        this.name = name;
    }

    public String getToken() { return token; }
    public String getRole() { return role; }
    public String getStatus() { return status; }
    public String getEmail() { return email; }
    public String getName() { return name; }
}