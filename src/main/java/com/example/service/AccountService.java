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
import com.example.util.JwtUtil;
import lombok.RequiredArgsConstructor;
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
    // temporary issues, will be removed
    var link =
        "<html lang='\\\"en\\\"'><head>\n"
            + "            <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n"
            + "            <meta name=\"viewport\" content=\"initial-scale=1.0\">\n"
            + "            <meta name=\"GENERATOR\" content=\"MSHTML 11.00.10570.1001\"></head>\n"
            + "            <body style=\"background-color: rgb(255, 255, 255);\" bgcolor=\"#ffffff\">\n"
            + "              <table width=\"100%\" height=\"100%\" bgcolor=\"#9370db\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n"
            + "               <!-- container -->\n"
            + "                  <tbody>\n"
            + "                  <tr>\n"
            + "                    <td align=\"center\" valign=\"top\" style=\"background-color: rgb(255, 255, 255);\" bgcolor=\"#ebebeb\"><!-- container -->\n"
            + "                            <table width=\"700\" class=\"container\" bgcolor=\"#ffffff\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" border-radius=\"6\">\n"
            + "                             <tbody>\n"
            + "                              <tr>\n"
            + "                     <td class=\"container-padding\" style=\"border-radius: 15px; color: rgb(51, 51, 51); line-height: 20px; padding-right: 30px;"
            + " padding-left: 30px; font-family: Helvetica, sans-serif; font-size: 14px; background-color: rgb(224, 230, 250);\" bgcolor=\"#ffffff\"><br><!-- CONTENT -->\n"
            + "                     <p style=\"margin: 0px; padding: 0px; border-radius: 10px; line-height: 10px; font-size: 10px; font-weight: bold;\">"
            + "&nbsp;</p><p align=\"center\" style=\"margin: 0px; padding: 0px; color: rgb(224, 230, 250); line-height: 32px; font-size: 28px; font-weight:"
            + " bold;\">&nbsp;&nbsp;<font color=\"#80ff00\" size=\"8\">GreenBank</font>\n"
            + "          </p><p align=\"center\" style=\"margin: 0px; padding: 0px; color: rgb(247, 180, 0); line-height: 32px; font-size: 28px;"
            + " font-weight: bold;\"><hr id=\"undefined\"><p></p><p style=\"margin: 0px; padding: 0px; color: rgb(51, 51, 51); line-height:"
            + " 32px; font-size: 28px; font-weight: bold;\"> </p>\n"
            + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span id=\"spintext\" "
            + "spincoll=\"Программа имеет|У программы\" spinflag=\"($R-$C0)\">&nbsp;<font size=\"4\">Dear user,"
            + " you have initiated recovering the password to access the Green Bank application. To reset your "
            + "current password and set a new one, please follow the link below.If you have not initiated a password "
            + "reset in the application, please follow the instructions below.</font><p style=\"color: rgb(34, 34, 34);"
            + " text-transform: none; text-indent: 25px; letter-spacing: normal; font-family: Arial, Helvetica, sans-serif;"
            + " font-style: normal; font-weight: 400; word-spacing: 0px; white-space: normal; orphans: 2; widows: 2;"
            + " background-color: rgb(224, 230, 250); font-variant-ligatures: normal; font-variant-caps: normal;"
            + " -webkit-text-stroke-width: 0px; text-decoration-thickness: initial; text-decoration-style: initial; "
            + "text-decoration-color: initial;\"><font size=\"4\">1. Don't click on a link in an email</font></p>"
            + "<p style=\"color: rgb(34, 34, 34); text-transform: none; text-indent: 25px; letter-spacing: normal;"
            + " font-family: Arial, Helvetica, sans-serif; font-style: normal; font-weight: 400; word-spacing: 0px;"
            + " white-space: normal; orphans: 2; widows: 2; background-color: rgb(224, 230, 250); font-variant-ligatures:"
            + " normal; font-variant-caps: normal; -webkit-text-stroke-width: 0px; text-decoration-thickness: initial;"
            + " text-decoration-style: initial; text-decoration-color: initial;\"><font size=\"4\">2. Delete this email"
            + " from your inbox and from your email trash</font></p><p style=\"color: rgb(34, 34, 34); text-transform: "
            + "none; text-indent: 25px; letter-spacing: normal; font-family: Arial, Helvetica, sans-serif; font-style: "
            + "normal; font-weight: 400; word-spacing: 0px; white-space: normal; orphans: 2; widows: 2; background-color:"
            + " rgb(224, 230, 250); font-variant-ligatures: normal; font-variant-caps: normal; -webkit-text-stroke-width:"
            + " 0px; text-decoration-thickness: initial; text-decoration-style: initial; text-decoration-color:"
            + " initial;\"><font size=\"4\">3. For added security, you can change your email password to minimize"
            + " the risk of using this link.</font></p><p style=\"color: rgb(34, 34, 34); text-transform: none; "
            + "text-indent: 25px; letter-spacing: normal; font-family: Arial, Helvetica, sans-serif; font-style:"
            + " normal; font-weight: 400; word-spacing: 0px; white-space: normal; orphans: 2; widows: 2; "
            + ""
            + "background-color: rgb(224, 230, 250); font-variant-ligatures: normal; font-variant-caps: normal;"
            + " -webkit-text-stroke-width: 0px; text-decoration-thickness: initial; text-decoration-style: initial;"
            + " text-decoration-color: initial;\"><font size=\"4\">4. We advise you to change the password of your account"
            + " in the Green Bank application.</font></p><p style=\"color: rgb(34, 34, 34); text-transform: none; "
            + "text-indent: 25px; letter-spacing: normal; font-family: Arial, Helvetica, sans-serif; font-style: "
            + "normal; font-weight: 400; word-spacing: 0px; white-space: normal; orphans: 2; widows: 2; background-color:"
            + " rgb(224, 230, 250); font-variant-ligatures: normal; font-variant-caps: normal; -webkit-text-stroke-width:"
            + " 0px; text-decoration-thickness: initial; text-decoration-style: initial; text-decoration-color: "
            + ""
            + "initial;\"><font size=\"4\">Employees of our bank will never ask you for a password or data to access "
            + "your account, if you receive such a request or you provide such data, you should immediately contact "
            + "the bank to protect your personal data.</font></p><p align=\"center\" style=\"color: rgb(34, 34, 34); "
            + "text-transform: none; text-indent: 25px; letter-spacing: normal; font-family: Arial, Helvetica, sans-serif;"
            + " font-weight: 400; word-spacing: 0px; white-space: normal; orphans: 2; widows: 2; background-color: "
            + "rgb(224, 230, 250); font-variant-ligatures: normal; font-variant-caps: normal; -webkit-text-stroke-width:"
            + " 0px; text-decoration-thickness: initial; text-decoration-style: initial; text-decoration-color: initial;"
            + "\"><a href='greenbank://forgot_password/?recovery_token="
            + recoveryToken
            + "'><font"
            + " size=\"5\">Reset password</font></a></p><p style=\"color: rgb(34, 34, 34); text-transform: none;"
            + " text-indent: 25px; letter-spacing: normal; font-family: Arial, Helvetica, sans-serif; font-style:"
            + " normal; font-weight: 400; word-spacing: 0px; white-space: normal; orphans: 2; widows: 2; background-color:"
            + " rgb(224, 230, 250); font-variant-ligatures: normal; font-variant-caps: normal; -webkit-text-stroke-width:"
            + " 0px; text-decoration-thickness: initial; text-decoration-style: initial; text-decoration-color:"
            + " initial;\"><span style=\"color: rgb(34, 34, 34); text-transform: none; text-indent: 25px; letter-spacing:"
            + " normal; font-family: Arial, Helvetica, sans-serif; font-style: normal; font-weight: 400; word-spacing: 0px;"
            + " float: none; display: inline !important; white-space: normal; orphans: 2; widows: 2; background-color: "
            + "rgb(224, 230, 250); font-variant-ligatures: normal; font-variant-caps: normal; -webkit-text-stroke-width: "
            + "0px; text-decoration-thickness: initial; text-decoration-style: initial; text-decoration-color: initial;\">"
            + "<font size=\"4\">If the link above didn't work try the link below.</font></span></p><p align=\"center\" "
            + "style=\"color: rgb(34, 34, 34); text-transform: none; text-indent: 25px; letter-spacing: normal; font-family:"
            + " Arial, Helvetica, sans-serif; font-style: normal; font-weight: 400; word-spacing: 0px; white-space: normal; "
            + "orphans: 2; widows: 2; background-color: rgb(224, 230, 250); font-variant-ligatures: normal;"
            + " font-variant-caps: normal; -webkit-text-stroke-width: 0px; text-decoration-thickness: initial;"
            + " text-decoration-style: initial; text-decoration-color: initial;\"><span style=\"color: rgb(34, 34, 34); "
            + "text-transform: none; text-indent: 25px; letter-spacing: normal; font-family: Arial, Helvetica, sans-serif;"
            + " font-style: normal; font-weight: 400; word-spacing: 0px; float: none; display: inline !important;"
            + " white-space: normal; orphans: 2; widows: 2; background-color: rgb(224, 230, 250); font-variant-ligatures:"
            + " normal; font-variant-caps: normal; -webkit-text-stroke-width: 0px; text-decoration-thickness: initial;"
            + " text-decoration-style: initial; text-decoration-color: initial;\"><a "
            + "href=\"https://greeenbank/?action=forgot_password&amp;recovery_token="
            + recoveryToken
            + "\">"
            + "<font size=\"5\">Reset Password</font><span style=\"color: rgb(34, 34, 34); text-transform: "
            + "none; text-indent: 25px; letter-spacing: normal; font-family: Arial, Helvetica, sans-serif; "
            + "font-size: small; font-style: normal; font-weight: 400; word-spacing: 0px; float: none; display: inline "
            + "!important; white-space: normal; orphans: 2; widows: 2; background-color: rgb(255, 255, 255); "
            + "font-variant-ligatures: normal; font-variant-caps: normal; -webkit-text-stroke-width: 0px; "
            + "text-decoration-thickness: initial; text-decoration-style: initial; text-decoration-color: initial;\">"
            + "<font color=\"#000000\"><br></font></span></a></span></p></span><p><br></p>\n"
            + "</tr></tbody></table></tr></tbody></table></body></html>";

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
          getUnlockTimeInMs(LocalDateTime.parse(credentials.getPerson().getNotificationSettings().getEmailLockTime())));
    }
  }
  public String getPersonIdFromLogin(String token){
    if(jwtUtils.validateJwtToken(token)){
      var login = jwtUtils.getLoginFromJwtToken(token);
      var credentials = credentialsService.findByLogin(login);
      return credentials.getPerson().getPersonId().toString();
    }
    throw new InvalidTokenException("Token is not valid");
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
        .ifPresent(person -> {
          throw new PersonWithThisLoginOrEmailAlreadyExistsException("User with this login already exists");});

    personRepository
        .findByEmail(createPersonRequest.getEmail())
        .ifPresent(person -> {
              throw new PersonWithThisLoginOrEmailAlreadyExistsException("User with this email already exists");});

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
