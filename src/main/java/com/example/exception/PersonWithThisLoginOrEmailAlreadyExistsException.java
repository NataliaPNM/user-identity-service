package com.example.exception;

public class PersonWithThisLoginOrEmailAlreadyExistsException extends RuntimeException {

    public PersonWithThisLoginOrEmailAlreadyExistsException(String message) {
        super(message);
    }
}
