package com.blog.eu.dto;

import com.blog.eu.model.Role;

public class UpdateUserRoleDTO {
    private Long userId;
    private Role role;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
