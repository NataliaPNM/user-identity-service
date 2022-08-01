package com.example.dto.response.error;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class OperationNotAvailableErrorResponse {

    @Schema(example="403")
    private String status;
    @Schema(example = "Forbidden")
    private String error;
    @Schema(example="Your account is not verified, the operation is not available")
    private String message;

}
