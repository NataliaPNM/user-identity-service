package com.example.mapper;

import com.example.dto.response.LoginResponseDto;
import org.mapstruct.Mapper;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface LoginResponseMapper {

  LoginResponseDto toLoginResponseDto(String token, String refreshToken, int accessTokenExpirationTime,
      int refreshTokenExpirationTime, UUID personId);
}
