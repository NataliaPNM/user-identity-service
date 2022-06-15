package com.example;

import com.example.dto.*;
import com.example.model.Credentials;
import com.example.model.Person;

import org.springframework.security.core.Authentication;

import java.util.UUID;

public class Fixtures {

  public static LoginRequest getLoginRequest(String login, String password) {
    return LoginRequest.builder().login(login).password(password).build();
  }

  public static PersonContactsDto getPersonContactsDto(String uuid, String email) {
    return PersonContactsDto.builder().id(UUID.fromString(uuid)).email(email).build();
  }

  public static PersonContactsRequestDto getPersonContactsRequestDto(String uuid) {
    PersonContactsRequestDto personContactsRequestDto = new PersonContactsRequestDto();
    personContactsRequestDto.setPersonId(uuid);
    return personContactsRequestDto;
  }

  public static String getChangePasswordResult() {
    return "Password changed!";
  }

  public static UUID getUUID() {
    return UUID.fromString("f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454");
  }

  public static Credentials getCredentials(
      String uuid, String login, String password, String refreshToken) {
    return Credentials.builder()
        .credentialsId(UUID.fromString(uuid))
        .login(login)
        .password(password)
        .refreshToken(refreshToken)
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
      int accessTokenExpirationTime) {
    return LoginResponseDto.builder()
        .token(token)
        .refreshToken(refreshToken)
        .refreshTokenExpirationTime(refreshTokenExpirationTime)
        .accessTokenExpirationTime(accessTokenExpirationTime)
        .build();
  }

  public static RequestNewTokensDto getRequestNewTokensDto(String refreshToken) {
    return RequestNewTokensDto.builder().refreshToken(refreshToken).build();
  }

  public static ChangePasswordRequest getChangePasswordRequest(String newPassword) {
    return ChangePasswordRequest.builder().newPassword(newPassword).build();
  }

  public static Person getPerson() {
    return Person.builder().build();
  }
}
