package com.example.exception;

public class PersonAccountLockedException extends RuntimeException {

    public PersonAccountLockedException(String message) {
        super(message);
    }
}
