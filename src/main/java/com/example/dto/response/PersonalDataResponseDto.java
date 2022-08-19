package com.example.dto.response;

import com.example.model.Address;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class PersonalDataResponseDto {
  @Schema(example = "Natalia")
  private String name;

  private UUID personId;

  @Schema(example = "Ponomareva")
  private String surname;

  @Schema(example = "Aleksandrovna")
  private String patronymic;

  @Schema(example = "14.08.1999")
  private String dateOfBirth;

  @Schema(example = "1234 43224")
  private String serialNumber;

  @Schema(example = "543")
  private String departmentCode;

  @Schema(example = "MVD")
  private String departmentIssued;

  @Schema(example = "89922098031")
  private String phone;

  @Schema(example = "ttnpnm@yandex.ru")
  private String email;

  private Address registrationAddress;
  private Address residentialAddress;
}
