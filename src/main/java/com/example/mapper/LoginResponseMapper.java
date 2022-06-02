package com.example.mapper;

import com.example.dto.LoginResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LoginResponseMapper {
  LoginResponseDto toLoginResponseDto(String token, String refreshToken);
}
