package com.example.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class ChangePasswordResponseDto {
  private String contact;
  private UUID operationId;
  private String message;
  private HttpStatus status;
  private String unlockTime;
}
