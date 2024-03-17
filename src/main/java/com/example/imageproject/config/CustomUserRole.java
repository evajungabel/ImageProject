package com.example.imageproject.config;

public enum CustomUserRole {

    ROLE_USER("USER"),
    ROLE_ADMIN("ADMIN");
    private final String role;

    CustomUserRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}
