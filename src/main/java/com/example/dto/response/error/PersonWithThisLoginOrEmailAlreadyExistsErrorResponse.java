package com.example.dto.response.error;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class PersonWithThisLoginOrEmailAlreadyExistsErrorResponse {

    @Schema(example="409")
    private String status;
    @Schema(example = "Conflict")
    private String error;
    @Schema(example="User with this login already exists")
    private String message;

}
