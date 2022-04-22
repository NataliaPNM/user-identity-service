package com.example.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
@Data
public class ChangePasswordRequest {

    @NotBlank
    private String newPassword;
}
