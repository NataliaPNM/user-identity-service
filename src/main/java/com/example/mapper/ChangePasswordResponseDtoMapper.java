package com.example.mapper;

import com.example.dto.response.ChangePasswordResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.http.HttpStatus;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ChangePasswordResponseDtoMapper {
  @Mapping(target = "status", source = "status")
  @Mapping(target = "contact", source = "contact")
  @Mapping(target = "operationId", source = "operationId")
  @Mapping(target = "message", source = "message")
  @Mapping(target = "unlockTime", source = "lockTime")
  ChangePasswordResponseDto toChangePasswordResponseConfirmationLockedDto(
      HttpStatus status, String contact, UUID operationId, String message, String lockTime);

  @Mapping(target = "status", source = "status")
  @Mapping(target = "contact", source = "contact")
  @Mapping(target = "operationId", source = "operationId")
  @Mapping(target = "message", source = "message")
  ChangePasswordResponseDto toChangePasswordResponsePreconditionRequiredDto(
      HttpStatus status, String contact, UUID operationId, String message);
}
