package com.example.exception;

public class PersonAccountWasDeletedException extends RuntimeException {

    PersonAccountWasDeletedException(String message) {
        super(message);
    }
}
