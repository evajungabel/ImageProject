package com.example.imageproject.exception;


public class IOExceptionImpl extends RuntimeException {
    private final String username;


    public IOExceptionImpl(String username) {
        this.username = username;
    }
}
