package com.example.service;

import com.example.dto.request.CreatePersonRequest;
import com.example.dto.response.CreatePersonResponseDto;
import com.example.exception.NotFoundException;
import com.example.model.*;
import com.example.repository.CredentialsRepository;
import com.example.repository.NotificationSettingsRepository;
import com.example.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {
  private final PasswordEncoder passwordEncoder;
  private final CredentialsRepository credentialsRepository;
  private final PersonRepository personRepository;
  private final NotificationSettingsRepository notificationSettingsRepository;

  public void resetPerson(UUID personId) {
    var credentials =
        credentialsRepository
            .findByPersonId(personId)
            .orElseThrow(() -> new NotFoundException("Not found such person"));
    credentials.setAccountVerified(false);
    credentials.setLock(false);
    credentials.setLockTime("");
    credentials.setTemporaryPassword("");
    credentials.setRefreshToken("");
    credentials.getPerson().getNotificationSettings().setPushLock(false);
    credentials.getPerson().getNotificationSettings().setEmailLock(false);
    credentials.getPerson().getNotificationSettings().setConfirmationLock(ConfirmationLock.NONE);
    credentials.getPerson().getNotificationSettings().setEmailLockTime("");
    credentials.getPerson().getNotificationSettings().setPushLockTime("");
    credentialsRepository.save(credentials);
  }

  public void deletePerson(UUID personId) {
    var credentials =
        credentialsRepository
            .findByPersonId(personId)
            .orElseThrow(() -> new NotFoundException("Not found such person"));
    credentialsRepository.delete(credentials);
  }

  public CreatePersonResponseDto createPerson(CreatePersonRequest createPersonRequest) {
    NotificationSettings notificationSettings =
        NotificationSettings.builder()
            .confirmationLock(ConfirmationLock.NONE)
            .defaultTypeOfConfirmation("email")
            .emailLock(false)
            .pushLock(false)
            .pushLockTime("")
            .emailLockTime("")
            .build();
    notificationSettingsRepository.save(notificationSettings);
    Person newPerson =
        Person.builder()
            .role(PersonRole.ROLE_USER)
            .name(createPersonRequest.getName())
            .email(createPersonRequest.getEmail())
            .patronymic(createPersonRequest.getPatronymic())
            .surname(createPersonRequest.getSurname())
            .phone(createPersonRequest.getPhone())
            .notificationSettings(notificationSettings)
            .build();
    personRepository.save(newPerson);
    Credentials credentials =
        Credentials.builder()
            .temporaryPassword("")
            .password(passwordEncoder.encode(createPersonRequest.getPassword()))
            .isAccountVerified(false)
            .lockTime("")
            .person(newPerson)
            .lock(false)
            .login(createPersonRequest.getLogin())
            .refreshToken("")
            .build();
    credentialsRepository.save(credentials);
    return CreatePersonResponseDto.builder()
        .personId(newPerson.getPersonId())
        .login(createPersonRequest.getLogin())
        .password(createPersonRequest.getPassword())
        .build();
  }
}
