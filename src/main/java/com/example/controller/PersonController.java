package com.example.controller;

import com.example.dto.request.CreatePersonRequest;
import com.example.dto.request.SetDefaultNotificationTypeRequest;
import com.example.dto.response.CreatePersonResponseDto;
import com.example.dto.response.DefaultConfirmationTypeResponse;
import com.example.dto.response.SetDefaultConfirmationTypeResponse;
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

  @Operation(summary = "Получить дефолтный способ отправки кода подтверждения")
  @GetMapping("/defaultConfirmationType")
  public DefaultConfirmationTypeResponse getDefaultType(String personId) {

    return notificationSettingsService.getPersonDefaultNotificationType(UUID.fromString(personId));
  }

  @Operation(summary = "Изменить дефолтный способ отправки кода подтверждения")
  @PostMapping("/setDefaultConfirmationType")
  public SetDefaultConfirmationTypeResponse setDefaultType(
      @Valid @RequestBody SetDefaultNotificationTypeRequest setTypeRequestDto) {

    return notificationSettingsService.setPersonDefaultConfirmationType(setTypeRequestDto);
  }

  @Operation(summary = "Получить сводку по заблокированным способам отправки кода подтверждения")
  @GetMapping("/getConfirmationLocks")
  public Map<String, String> getPersonLocks(String personId) {

    return notificationSettingsService.getPersonConfirmationLocks(UUID.fromString(personId));
  }

  @Operation(summary = "Вернуть пользователя к изначальным настройкам")
  @GetMapping("/reset")
  public void resetPerson(String personId) {
    accountService.resetPerson(UUID.fromString(personId));
  }

  @Operation(summary = "Удалить пользователя")
  @GetMapping("/delete")
  public void deletePerson(String personId) {

    accountService.deletePerson(UUID.fromString(personId));
  }

  @Operation(summary = "Создать нового пользователя")
  @PostMapping("/create")
  public CreatePersonResponseDto createPerson(
      @RequestBody CreatePersonRequest createPersonRequest) {

    return accountService.createPerson(createPersonRequest);
  }
}
