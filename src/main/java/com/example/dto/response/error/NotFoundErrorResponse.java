package com.example.dto.response.error;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class NotFoundErrorResponse {
    @Schema(example="422")
    private String status;
    @Schema(example = "Unprocessable Entity")
    private String error;
    @Schema(example="User with login can not be found. Login: RandomName")
    private String personId;
}
