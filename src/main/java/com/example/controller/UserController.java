package com.example.controller;

import com.example.dto.UserContactsDto;
import com.example.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@RefreshScope
public class UserController {

    private final UserService userService;

    @Operation(summary = "Получение контактов пользователя",
            description = "Данный ендпоинт нужен для общения с другим микросервисом через веб-клиент, возможно будет удалён")
    @GetMapping("/contact")
    public UserContactsDto getUser(@RequestParam Long userId) {
        return userService.sendResult(userId);
    }


}
