package com.example.service;

import com.example.dto.request.CreatePersonRequest;
import com.example.dto.request.PasswordRecoveryNotificationRequest;
import com.example.dto.request.PasswordRecoveryRequest;
import com.example.dto.request.ResetPasswordRequest;
import com.example.dto.response.CreatePersonResponseDto;
import com.example.dto.response.SetDefaultConfirmationTypeResponse;
import com.example.exception.*;
import com.example.model.*;
import com.example.repository.CredentialsRepository;
import com.example.repository.NotificationSettingsRepository;
import com.example.repository.PersonRepository;
import com.example.security.JwtUtils;
import io.micrometer.core.instrument.util.IOUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {
  private final PasswordEncoder passwordEncoder;
  private final CredentialsRepository credentialsRepository;
  private final PersonRepository personRepository;
  private final NotificationSettingsRepository notificationSettingsRepository;
  private final KafkaTemplate<String, PasswordRecoveryNotificationRequest> kafkaTemplate;
  private final JwtUtils jwtUtils;
  private final NotificationSettingsService notificationSettingsService;

  @Transactional(
      transactionManager = "kafkaTransactionManagerForPasswordRecoveryNeeds",
      propagation = Propagation.REQUIRED)
  public SetDefaultConfirmationTypeResponse recoverPassword(PasswordRecoveryRequest recoveryRequest)
      throws FileNotFoundException {
    var credentials =
        credentialsRepository
            .findByLogin(recoveryRequest.getLogin())
            .orElseThrow(() -> new NotFoundException("Not found person with this login"));
    var recoveryToken = jwtUtils.generateJwtToken(recoveryRequest.getLogin(), 300000);
    checkPersonAccount(credentials);

    var link =
        IOUtils.toString(new FileInputStream("src/main/java/com/example/password-recovery-link.html"));

    kafkaTemplate.send(
        "password-recovery",
        PasswordRecoveryNotificationRequest.builder()
            .email(credentials.getPerson().getEmail())
            .link(link)
            .build());
    return SetDefaultConfirmationTypeResponse.builder()
        .personContact(credentials.getPerson().getEmail())
        .build();
  }

  public void checkPersonAccount(Credentials credentials) {
    if (!credentials.isAccountVerified()) {
      throw new OperationNotAvailableException(
          "Your account is not verified, the operation is not available");
    } else if (credentials.isLock()) {
      throw new PersonAccountLockedException(
          notificationSettingsService.getUnlockTimeInMs(
              LocalDateTime.parse(credentials.getLockTime())));
    } else if (credentials.getPerson().getNotificationSettings().getEmailLock()) {
      throw new DefaultConfirmationTypeLockedException(
          notificationSettingsService.getUnlockTimeInMs(
              LocalDateTime.parse(
                  credentials.getPerson().getNotificationSettings().getEmailLockTime())));
    }
  }

  @Transactional
  public boolean resetPassword(String token, ResetPasswordRequest resetPasswordRequest) {

    if (jwtUtils.validateJwtToken(token)) {
      var login = jwtUtils.getLoginFromJwtToken(token);
      var credentials =
          credentialsRepository
              .findByLogin(login)
              .orElseThrow(() -> new NotFoundException("Not found person with this login"));
      checkPersonAccount(credentials);
      credentials.setPassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));
      credentialsRepository.save(credentials);
    } else {
      throw new InvalidTokenException("Token is not valid");
    }
    return true;
  }

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
    credentialsRepository
        .findByLogin(createPersonRequest.getLogin())
        .ifPresent(
            (c) -> {
              throw new PersonWithThisLoginOrEmailAlreadyExistsException(
                  "User with this login already exists");
            });

    personRepository
        .findByEmail(createPersonRequest.getEmail())
        .ifPresent(
            (c) -> {
              throw new PersonWithThisLoginOrEmailAlreadyExistsException(
                  "User with this email already exists");
            });
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
