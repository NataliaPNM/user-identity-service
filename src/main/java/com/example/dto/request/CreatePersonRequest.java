package com.example.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreatePersonRequest {
  private String email;
  private Long phone;
  private String name;
  private String surname;
  private String patronymic;
  private String password;
  private String login;
}
