package com.example.service;

import com.example.dto.request.*;
import com.example.dto.response.CreatePersonResponseDto;
import com.example.dto.response.LoginResponseDto;
import com.example.dto.response.SetDefaultConfirmationTypeResponse;
import com.example.exception.*;
import com.example.model.*;
import com.example.repository.CredentialsRepository;
import com.example.repository.NotificationSettingsRepository;
import com.example.repository.PersonRepository;
import com.example.util.FileUtil;
import com.example.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.example.util.TimeUtil.getUnlockTimeInMs;

@Service
@RequiredArgsConstructor
public class AccountService {
  private final PasswordEncoder passwordEncoder;
  private final CredentialsRepository credentialsRepository;
  private final PersonRepository personRepository;
  private final NotificationSettingsRepository notificationSettingsRepository;
  private final KafkaTemplate<String, PasswordRecoveryNotificationRequest> kafkaTemplate;
  private final JwtUtil jwtUtils;
  private final CredentialsService credentialsService;
  private final AuthenticationService authenticationService;
  private final LoginAttemptService loginAttemptService;
  private final FileUtil fileUtil;
  private final String RECOVERY_PAGE_FILE = "classpath:files/password-recovery-link.html";
  private final String RECOVERY_TOKEN_BODY_TAG = "%recovery_token_body%";

  @Value("${jwtExpirationMs}")
  private int jwtExpirationMs;

  @Value("${jwtRefreshExpirationMs}")
  private int jwtRefreshExpirationMs;

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

    // read html file content into a String
    // and replace all %recovery_token_body% with an actual token
    String link = fileUtil.getFileDataAsString(RECOVERY_PAGE_FILE);
    link = link.replaceAll(RECOVERY_TOKEN_BODY_TAG, recoveryToken);

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
          getUnlockTimeInMs(LocalDateTime.parse(credentials.getLockTime())));
    } else if (credentials.getPerson().getNotificationSettings().getEmailLock()) {
      throw new DefaultConfirmationTypeLockedException(
          getUnlockTimeInMs(
              LocalDateTime.parse(
                  credentials.getPerson().getNotificationSettings().getEmailLockTime())));
    }
  }

  public String getPersonIdFromLogin(String token) {
    if (jwtUtils.validateJwtToken(token)) {
      var login = jwtUtils.getLoginFromJwtToken(token);
      var credentials = credentialsService.findByLogin(login);
      return credentials.getPerson().getPersonId().toString();
    }
    throw new InvalidTokenException("Token is not valid");
  }

  @Transactional
  public LoginResponseDto resetPassword(String token, ResetPasswordRequest resetPasswordRequest) {

    if (jwtUtils.validateJwtToken(token)) {
      var login = jwtUtils.getLoginFromJwtToken(token);
      var credentials =
          credentialsRepository
              .findByLogin(login)
              .orElseThrow(() -> new NotFoundException("Not found person with this login"));
      checkPersonAccount(credentials);
      credentials.setPassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));
      var refreshJwt = jwtUtils.generateRefreshToken(login, jwtRefreshExpirationMs);
      credentials.setRefreshToken(refreshJwt);
      credentialsRepository.save(credentials);
      loginAttemptService.evictUserFromLoginAttemptCache(login);
      return LoginResponseDto.builder()
          .token(jwtUtils.generateJwtToken(login, jwtExpirationMs))
          .refreshToken(refreshJwt)
          .accessTokenExpirationTime(jwtExpirationMs)
          .refreshTokenExpirationTime(jwtRefreshExpirationMs)
          .personId(credentials.getPerson().getPersonId())
          .build();
    } else {
      throw new InvalidTokenException("Token is not valid");
    }
  }

  @Transactional(propagation = Propagation.NESTED)
  public LoginResponseDto loginAfterReset(LoginRequest loginRequest) {
    return authenticationService.login(loginRequest);
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
            person -> {
              throw new PersonWithThisLoginOrEmailAlreadyExistsException(
                  "User with this login already exists");
            });

    personRepository
        .findByEmail(createPersonRequest.getEmail())
        .ifPresent(
            person -> {
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
