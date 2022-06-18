package com.example.exception;


  public class NotFoundSuchUserException extends RuntimeException {

    public NotFoundSuchUserException(String message) {
      super(message);
    }

  }
