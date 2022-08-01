package com.example.dto.response.error;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Builder
@Data
public class AccountConfirmationRequiredErrorResponse {

    @Schema(example="428")
    private String status;
    @Schema(example = "Precondition Required")
    private String error;
    @Schema(example="d6d83746-d862-4562-99d4-4ec5a664a59a")
    private String personId;

}
