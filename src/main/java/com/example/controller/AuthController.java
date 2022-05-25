package com.example.controller;

import com.example.dto.*;
import com.example.security.JwtUtils;
import com.example.service.AuthService;
import com.example.security.JwtUser;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@RefreshScope
public class AuthController {

    private final AuthService authService;
    private final JwtUtils jwtUtils;

    @Operation(summary = "Аутентификация и авторизация",
            description = "token необходим для каждого запроса, кроме случаев авторизации/аутентификации" +
                    " и случаев, когда срок жизни обычного токена истёк.\n" +
                    "Формат токена — Bearer.")
    @PostMapping("/signin")
    @ResponseStatus(HttpStatus.OK)
    public LoginResponseDto authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }

    @Operation(summary = "Получение новой пары токенов",
            description = "RefreshToken необходим для получения новой пары токенов, т.к. срок жизни обычного токена (в целях безопасности) ограничен")
    @PostMapping("/refresh")
    @ResponseStatus(HttpStatus.OK)
    public LoginResponseDto refreshToken(@Valid @RequestBody RequestNewTokensDto refreshToken) {
        return authService.refreshJwt(refreshToken);
    }

    @Operation(summary = "Изменение пароля",
            description = "Изменение одноразового пароля после первого входа")
    @PostMapping("/secure")
    @ResponseStatus(HttpStatus.OK)
    public String changeCredentials(@Valid @RequestBody ChangePasswordRequest changePasswordRequest,
                                    @AuthenticationPrincipal JwtUser user) {
        return authService.changePassword(changePasswordRequest, user.getId());
    }

    @Operation(summary = "Валидация токена",
            description = "Данный ендпоинт используется api-gateway для проверки валидности токенов")
    @PostMapping("/validateToken")
    public Boolean getResult(@RequestParam String token) {
        return jwtUtils.validateJwtToken(token);
    }
}

