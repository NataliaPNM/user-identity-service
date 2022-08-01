package com.example.dto.response.error;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class DefaultConfirmationTypeLockedErrorResponse {

    @Schema(example="423")
    private String status;
    @Schema(example = "Locked")
    private String error;
    @Schema(example="3599927")
    private String unlockTime;
}
