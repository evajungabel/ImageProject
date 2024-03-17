package com.example.imageproject.exception;

public class ImageNotFoundException extends RuntimeException{

    private final Long imageId;

    public ImageNotFoundException(Long imageId) {
        this.imageId = imageId;
    }
}
