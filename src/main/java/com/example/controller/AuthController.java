package com.example.controller;

import com.example.service.AuthService;
import com.example.dto.ChangePasswordRequest;
import com.example.dto.LoginRequest;
import com.example.dto.LoginResponseDto;
import com.example.security.JwtUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

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
    @ResponseStatus(HttpStatus.OK)
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

