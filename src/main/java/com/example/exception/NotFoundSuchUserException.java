package com.example.exception;

public class NotFoundSuchUserException extends RuntimeException {

  public NotFoundSuchUserException() {
    super("User with this login not found");
  }
}