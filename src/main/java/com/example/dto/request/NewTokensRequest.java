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
public class NewTokensRequest {

  @Schema(example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJwb3N0Z3JlcyIsImlhdCI6MTY1OTA4NTY2NSwiZXhwIjoxNjU5MDg2NDQ1fQ.tEo-pkf9ZV6qY0bZEiy8lWyPCTbuMkNf6emPj6C5eLXDZNs28gtbSyHlZ7HRQp-suteqmgQmxG7kfuQIVczeuQ")
  @NotBlank private String refreshToken;

}
