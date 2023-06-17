package com.example.controller;

import com.example.dto.request.ChangePasswordRequest;
import com.example.dto.request.LoginRequest;
import com.example.dto.request.PasswordRecoveryRequest;
import com.example.dto.request.ResetPasswordRequest;
import com.example.dto.response.ChangePasswordResponseDto;
import com.example.dto.response.LoginResponseDto;
import com.example.dto.response.SetDefaultConfirmationTypeResponse;
import com.example.service.AccountService;
import com.example.service.AuthenticationService;
import com.example.util.JwtUtil;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.apache.http.HttpHeaders.AUTHORIZATION;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@RefreshScope
public class AuthController {

    private final AuthenticationService authenticationService;
    private final AccountService accountService;
    private final JwtUtil jwtUtil;

    @Operation(
            summary = "Authentication and authorization",
            description =
                    "Token is required for each request, "
                            + "except for cases of authorization/authentication "
                            + "and cases when the normal token has expired. The token format is Bearer.")
    @PostMapping("/signin")
    @ResponseStatus(HttpStatus.OK)
    public LoginResponseDto authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        return authenticationService.login(loginRequest);
    }

    @Operation(
            summary = "Getting a new pair of tokens",
            description =
                    "RefreshToken is required to get a new pair of tokens,"
                            + " because the lifetime of a regular token (for security purposes) is limited")
    @PostMapping("/secure")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ChangePasswordResponseDto> changeCredentials(
            @Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        var body = authenticationService.changePassword(changePasswordRequest);
        return ResponseEntity.status(body.getStatus()).body(body);
    }

    @Operation(
            summary = "Token validation",
            description = "This endpoint is used by api-gateway to check the validity of tokens")
    @PostMapping("/validateToken")
    public Boolean getResult(@RequestHeader(AUTHORIZATION) String token) {
        var header = token.split(" ");
        return jwtUtil.validateJwtToken(header[1]);
    }

    @Operation(
            summary = "Password recovery",
            description = "Sends a password reset link to the email with the token")
    @PostMapping("/passwordRecovery")
    public SetDefaultConfirmationTypeResponse passwordRecovery(
            @RequestBody PasswordRecoveryRequest login) {

        return accountService.recoverPassword(login);
    }

    @Operation(
            summary = "Setting a new password after reset",
            description = "Requires an authorization header with the token that was received in the link")
    @PostMapping("/resetPassword")
    public LoginResponseDto resetPassword(
            @RequestHeader(AUTHORIZATION) String token,
            @RequestBody ResetPasswordRequest resetPasswordRequest) {
        var header = token.split(" ");
        return accountService.resetPassword(header[1], resetPasswordRequest);
    }

    @GetMapping("/getPersonId")
    @Hidden
    public String getUserIdFromToken(@RequestHeader(AUTHORIZATION) String token) {
        var header = token.split(" ");
        return accountService.getPersonIdFromLogin(header[1]);
    }
}
