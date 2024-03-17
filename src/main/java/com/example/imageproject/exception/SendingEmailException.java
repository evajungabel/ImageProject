package com.example.imageproject.exception;

public class SendingEmailException extends RuntimeException{

    private final String email;

    public SendingEmailException(String email) {
        this.email = email;
    }
}
