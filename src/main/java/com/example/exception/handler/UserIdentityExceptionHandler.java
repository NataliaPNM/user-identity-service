package com.example.exception.handler;

import com.example.dto.response.error.*;
import com.example.exception.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.Access;
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
    AccountConfirmationRequiredErrorResponse response =
            AccountConfirmationRequiredErrorResponse.builder()
                    .status(String.valueOf(HttpStatus.PRECONDITION_REQUIRED.value()))
                    .error("Precondition Required")
                    .personId(ex.getMessage())
                    .build();
    return handleExceptionInternal(ex, response, new HttpHeaders(), HttpStatus.PRECONDITION_REQUIRED, request);
  }

  @ExceptionHandler(value = {NotFoundException.class})
  protected ResponseEntity<Object> handleNotFoundException(
      NotFoundException ex, WebRequest request) {
    NotFoundErrorResponse response = NotFoundErrorResponse.builder()
            .status(String.valueOf(HttpStatus.NOT_FOUND.value()))
            .error("Not found")
            .personId(ex.getMessage())
            .build();

    return handleExceptionInternal(
        ex, response, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
  }

  @ExceptionHandler(value = {PersonAccountLockedException.class})
  protected ResponseEntity<Object> handlePersonAccountLockedException(
      PersonAccountLockedException ex, WebRequest request) {
    PersonAccountLockedErrorResponse response =
            PersonAccountLockedErrorResponse.builder()
                    .status(String.valueOf(HttpStatus.LOCKED.value()))
                    .error("Locked")
                    .unlockTime(ex.getMessage())
                    .build();
    return handleExceptionInternal(ex, response, new HttpHeaders(), HttpStatus.LOCKED, request);
  }

  @ExceptionHandler(value = {InvalidTokenException.class})
  protected ResponseEntity<Object> handleInvalidTokenException(
      InvalidTokenException ex, WebRequest request) {
    InvalidTokenErrorResponse response =
            InvalidTokenErrorResponse.builder()
                    .status(String.valueOf(HttpStatus.BAD_REQUEST.value()))
                    .error("Bad Request")
                    .message("Invalid token")
                    .build();
    return handleExceptionInternal(ex, response, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler(value = {IncorrectDefaultNotificationTypeException.class})
  protected ResponseEntity<Object> handleIncorrectDefaultNotificationTypeException(
      IncorrectDefaultNotificationTypeException ex, WebRequest request) {
    IncorrectDefaultNotificationTypeErrorResponse response =
            IncorrectDefaultNotificationTypeErrorResponse.builder()
                    .status(String.valueOf(HttpStatus.BAD_REQUEST.value()))
                    .error("Bad Request")
                    .message(ex.getMessage())
                    .build();
    return handleExceptionInternal(ex, response, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler(value = {DefaultConfirmationTypeLockedException.class})
  protected ResponseEntity<Object> handleDefaultConfirmationTypeLockedException(
      DefaultConfirmationTypeLockedException ex, WebRequest request) {
    DefaultConfirmationTypeLockedErrorResponse response =
            DefaultConfirmationTypeLockedErrorResponse.builder()
                    .status(String.valueOf(HttpStatus.LOCKED.value()))
                    .error("Locked")
                    .unlockTime(ex.getMessage())
                    .build();
    return handleExceptionInternal(ex, response, new HttpHeaders(), HttpStatus.LOCKED, request);
  }

  @ExceptionHandler(value =  {BadCredentialsException.class})
  protected ResponseEntity<Object> handleBadCredentialsException(
         BadCredentialsException ex, WebRequest request) {
    BadCredentialsErrorResponse response =
            BadCredentialsErrorResponse.builder()
                    .status(String.valueOf(HttpStatus.UNAUTHORIZED.value()))
                    .error("Bad credentials")
                    .remainingAttempts(ex.getMessage())
                    .build();
    return handleExceptionInternal(ex, response, new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
  }

  @ExceptionHandler(value =  {AuthenticationException.class})
  protected ResponseEntity<Object> handleAuthenticationException(
          AuthenticationException ex, WebRequest request) {
    AuthenticationErrorResponse response =
            AuthenticationErrorResponse.builder()
                    .status(String.valueOf(HttpStatus.UNAUTHORIZED.value()))
                    .error("Unauthorized")
                    .message(ex.getMessage())
                    .build();
    return handleExceptionInternal(ex, response, new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
  }

  @ExceptionHandler(value =  {AccessDeniedException.class})
  protected ResponseEntity<Object> handleAuthenticationException(
          AccessDeniedException ex, WebRequest request) {
    AccessDeniedErrorResponse response =
            AccessDeniedErrorResponse.builder()
                    .status(String.valueOf(HttpStatus.FORBIDDEN.value()))
                    .error("Access Denied")
                    .message(ex.getMessage())
                    .build();

    return handleExceptionInternal(ex, response, new HttpHeaders(), HttpStatus.FORBIDDEN, request);
  }

  @ExceptionHandler(value = {Exception.class})
  public ResponseEntity<Object> handleAllExceptionsWithStackTrace(
          Exception ex, WebRequest request) {
    ex.printStackTrace();
    ExceptionErrorResponse response =
            ExceptionErrorResponse.builder()
                    .status(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                    .error("Internal Server Error")
                    .message(ex.getMessage())
                    .build();
    return handleExceptionInternal( ex, response, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
  }

  @ExceptionHandler(value = {PersonWithThisLoginOrEmailAlreadyExistsException.class})
  protected ResponseEntity<Object> handlePersonWithThisLoginOrEmailAlreadyExistsException(
          PersonWithThisLoginOrEmailAlreadyExistsException ex, WebRequest request) {
    PersonWithThisLoginOrEmailAlreadyExistsErrorResponse response =
            PersonWithThisLoginOrEmailAlreadyExistsErrorResponse.builder()
                    .status(String.valueOf(HttpStatus.CONFLICT.value()))
                    .error("Conflict")
                    .message(ex.getMessage())
                    .build();
    return handleExceptionInternal(ex, response, new HttpHeaders(), HttpStatus.CONFLICT, request);
  }

  @ExceptionHandler(value = {OperationNotAvailableException.class})
  protected ResponseEntity<Object> handleOperationNotAvailableException(
          OperationNotAvailableException ex, WebRequest request) {
    OperationNotAvailableErrorResponse response =
            OperationNotAvailableErrorResponse.builder()
                    .status(String.valueOf(HttpStatus.FORBIDDEN.value()))
                    .error("Forbidden")
                    .message(ex.getMessage())
                    .build();
    return handleExceptionInternal(ex, response, new HttpHeaders(), HttpStatus.FORBIDDEN, request);
  }

}
