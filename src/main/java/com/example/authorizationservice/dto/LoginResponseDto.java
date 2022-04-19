package com.example.authorizationservice.dto;

import lombok.Data;

@Data
public class LoginResponseDto {
    private String token;
    private String refreshToken;
}
