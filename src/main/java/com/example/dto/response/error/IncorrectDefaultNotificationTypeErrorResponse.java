package com.example.dto.response.error;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class IncorrectDefaultNotificationTypeErrorResponse {

    @Schema(example="400")
    private String status;
    @Schema(example = "Bad Request")
    private String error;
    @Schema(example = "Incorrect confirmation type")
    private String message;

}
