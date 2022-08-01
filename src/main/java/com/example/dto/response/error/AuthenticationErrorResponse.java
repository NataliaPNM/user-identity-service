package com.example.dto.response.error;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class AuthenticationErrorResponse {

    @Schema(example="401")
    private String status;
    @Schema(example = "Unauthorized")
    private String error;
    @Schema(example="Unauthorized")
    private String message;
}
