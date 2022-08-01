package com.example.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordRequest {

  @Schema(example = "supersecurepassword")
  @NotBlank private String newPassword;
  @Schema(example = "d6d83746-d862-4562-99d4-4ec5a664a59a")
  @NotBlank private String personId;

}
