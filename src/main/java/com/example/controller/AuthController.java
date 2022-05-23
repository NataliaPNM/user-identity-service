package com.example.controller;

import com.example.dto.UserDto;
import com.example.service.AuthService;
import com.example.dto.ChangePasswordRequest;
import com.example.dto.LoginRequest;
import com.example.dto.LoginResponseDto;
import com.example.security.JwtUser;
import com.example.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
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
    private final UserService userService;

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
    public LoginResponseDto refreshToken(@RequestHeader(AUTHORIZATION) String refreshToken) {
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
    @Operation(summary = "Not public",
            description = "Данный ендпоинт нужен для общения с другим микросервисом через веб-клиент, возможно будет удалён")
    @GetMapping("/users")
    public UserDto getUser(@RequestParam Long userId) {
       return userService.sendResult(userId);
    }


}

