package com.example.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class SetDefaultConfirmationTypeResponse {
    private String personContact;
}