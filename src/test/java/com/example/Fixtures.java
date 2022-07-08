package com.example;

import com.example.dto.request.ChangePasswordRequest;
import com.example.dto.request.LoginRequest;
import com.example.dto.request.NewTokensRequest;
import com.example.dto.response.ChangePasswordResponseDto;
import com.example.dto.response.LoginResponseDto;
import com.example.model.Credentials;
import com.example.model.Person;
import com.example.model.PersonRole;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;

import java.util.UUID;

public class Fixtures {

  public static LoginRequest getLoginRequest(String login, String password) {
    return LoginRequest.builder().login(login).password(password).build();
  }

  public static ChangePasswordResponseDto getChangePasswordResult() {
    return ChangePasswordResponseDto.builder()
        .status(HttpStatus.PRECONDITION_REQUIRED)
        .message("This operation requires confirmation by code")
        .operationId(UUID.fromString("a05ed02c-fca0-4912-9773-6b13c31b772a"))
        .unlockTime("")
        .contact("mihant91@gmail.com")
        .build();
  }

  public static UUID getUUID() {
    return UUID.fromString("f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454");
  }

  public static Credentials getCredentials(
      String uuid,
      String login,
      String password,
      Boolean isAccountVerified,
      String refreshToken,
      String temporaryPassword,
      Person person,
      Boolean lock,
      String lockTime) {
    return Credentials.builder()
        .credentialsId(UUID.fromString(uuid))
        .login(login)
        .password(password)
        .isAccountVerified(isAccountVerified)
        .refreshToken(refreshToken)
        .person(person)
        .temporaryPassword(temporaryPassword)
        .lock(lock)
        .lockTime(lockTime)
        .build();
  }

  public static Authentication getAuthentication() {
    Authentication authentication = null;
    return authentication;
  }

  public static LoginResponseDto getLoginResponseDto(
      String token,
      String refreshToken,
      int refreshTokenExpirationTime,
      int accessTokenExpirationTime,
      UUID personId) {
    return LoginResponseDto.builder()
        .token(token)
        .refreshToken(refreshToken)
        .refreshTokenExpirationTime(refreshTokenExpirationTime)
        .personId(personId)
        .accessTokenExpirationTime(accessTokenExpirationTime)
        .build();
  }

  public static NewTokensRequest getRequestNewTokensDto(String refreshToken) {
    return NewTokensRequest.builder().refreshToken(refreshToken).build();
  }

  public static ChangePasswordRequest getChangePasswordRequest(
      String newPassword, String personId) {
    return ChangePasswordRequest.builder().personId(personId).newPassword(newPassword).build();
  }

  public static Person getPersonWithoutData() {
    return Person.builder().build();
  }

  public static Person getPerson(
      Long phone,
      String surname,
      String name,
      String personId,
      String patronymic,
      String email,
      PersonRole role) {
    return Person.builder()
        .phone(phone)
        .surname(surname)
        .name(name)
        .personId(UUID.fromString(personId))
        .patronymic(patronymic)
        .email(email)
        .role(role)
        .build();
  }
}
