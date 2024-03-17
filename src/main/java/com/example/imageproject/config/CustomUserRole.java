package com.example.imageproject.domain;

public enum CustomUserRole {

    ROLE_GUEST("GUEST"),
    ROLE_USER("USER"),
    ROLE_ADMIN("ADMIN"),
    ROLE_AGENT("AGENT");

    private final String role;

    CustomUserRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}
