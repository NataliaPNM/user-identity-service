package com.example.exception.handler;

import com.example.exception.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class UserIdentityExceptionHandler extends ResponseEntityExceptionHandler {
  private static final String STATUS = "status";
  private static final String ERROR = "error";
  private static final String MESSAGE = "message";
  private static final String PERSON_ID = "personId";
  private final Map<String, String> body = new HashMap<>();

  @ExceptionHandler(value = {AccountConfirmationRequiredException.class})
  protected ResponseEntity<Object> handleAccountConfirmationRequiredException(
      AccountConfirmationRequiredException ex, WebRequest request) {
    body.clear();
    body.put(STATUS, "428");
    body.put(ERROR, "Precondition Required");
    body.put(PERSON_ID, ex.getMessage());
    return handleExceptionInternal(
        ex, body, new HttpHeaders(), HttpStatus.PRECONDITION_REQUIRED, request);
  }

  @ExceptionHandler(value = {NotFoundException.class})
  protected ResponseEntity<Object> handleNotFoundException(
      NotFoundException ex, WebRequest request) {
    body.clear();
    body.put(STATUS, "422");
    body.put(ERROR, "Unprocessable Entity");
    body.put(PERSON_ID, ex.getMessage());
    return handleExceptionInternal(
        ex, body, new HttpHeaders(), HttpStatus.UNPROCESSABLE_ENTITY, request);
  }

  @ExceptionHandler(value = {PersonAccountLockedException.class})
  protected ResponseEntity<Object> handlePersonAccountLockedException(
      PersonAccountLockedException ex, WebRequest request) {
    body.clear();
    body.put(STATUS, "423");
    body.put(ERROR, "Locked");
    body.put("unlockTime", ex.getMessage());
    return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.LOCKED, request);
  }

  @ExceptionHandler(value = {InvalidTokenException.class})
  protected ResponseEntity<Object> handleInvalidTokenException(
      InvalidTokenException ex, WebRequest request) {
    body.clear();
    body.put(STATUS, "400");
    body.put(ERROR, "Bad Request");
    body.put(MESSAGE, ex.getMessage());
    return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler(value = {IncorrectDefaultNotificationTypeException.class})
  protected ResponseEntity<Object> handleIncorrectDefaultNotificationTypeException(
      IncorrectDefaultNotificationTypeException ex, WebRequest request) {
    body.clear();
    body.put(STATUS, "400");
    body.put(ERROR, "Bad Request");
    body.put(MESSAGE, ex.getMessage());
    return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler(value = {DefaultConfirmationTypeLockedException.class})
  protected ResponseEntity<Object> handleDefaultConfirmationTypeLockedException(
      DefaultConfirmationTypeLockedException ex, WebRequest request) {
    body.clear();
    body.put(STATUS, "423");
    body.put(ERROR, "Locked");
    body.put("unlockTime", ex.getMessage());
    return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.LOCKED, request);
  }
}
