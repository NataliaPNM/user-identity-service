package com.example.dto;

import lombok.Data;

@Data
public class LoginResponseDto {
    private String token;
    private String refreshToken;
}
