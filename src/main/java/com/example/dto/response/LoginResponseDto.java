package com.example.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class LoginResponseDto {
  @Schema(example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJwb3N0Z3JlcyIsImlhdCI6MTY1OTA4NTY2NSwiZXhwIjoxNjU5MDg2MjY1fQ.esGivP8dD4_2uPVsGByGCGzng0rvCF9SsGKV3TaXjzOX-FoJylz4h2ai4_J1OXZaBj52BDiE1isj7jFUlsW1UQ")
  private String token;
  @Schema(example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJwb3N0Z3JlcyIsImlhdCI6MTY1OTA4NTY2NSwiZXhwIjoxNjU5MDg2NDQ1fQ.tEo-pkf9ZV6qY0bZEiy8lWyPCTbuMkNf6emPj6C5eLXDZNs28gtbSyHlZ7HRQp-suteqmgQmxG7kfuQIVczeuQ")
  private String refreshToken;
  @Schema(example = "600000",type = "int")
  private int accessTokenExpirationTime;
  @Schema(example = "780000", type = "int")
  private int refreshTokenExpirationTime;
  @Schema(example = "d6d83746-d862-4562-99d4-4ec5a664a59a")
  private UUID personId;
}
