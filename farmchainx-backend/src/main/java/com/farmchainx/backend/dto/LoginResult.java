package com.farmchainx.backend.dto;

import com.farmchainx.backend.entity.User;

public class LoginResult {

    private String token;
    private UserDto user;

    public LoginResult(String token, User user) {
        this.token = token;
        this.user = new UserDto(user.getId(), user.getEmail(), user.getRole(), user.getStatus());
    }

    public String getToken() {
        return token;
    }

    public UserDto getUser() {
        return user;
    }
}
