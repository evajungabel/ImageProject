package com.example.imageproject.exception;


public class UsernameExistsException extends RuntimeException {

    private final String username;

    public UsernameExistsException(String username) {
        super("Username exists with: " + username);
        this.username = username;
    }
}
