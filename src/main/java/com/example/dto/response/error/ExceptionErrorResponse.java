package com.example.dto.response.error;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class ExceptionErrorResponse {

    @Schema(example="500")
    private String status;
    @Schema(example = "Internal Server Error")
    private String error;
    @Schema(example="Internal Server Error")
    private String message;
}
