package com.example.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

  @Schema(example = "superuser")
  @NotBlank private String login;
  @Schema(example = "superpassword")
  @NotBlank private String password;

}
