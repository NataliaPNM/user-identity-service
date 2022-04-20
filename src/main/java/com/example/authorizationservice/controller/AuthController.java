package com.example.authorizationservice.controller;

import com.example.authorizationservice.AuthService.AuthService;
import com.example.authorizationservice.dto.ChangePasswordRequest;
import com.example.authorizationservice.dto.LoginRequest;
import com.example.authorizationservice.dto.LoginResponseDto;
import com.example.authorizationservice.security.JwtUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signin")
    @ResponseStatus(HttpStatus.OK)
    public LoginResponseDto authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }

    @PostMapping("/refresh")
    public LoginResponseDto refreshToken(@RequestHeader(AUTHORIZATION) String refreshToken) {
        return authService.refreshJwt(refreshToken);
    }

    @PostMapping("/secure")
    @ResponseStatus(HttpStatus.OK)
    public void changeCredentials(@Valid @RequestBody ChangePasswordRequest changePasswordRequest,
                                  @AuthenticationPrincipal JwtUser user) {
        authService.changePassword(changePasswordRequest, user.getId());
    }

}

