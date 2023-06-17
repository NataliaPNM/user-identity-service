package com.example.controller;

import com.example.dto.request.CreatePersonRequest;
import com.example.dto.request.EditResidentialAddressRequest;
import com.example.dto.request.SetDefaultNotificationTypeRequest;
import com.example.dto.response.CreatePersonResponseDto;
import com.example.dto.response.DefaultConfirmationTypeResponse;
import com.example.dto.response.PersonalDataResponseDto;
import com.example.dto.response.SetDefaultConfirmationTypeResponse;
import com.example.model.Address;
import com.example.service.AccountService;
import com.example.service.NotificationSettingsService;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(
            summary = "Get default confirmation type",
            description = "Returns the default way to send a confirmation code for a specific user")
    @GetMapping("/defaultConfirmationType")
    public DefaultConfirmationTypeResponse getDefaultType(String personId) {

        return notificationSettingsService.getPersonDefaultNotificationType(UUID.fromString(personId));
    }

    @Operation(
            summary = "Set default confirmation type",
            description = "Change the default way to send the confirmation code")
    @PostMapping("/setDefaultConfirmationType")
    public SetDefaultConfirmationTypeResponse setDefaultType(
            @Valid @RequestBody SetDefaultNotificationTypeRequest setTypeRequestDto) {

        return notificationSettingsService.setPersonDefaultConfirmationType(setTypeRequestDto);
    }

    @Operation(
            summary = "Get all blocked confirmation methods",
            description = "Get a summary of blocked methods for sending a confirmation code")
    @GetMapping("/getConfirmationLocks")
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

    @Operation(summary = "Get person`s personal data")
    @GetMapping("/personalData")
    public PersonalDataResponseDto getPersonPersonalData(@RequestParam String personId) {

        return accountService.getPersonPersonalData(personId);
    }

    @Operation(summary = "Edit person`s residential address")
    @PostMapping("/personalData/edit")
    public String editPersonPersonalData(
            @RequestBody EditResidentialAddressRequest editResidentialAddressRequest) {

        return accountService.editResidentialAddress(editResidentialAddressRequest);
    }

    @Operation(summary = "Get person`s personal data")
    @GetMapping("/personalData/residentialAddress")
    public Address getPersonResidentialAddress(@RequestParam String personId) {

        return accountService.getResidentialAddress(UUID.fromString(personId));
    }

    @Operation(summary = "Create a new user account")
    @PostMapping("/create")
    public CreatePersonResponseDto createPerson(
            @RequestBody CreatePersonRequest createPersonRequest) {

        return accountService.createPerson(createPersonRequest);
    }
}
