package com.example.controller;

import com.example.dto.request.*;
import com.example.dto.response.ChangePasswordResponseDto;
import com.example.dto.response.LoginResponseDto;
import com.example.dto.response.SetDefaultConfirmationTypeResponse;
import com.example.dto.response.error.*;
import com.example.service.AccountService;
import com.example.service.AuthenticationService;
import com.example.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.io.FileNotFoundException;

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
                  "Token is required for each request, " +
                          "except for cases of authorization/authentication " +
                          "and cases when the normal token has expired. The token format is Bearer.")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200",
                  description = "JWT Access and Refresh tokens are created",
                  content = @Content(
                          mediaType = "application/json",
                          schema = @Schema(implementation = LoginResponseDto.class))),
          @ApiResponse(responseCode = "428",
                  description = "Precondition required. Login and password are correct. " +
                          "Account confirmation required (one-time password change).",
                  content = @Content(
                          mediaType = "application/json",
                          schema = @Schema(implementation = AccountConfirmationRequiredErrorResponse.class))),
          @ApiResponse(responseCode = "423",
                  description = "Account is temporary locked, with no attempts left " +
                          "after 5 attempts to enter correct password.",
                  content = @Content(
                          mediaType = "application/json",
                          schema = @Schema(implementation = PersonAccountLockedErrorResponse.class))),
          @ApiResponse(responseCode = "401",
                  description = "Bad credentials. Password is incorrect. " +
                          "You have 5 attempts to enter the correct password before being blocked.",
                  content = @Content(
                          mediaType = "application/json",
                          schema = @Schema(implementation = BadCredentialsErrorResponse.class))),
          @ApiResponse(responseCode = "422",
                  description = "Username not found in the database",
                  content = @Content(
                          mediaType = "application/json",
                          schema = @Schema(implementation = NotFoundErrorResponse.class)))
  })
  @PostMapping("/signin")
  @ResponseStatus(HttpStatus.OK)
  public LoginResponseDto authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
    return authenticationService.login(loginRequest);
  }

  @Operation(
          summary = "Getting a new pair of tokens",
          description =
                  "RefreshToken is required to get a new pair of tokens," +
                          " because the lifetime of a regular token (for security purposes) is limited")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200",
                  description = "Returns a new set of Access and Refresh tokens",
                  content = @Content(
                          mediaType = "application/json",
                          schema = @Schema(implementation = LoginResponseDto.class))),
          @ApiResponse(responseCode = "400",
                  description = "Invalid token. Refresh token can not be found in the database, " +
                          "or token is malformed.",
                  content = @Content(
                          mediaType = "application/json",
                          schema = @Schema(implementation = InvalidTokenErrorResponse.class)))
  })
  @PostMapping("/refresh")
  @ResponseStatus(HttpStatus.OK)
  public LoginResponseDto refreshToken(@Valid @RequestBody NewTokensRequest refreshToken) {
    return authenticationService.refreshJwt(refreshToken);
  }

  @Operation(
      summary = "Change Password",
      description = "Changing the one-time password or permanent password")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "406",
                  description = "The new password will be saved after confirmation by the code. " +
                          "You get the id of the operation to be confirmed. In this case, " +
                          "the operation to change the password.",
                  content = @Content(
                          mediaType = "application/json",
                          schema = @Schema(implementation = ChangePasswordResponseDto.class))),
          @ApiResponse(responseCode = "422",
                  description = "Username not found in the database",
                  content = @Content(
                          mediaType = "application/json",
                          schema = @Schema(implementation = NotFoundErrorResponse.class))),
          @ApiResponse(responseCode = "423",
                  description = "Error for the case when you try to access the endpoint with a password change, " +
                  "but your account is blocked. And since at this stage there are no tokens yet, " +
                  "this error will report that access to this endpoint is denied without authorization:\n " +
                  "unlockTime specific time until account is unlocked in ms. " +
                  "Or request to change the password was accepted, " +
                  "but the method of sending the code needs to be changed, " +
                  "since the default one is blocked",
                  content = @Content(
                          mediaType = "application/json",
                          schema = @Schema(implementation = PersonAccountLockedErrorResponse.class))),
  })
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
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200",
                  description = "Returns a string containing \"true\" or \"false\" " +
                          "regarding the validity of provided token",
                  content = @Content(
                          mediaType = "application/json",
                          schema = @Schema(implementation = Boolean.class)))
  })
  @PostMapping("/validateToken")
  public Boolean getResult(@RequestHeader(AUTHORIZATION) String token) {
    var header = token.split(" ");
    return jwtUtil.validateJwtToken(header[1]);
  }

  @Operation(
          summary = "Password recovery",
          description = "Sends a password reset link to the email with the token")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200",
                  description = "Returns the address to which the link was sent",
                  content = @Content(
                          mediaType = "application/json",
                          schema = @Schema(implementation = SetDefaultConfirmationTypeResponse.class))),
          @ApiResponse(responseCode = "422",
                  description = "Username not found in the database",
                  content = @Content(
                          mediaType = "application/json",
                          schema = @Schema(implementation = NotFoundErrorResponse.class))),
          @ApiResponse(responseCode = "423",
                  description = "Account is locked.",
                  content = @Content(
                          mediaType = "application/json",
                          schema = @Schema(implementation = PersonAccountLockedErrorResponse.class)))
  })
  @PostMapping("/passwordRecovery")
  public SetDefaultConfirmationTypeResponse passwordRecovery(
      @RequestBody PasswordRecoveryRequest login) throws FileNotFoundException {

    return accountService.recoverPassword(login);
  }

  @Operation(
          summary = "Setting a new password after reset",
          description = "Requires an authorization header with the token that was received in the link")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200",
                  description = "Returns \"true\" if password reset was successful",
                  content = @Content(
                          mediaType = "application/json",
                          schema = @Schema(implementation = Boolean.class))),
          @ApiResponse(responseCode = "400",
                  description = "Invalid token. Provided token is invalid or malformed.",
                  content = @Content(
                          mediaType = "application/json",
                          schema = @Schema(implementation = InvalidTokenErrorResponse.class))),
          @ApiResponse(responseCode = "422",
                  description = "Username not found in the database",
                  content = @Content(
                          mediaType = "application/json",
                          schema = @Schema(implementation = NotFoundErrorResponse.class))),
          @ApiResponse(responseCode = "423",
                  description = "Account is locked.",
                  content = @Content(
                          mediaType = "application/json",
                          schema = @Schema(implementation = PersonAccountLockedErrorResponse.class)))
  })
  @PostMapping("/resetPassword")
  public Boolean resetPassword(
      @RequestHeader(AUTHORIZATION) String token,
      @RequestBody ResetPasswordRequest resetPasswordRequest) {
    var header = token.split(" ");
    return accountService.resetPassword(header[1], resetPasswordRequest);
  }
}
