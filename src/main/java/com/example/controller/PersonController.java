package com.example.controller;

import com.example.dto.request.CreatePersonRequest;
import com.example.dto.request.SetDefaultNotificationTypeRequest;
import com.example.dto.response.CreatePersonResponseDto;
import com.example.dto.response.DefaultConfirmationTypeResponse;
import com.example.dto.response.SetDefaultConfirmationTypeResponse;
import com.example.dto.response.error.DefaultConfirmationTypeLockedErrorResponse;
import com.example.dto.response.error.IncorrectDefaultNotificationTypeErrorResponse;
import com.example.dto.response.error.NotFoundErrorResponse;
import com.example.service.AccountService;
import com.example.service.NotificationSettingsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;
import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth/person")
@RequiredArgsConstructor
@RefreshScope
public class PersonController {
    private final AccountService accountService;
  private final NotificationSettingsService notificationSettingsService;

    @Operation(summary = "Get default confirmation type",
            description = "Returns the default way to send a confirmation code for a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "OK",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DefaultConfirmationTypeResponse.class))),
            @ApiResponse(responseCode = "422",
                    description = "Invalid user id",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = NotFoundErrorResponse.class)))
    })
    @GetMapping("/defaultConfirmationType")
    public DefaultConfirmationTypeResponse getDefaultType(String personId) {

        return notificationSettingsService.getPersonDefaultNotificationType(UUID.fromString(personId));
    }

  @Operation(summary = "Set default confirmation type",
  description = "Change the default way to send the confirmation code")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200",
                  description = "The default way to send a confirmation code has been changed. " +
                          "Returns E-Mail of a person the code has been sent to.",
                  content = @Content(
                          mediaType = "application/json",
                          schema = @Schema(implementation = SetDefaultConfirmationTypeResponse.class))),
          @ApiResponse(responseCode = "422",
                  description = "Invalid operation id",
                  content =
                  @Content(
                          mediaType = "application/json",
                          // "message": "Not found operation with this id"
                          schema = @Schema(implementation = NotFoundErrorResponse.class))),
          @ApiResponse(responseCode = "400",
                  description = "Wrong type of verification code sent",
                  content = @Content(
                          mediaType = "application/json",
                          schema = @Schema(implementation = IncorrectDefaultNotificationTypeErrorResponse.class))),
          @ApiResponse(responseCode = "423",
                  description = "The selected confirmation method is blocked. Choose another",
                  content = @Content(
                          mediaType = "application/json",
                          schema = @Schema(implementation = DefaultConfirmationTypeLockedErrorResponse.class))),
  })
  @PostMapping("/setDefaultConfirmationType")
  public SetDefaultConfirmationTypeResponse setDefaultType(
      @Valid @RequestBody SetDefaultNotificationTypeRequest setTypeRequestDto) {

    return notificationSettingsService.setPersonDefaultConfirmationType(setTypeRequestDto);
  }

  @Operation(summary = "Get all blocked confirmation methods",
  description = "Get a summary of blocked methods for sending a confirmation code")
  @GetMapping("/getConfirmationLocks")
  @ApiResponses(
          value = {
                  @ApiResponse(responseCode = "200",
                          description = "Returns a list with blocking statuses of ways to send code",
                          content =
                          @Content(
                                  mediaType = "application/json",
                                  schema = @Schema(implementation = Map.class,
                                          example =
                                                  "{\n"
                                                          + "    \"email\", \"423123\",\n"
                                                          + "    \"push\", \"12344\"\n"
                                                          + "}\n"))),
                  @ApiResponse(responseCode = "422",
                          description = "Invalid operation id",
                          content = @Content(
                                  mediaType = "application/json",
                                  // "message": "Not found operation with this id"
                                  schema = @Schema(implementation = NotFoundErrorResponse.class))),
          })
  public Map<String, String> getPersonLocks(String personId) {

    return notificationSettingsService.getPersonConfirmationLocks(UUID.fromString(personId));
  }

  @Operation(
      summary = "Reset user settings",
      description = "Resets the account status to unverified and removes all blocking")
  @GetMapping("/reset")
  public void resetPerson(String personId) {
    accountService.resetPerson(UUID.fromString(personId));
  }

  @Operation(summary = "Delete user account")
  @GetMapping("/delete")
  public void deletePerson(String personId) {

    accountService.deletePerson(UUID.fromString(personId));
  }

  @Operation(summary = "Create a new user account")
  @PostMapping("/create")
  public CreatePersonResponseDto createPerson(
      @RequestBody CreatePersonRequest createPersonRequest) {

    return accountService.createPerson(createPersonRequest);
  }
}
