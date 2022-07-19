package com.example.service;

import com.example.dto.request.ChangePasswordRequest;
import com.example.dto.request.CreatePersonRequest;
import com.example.dto.request.PasswordRecoveryNotificationRequest;
import com.example.dto.request.PasswordRecoveryRequest;
import com.example.dto.response.CreatePersonResponseDto;
import com.example.dto.response.SetDefaultConfirmationTypeResponse;
import com.example.exception.*;
import com.example.model.*;
import com.example.repository.CredentialsRepository;
import com.example.repository.NotificationSettingsRepository;
import com.example.repository.PersonRepository;
import com.example.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
  public SetDefaultConfirmationTypeResponse recoverPassword(PasswordRecoveryRequest recoveryRequest) {
    var credentials =
        credentialsRepository
            .findByLogin(recoveryRequest.getLogin())
            .orElseThrow(() -> new NotFoundException("Not found person with this login"));
    var recoveryToken = jwtUtils.generateJwtToken(recoveryRequest.getLogin(), 300000);
    checkPersonAccount(credentials);
    var link =
        "<html>\n"
            + " <body>\n"
            + "<h3>"
            + "Dear user, you have initiated recovering the password to access the Green Bank application. To reset your current password and set a new one, please follow the link below.</h3>"
            + "\n"
            + "<h3>If you have not initiated a password reset in the application, please follow the instructions below.</h3>"
            + "<h3>1. Don't click on a link in an email</h3>"
            + "\n"
            + "<h3>2. Delete this email from your inbox and from your email trash</h3>"
            + "\n"
            + "<h3>3. For added security, you can change your email password to minimize the risk of using this link.</h3>"
            + "\n"
            + "<h3>4. We advise you to change the password of your account in the Green Bank application.</h3>"
            + "\n"
            + "<h3>Employees of our bank will never ask you for a password or data to access your account, if you receive such a request or you provide such data, you should immediately contact the bank to protect your personal data.</h3>"
            + "\n"
            + "<a href='greenbank://forgot_password/?recovery_token="
            + recoveryToken
            + "'>Reset password</a>\n"
                +"If the link above didn't work try the link below\n"
            + "<a href='https://greeenbank/?action=forgot_password&recovery_token="
            + recoveryToken
            + "'>Reset password</a>\n"
            + " </body>\n"
            + "</html>";

    kafkaTemplate.send(
        "password-recovery",
        PasswordRecoveryNotificationRequest.builder()
            .email(credentials.getPerson().getEmail())
            .link(link)
            .build());
    return SetDefaultConfirmationTypeResponse.builder().personContact(credentials.getPerson().getEmail()).build();
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
  public boolean resetPassword(String token, ChangePasswordRequest changePasswordRequest) {

    if (jwtUtils.validateJwtToken(token)) {
      var login = jwtUtils.getLoginFromJwtToken(token);
      var credentials =
          credentialsRepository
              .findByLogin(login)
              .orElseThrow(() -> new NotFoundException("Not found person with this login"));
      checkPersonAccount(credentials);
      credentials.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
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
