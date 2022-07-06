package com.example.exception;

public class OperationNotAvailableException extends RuntimeException {
  public OperationNotAvailableException(String message) {
    super(message);
  }
}
