package com.example;

import com.example.dto.*;
import com.example.model.Credentials;
import com.example.model.Person;
import com.example.security.JwtPerson;
import org.springframework.security.core.Authentication;

import java.util.UUID;

public class Fixtures {
  public static String token1 = "eyJhbGciOiJI";
  public static String token2 = "XOFsm1P2tSDo";
  public static String login = "postgres";
  public static String refreshToken1 = "UCkErJNzC0rcAd";
  public static String refreshToken2 = "P3yzy8a91ixRrB";

  public static LoginRequest getLoginRequest1() {
    return LoginRequest.builder().login("postgres").password("postgres").build();
  }

  public static PersonContactsDto getPersonContactsDto1() {
    return PersonContactsDto.builder()
        .id(UUID.fromString("f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454"))
        .email("tttn@yandex.ru")
        .phone(89922098031L)
        .build();
  }

  public static String getToken1() {
    return "ttt";
  }

  public static String getChangePasswordResult() {
    return "Password changed!";
  }

  public static UUID getUUID() {
    return UUID.fromString("f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454");
  }

  public static Credentials getCredentials1() {
    return Credentials.builder()
        .credentialsId(UUID.fromString("f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454"))
        .login("postgres")
        .password("postgres")
        .refreshToken("UCkErJNzC0rcAd")
        .build();
  }

  public static Credentials getCredentials2() {
    return Credentials.builder()
        .credentialsId(UUID.fromString("f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454"))
        .login("postgres")
        .password("postgres")
        .refreshToken("")
        .build();
  }

  public static Credentials getCredentials3() {
    return Credentials.builder()
        .credentialsId(UUID.fromString("f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454"))
        .login("postgres")
        .password("postgres")
        .refreshToken("P3yzy8a91ixRrB")
        .build();
  }

  public static Credentials getCredentials4() {
    return Credentials.builder()
        .credentialsId(UUID.fromString("f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454"))
        .login("postgres")
        .password("encodedPassword")
        .refreshToken("P3yzy8a91ixRrB")
        .build();
  }

  public static Authentication getAuthentication1() {
    Authentication authentication = null;
    return authentication;
  }

  public static LoginResponseDto getLoginResponseDto() {
    return LoginResponseDto.builder().token("eyJhbGciOiJI").refreshToken("UCkErJNzC0rcAd").build();
  }

  public static LoginResponseDto getLoginResponseDto2() {
    return LoginResponseDto.builder().token("XOFsm1P2tSDo").refreshToken("P3yzy8a91ixRrB").build();
  }

  public static RequestNewTokensDto getRequestNewTokensDto() {
    return RequestNewTokensDto.builder().refreshToken("UCkErJNzC0rcAd").build();
  }

  public static ChangePasswordRequest getChangePasswordRequest() {
    return ChangePasswordRequest.builder().newPassword("newPassword").build();
  }

  public static Person getPerson() {
    return Person.builder().build();
  }

  public static Credentials getCredentials() {
    return Credentials.builder().build();
  }

  public static JwtPerson getJwtPerson() {
    return JwtPerson.build(getPerson(), getCredentials());
  }
}
