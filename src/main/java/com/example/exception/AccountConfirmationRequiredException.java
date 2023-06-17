package com.example.exception;

public class AccountConfirmationRequiredException extends RuntimeException {

    public AccountConfirmationRequiredException(String message) {
        super(message);
    }
}
