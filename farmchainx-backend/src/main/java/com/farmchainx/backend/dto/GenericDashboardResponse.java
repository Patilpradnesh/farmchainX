package com.farmchainx.backend.dto;

public class GenericDashboardResponse {

    private String message;

    public GenericDashboardResponse(String message) {
        this.message = message;
    }

    public String getMessage() { return message; }
}
