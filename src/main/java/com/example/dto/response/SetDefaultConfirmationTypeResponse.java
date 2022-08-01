package com.example.dto.response;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class SetDefaultConfirmationTypeResponse {

    @Schema(example = "mihant91@gmail.com")
    private String personContact;

}