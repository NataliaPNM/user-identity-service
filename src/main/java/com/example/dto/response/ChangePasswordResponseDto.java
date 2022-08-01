package com.example.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class ChangePasswordResponseDto {

  @Schema(example="mihant91@gmail.com")
  private String contact;
  @Schema(example = "6223615f-0ecb-4258-909b-c924f1e14df5")
  private UUID operationId;
  @Schema(example = "This operation requires confirmation by code")
  private String message;
  @Schema(example = "NOT_ACCEPTABLE")
  private HttpStatus status;
  @Schema(example = "")
  private String unlockTime;

}
