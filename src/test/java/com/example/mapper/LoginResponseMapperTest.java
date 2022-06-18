package com.example.mapper;

import com.example.dto.LoginResponseDto;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoginResponseMapperTest {

  private final LoginResponseMapper loginResponseMapper =
      Mappers.getMapper(LoginResponseMapper.class);

  @Test
  void mappingToLoanProgramDto() {
    var expectedLoginResponseDto =
        new LoginResponseDto("f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454", "ttnpnm@yandex.ru", 0, 0);

    var actualLoginResponse =
        loginResponseMapper.toLoginResponseDto(
            "f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454", "ttnpnm@yandex.ru", 0, 0);

    assertEquals(expectedLoginResponseDto, actualLoginResponse);
  }
}
