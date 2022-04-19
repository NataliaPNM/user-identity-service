package com.example.authorizationservice.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
@Data
public class ChangePasswordRequest {

    @NotBlank
    private String newPassword;
}
