package com.example.imageproject.exception;

public class ContentTypeIsNull extends RuntimeException{

    private final String username;


    public ContentTypeIsNull(String username) {
        super(username);
        this.username = username;
    }
}
