package com.farmchainx.backend.dto;

import com.farmchainx.backend.entity.User;

public class LoginResult {

    private String token;
    private User user;

    public LoginResult(String token, User user) {
        this.token = token;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public User getUser() {
        return user;
    }
}
