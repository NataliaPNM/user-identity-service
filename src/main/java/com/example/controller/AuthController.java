package com.example.controller;

import com.example.dto.request.*;
import com.example.dto.response.ChangePasswordResponseDto;
import com.example.dto.response.LoginResponseDto;
import com.example.dto.response.SetDefaultConfirmationTypeResponse;
import com.example.security.JwtUtils;
import com.example.service.AccountService;
import com.example.service.AuthService;
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

  private final AuthService authService;
  private final AccountService accountService;
  private final JwtUtils jwtUtils;

  @Operation(
      summary = "Authentication and authorization",
      description =
          "Token is required for each request, except for cases of authorization/authentication and cases when the normal token has expired."
              + "The token format is Bearer.")
  @PostMapping("/signin")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "428",
            description =
                "Login and password are correct. Account confirmation required (one-time password change)",
            content =
                @Content(
                    schema =
                        @Schema(
                            example =
                                "{\n"
                                    + "    \"status\", \"428\",\n"
                                    + "    \"error\", \"Precondition Required\",\n"
                                    + "    \"personId\", \"personUUID\"\n"
                                    + "}\n"))),
        @ApiResponse(
            responseCode = "200",
            description = "Account verified and authorization successful",
            content =
                @Content(
                    schema =
                        @Schema(
                            example =
                                "{\n"
                                    + "  \"token\": \"string\",\n"
                                    + "  \"refreshToken\": \"string\",\n"
                                    + "  \"accessTokenExpirationTime\": 0,\n"
                                    + "  \"refreshTokenExpirationTime\": 0,\n"
                                    + "  \"personId\": \"3fa85f64-5717-4562-b3fc-2c963f66afa6\"\n"
                                    + "}\n"))),
        @ApiResponse(
            responseCode = "422",
            description = "Invalid login",
            content =
                @Content(
                    schema =
                        @Schema(
                            example =
                                "{\n"
                                    + "    \"status\", \"422\",\n"
                                    + "    \"error\", \"Unprocessable Entity\",\n"
                                    + "    \"personId\", \"Not found person with this login\"\t\n"
                                    + "}\n"))),
        @ApiResponse(
            responseCode = "401",
            description = "Invalid password",
            content =
                @Content(
                    schema =
                        @Schema(
                            example =
                                "{\n"
                                    + "    \"status\", \"401\",\n"
                                    + "    \"path\", \"/auth/signin\",\n"
                                    + "    \"error\", \"Unauthorized error: ...\",\n"
                                    + "    \"remainingAttempts\", 4\n"
                                    + "}\n"))),
        @ApiResponse(
            responseCode = "423",
            description =
                "Password entered incorrectly 5 times. Authorization blocked\n"
                    + "unlockTime specific time until account is unlocked in ms",
            content =
                @Content(
                    schema =
                        @Schema(
                            example =
                                "{\n"
                                    + "    \"status\", \"423\",\n"
                                    + "    \"error\", \"Locked\",\n"
                                    + "    \"unlockTime\", \"time\"\n"
                                    + "}\n")))
      })
  @ResponseStatus(HttpStatus.OK)
  public LoginResponseDto authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
    return authService.login(loginRequest);
  }

  @Operation(
      summary = "Getting a new pair of tokens",
      description =
          "RefreshToken is required to get a new pair of tokens, because the lifetime of a regular token (for security purposes) is limited")
  @PostMapping("/refresh")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Refresh token is correct. Returns a new pair of tokens",
            content =
                @Content(
                    schema =
                        @Schema(
                            example =
                                "{\n"
                                    + "  \"token\": \"string\",\n"
                                    + "  \"refreshToken\": \"string\",\n"
                                    + "  \"accessTokenExpirationTime\": 0,\n"
                                    + "  \"refreshTokenExpirationTime\": 0\n"
                                    + "}\n"))),
        @ApiResponse(
            responseCode = "400",
            description = "Refresh token is not valid",
            content =
                @Content(
                    schema =
                        @Schema(
                            example =
                                "{\n"
                                    + "    \"status\", \"400\",\n"
                                    + "    \"error\", \"Bad Request\",\n"
                                    + "    \"message\", \"Invalid token\"\n"
                                    + "}\n")))
      })
  @ResponseStatus(HttpStatus.OK)
  public LoginResponseDto refreshToken(@Valid @RequestBody NewTokensRequest refreshToken) {
    return authService.refreshJwt(refreshToken);
  }

  @Operation(
      summary = "Change Password",
      description = "Changing the one-time password or permanent password")
  @PostMapping("/secure")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "406",
            description =
                "The new password will be saved after confirmation by the code. You get the id of the operation to be "
                    + "confirmed. In this case, the operation to change the password.",
            content =
                @Content(
                    schema =
                        @Schema(
                            example =
                                "{\n"
                                    + "    \"contact\": \"email@com\"\n"
                                    + "    \"operationId\": \"operationUUID\",\n"
                                    + "    \"message\": \" NOT_ACCEPTABLE \",\n"
                                    + "    \"status\": \"406\"\n"
                                    + "    \"unlockTime\": null\n"
                                    + "}\n"))),
        @ApiResponse(
            responseCode = "422",
            description = "Invalid user id",
            content =
                @Content(
                    schema =
                        @Schema(
                            example =
                                "{\n"
                                    + "    \"status\", \"422\",\n"
                                    + "    \"error\", \"Unprocessable Entity\",\n"
                                    + "    \"personId\", \"Not found person with this login\"\t\n"
                                    + "}\n"))),
        @ApiResponse(
            responseCode = "423",
            description =
                "Error for the case when you try to access the endpoint with a password change, but your account is blocked. And since at this stage there are no tokens yet, this error will report that access to this endpoint is denied without authorization\n"
                    + "unlockTime specific time until account is unlocked in ms. "
                    + "Or request to change the password was accepted, but the method of sending the code needs to be changed, since the default one is blocked",
            content =
                @Content(
                    schema =
                        @Schema(
                            example =
                                "{\n"
                                    + "    \"status\", \"423\",\n"
                                    + "    \"error\", \"Locked\",\n"
                                    + "    \"unlockTime\", \"time\"\n"
                                    + "}\n"
                                    + "{\n"
                                    + "    \"operationId\": \"operationUUID\",\n"
                                    + "    \"message\": \" Chose another confirmation type\",\n"
                                    + "    \"status\": \"423\",\n"
                                    + "   \"unlockTime\": \"time in ms\"\n"
                                    + "}\n")))
      })
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<ChangePasswordResponseDto> changeCredentials(
      @Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
    var body = authService.changePassword(changePasswordRequest);
    return ResponseEntity.status(body.getStatus()).body(body);
  }

  @Operation(
      summary = "Token validation",
      description = "This endpoint is used by api-gateway to check the validity of tokens")
  @PostMapping("/validateToken")
  public Boolean getResult(@RequestHeader(AUTHORIZATION) String token) {
    var header = token.split(" ");
    return jwtUtils.validateJwtToken(header[1]);
  }

  @Operation(
      summary = "Password recovery",
      description = "Sends a password reset link to the email with the token")
  @PostMapping("/passwordRecovery")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns the address to which the link was sent",
            content =
                @Content(
                    schema =
                        @Schema(example = "{\n" + "  \"personContact\": \"string\"\n" + "}\n"))),
        @ApiResponse(
            responseCode = "403",
            description = "Your account is not verified, the operation is not available",
            content =
                @Content(
                    schema =
                        @Schema(
                            example =
                                "{\n"
                                    + "    \"status\", \"403\",\n"
                                    + "    \"error\", \"Forbidden\",\n"
                                    + "    \"message\", \"Your account is not verified, the operation is not available\"\t\n"
                                    + "}\n"))),
        @ApiResponse(
            responseCode = "423",
            description = "Your account is locked",
            content =
                @Content(
                    schema =
                        @Schema(
                            example =
                                "{\n"
                                    + "    \"status\", \"423\",\n"
                                    + "    \"error\", \"Locked\",\n"
                                    + "    \"unlockTime\", \"time\"\n"
                                    + "}\n"))),
        @ApiResponse(
            responseCode = "422",
            description = "Not found person with this login",
            content =
                @Content(
                    schema =
                        @Schema(
                            example =
                                "{\n"
                                    + "    \"status\", \"422\",\n"
                                    + "    \"error\", \"Unprocessable Entity\",\n"
                                    + "    \"personId\", \"Not found person with this login\"\t\n"
                                    + "}\n")))
      })
  public SetDefaultConfirmationTypeResponse passwordRecovery(
      @RequestBody PasswordRecoveryRequest login) throws FileNotFoundException {

    return accountService.recoverPassword(login);
  }

  @Operation(
      summary = "Setting a new password after reset",
      description = "Requires an authorization header with the token that was received in the link")
  @PostMapping("/resetPassword")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Password changed",
            content = @Content(schema = @Schema(example = "true/false"))),
        @ApiResponse(
            responseCode = "400",
            description = "Token is not valid",
            content =
                @Content(
                    schema =
                        @Schema(
                            example =
                                "{\n"
                                    + "    \"status\", \"400\",\n"
                                    + "    \"error\", \"Bad Request\",\n"
                                    + "    \"message\", \"Invalid token\"\n"
                                    + "}\n"))),
        @ApiResponse(
            responseCode = "403",
            description = "Your account is not verified, the operation is not available",
            content =
                @Content(
                    schema =
                        @Schema(
                            example =
                                "{\n"
                                    + "    \"status\", \"403\",\n"
                                    + "    \"error\", \"Forbidden\",\n"
                                    + "    \"message\", \"Your account is not verified, the operation is not available\"\t\n"
                                    + "}\n"))),
        @ApiResponse(
            responseCode = "423",
            description = "Your account is locked",
            content =
                @Content(
                    schema =
                        @Schema(
                            example =
                                "{\n"
                                    + "    \"status\", \"423\",\n"
                                    + "    \"error\", \"Locked\",\n"
                                    + "    \"unlockTime\", \"time\"\n"
                                    + "}\n"))),
        @ApiResponse(
            responseCode = "422",
            description = "Not found person with this login",
            content =
                @Content(
                    schema =
                        @Schema(
                            example =
                                "{\n"
                                    + "    \"status\", \"422\",\n"
                                    + "    \"error\", \"Unprocessable Entity\",\n"
                                    + "    \"personId\", \"Not found person with this login\"\t\n"
                                    + "}\n")))
      })
  public Boolean resetPassword(
      @RequestHeader(AUTHORIZATION) String token,
      @RequestBody ResetPasswordRequest resetPasswordRequest) {
    var header = token.split(" ");
    return accountService.resetPassword(header[1], resetPasswordRequest);
  }
}
