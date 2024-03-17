package com.example.imageproject.exception;

import org.apache.tomcat.websocket.AuthenticationException;

public class AuthenticationExceptionImpl extends AuthenticationException {

    private final String username;
    public AuthenticationExceptionImpl(String username) {
        super("User was denied with username: " + username);
        this.username = username;
    }
}
