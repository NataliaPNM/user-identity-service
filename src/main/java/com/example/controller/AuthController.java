package com.example.controller;

import com.example.dto.request.ChangePasswordRequest;
import com.example.dto.request.LoginRequest;
import com.example.dto.request.NewTokensRequest;
import com.example.dto.response.ChangePasswordResponseDto;
import com.example.dto.response.LoginResponseDto;
import com.example.security.JwtUtils;
import com.example.service.AccountService;
import com.example.service.AuthService;
import com.example.service.NotificationSettingsService;
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

  private final AuthService authService;
  private final AccountService accountService;
  private final JwtUtils jwtUtils;
  private final NotificationSettingsService notificationSettingsService;

  @Operation(
      summary = "Аутентификация и авторизация",
      description =
          "token необходим для каждого запроса, кроме случаев авторизации/аутентификации"
              + " и случаев, когда срок жизни обычного токена истёк.\n"
              + "Формат токена — Bearer.")
  @PostMapping("/signin")
  @ResponseStatus(HttpStatus.OK)
  public LoginResponseDto authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
    return authService.login(loginRequest);
  }

  @Operation(
      summary = "Получение новой пары токенов",
      description =
          "RefreshToken необходим для получения новой пары токенов, т.к. срок жизни обычного токена (в целях безопасности) ограничен")
  @PostMapping("/refresh")
  @ResponseStatus(HttpStatus.OK)
  public LoginResponseDto refreshToken(@Valid @RequestBody NewTokensRequest refreshToken) {
    return authService.refreshJwt(refreshToken);
  }

  @Operation(
      summary = "Изменение пароля",
      description = "Изменение одноразового пароля после первого входа")
  @PostMapping("/secure")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<ChangePasswordResponseDto> changeCredentials(
      @Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
    var body = authService.changePassword(changePasswordRequest);
    return ResponseEntity.status(body.getStatus()).body(body);
  }

  @Operation(
      summary = "Валидация токена",
      description = "Данный ендпоинт используется api-gateway для проверки валидности токенов")
  @PostMapping("/validateToken")
  public Boolean getResult(@RequestHeader(AUTHORIZATION) String token) {
    var header = token.split(" ");
    return jwtUtils.validateJwtToken(header[1]);
  }
}
