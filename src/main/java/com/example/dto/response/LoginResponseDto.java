package com.example.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class LoginResponseDto {
  private String token;
  private String refreshToken;
  private int accessTokenExpirationTime;
  private int refreshTokenExpirationTime;
  private UUID personId;
}
