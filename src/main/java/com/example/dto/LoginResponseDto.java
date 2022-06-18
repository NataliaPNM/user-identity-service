package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class LoginResponseDto {
  private String token;
  private String refreshToken;
  private int accessTokenExpirationTime;
  private int refreshTokenExpirationTime;
}
