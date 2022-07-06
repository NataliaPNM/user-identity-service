package com.example.exception;

public class IncorrectDefaultNotificationTypeException extends RuntimeException {

  public IncorrectDefaultNotificationTypeException(String message) {
    super(message);
  }
}
