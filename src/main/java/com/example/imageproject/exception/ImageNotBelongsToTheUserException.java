package com.example.imageproject.exception;

public class ImageNotBelongsToTheUserException extends Exception{

    private final String username;

    public ImageNotBelongsToTheUserException(String username) {
        super("Image not belongs to the user: " + username);
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
