package com.example.controller;

import com.example.dto.PersonContactsDto;
import com.example.dto.PersonContactsRequestDto;
import com.example.service.PersonService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@RefreshScope
public class PersonController {

  private final PersonService personService;

  @Operation(
      summary = "Получение контактов пользователя",
      description =
          "Данный ендпоинт нужен для общения с другим микросервисом через веб-клиент, возможно будет удалён")
  @PostMapping("/contact")
  public PersonContactsDto getPerson(@RequestBody PersonContactsRequestDto personId) {
    return personService.sendResult(personId);
  }
}
