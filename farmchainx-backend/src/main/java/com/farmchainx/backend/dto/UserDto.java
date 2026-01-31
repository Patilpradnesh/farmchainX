package com.farmchainx.backend.dto;

import com.farmchainx.backend.enums.Role;
import com.farmchainx.backend.enums.Status;

public class UserDto {

    private Long id;
    private String email;
    private Role role;
    private Status status;

    public UserDto(Long id, String email, Role role, Status status) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.status = status;
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public Role getRole() { return role; }
    public Status getStatus() { return status; }
}
