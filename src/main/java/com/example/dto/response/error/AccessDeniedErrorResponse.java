package com.example.dto.response.error;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class AccessDeniedErrorResponse {

    @Schema(example="403")
    private String status;
    @Schema(example = "Access Denied")
    private String error;
    @Schema(example="Access Denied")
    private String message;
}
