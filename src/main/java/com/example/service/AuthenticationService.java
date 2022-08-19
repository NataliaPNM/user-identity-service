package com.example.service;

import com.example.dto.request.ChangePasswordRequest;
import com.example.dto.request.LoginRequest;
import com.example.dto.request.NewTokensRequest;
import com.example.dto.response.ChangePasswordResponseDto;
import com.example.dto.response.LoginResponseDto;
import com.example.exception.*;
import com.example.mapper.LoginResponseMapper;
import com.example.model.Credentials;
import com.example.security.userdetails.UserDetailsServiceImpl;
import com.example.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.Predicate;

import static com.example.util.TimeUtil.getUnlockTimeInMs;
import static com.example.util.ValidationUtil.checkLockTime;
import static com.example.util.ValidationUtil.validateCredentials;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
  private final UserDetailsServiceImpl userDetailsService;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;
  private final LoginResponseMapper loginResponseMapper;
  private final NotificationSettingsService notificationSettingsService;
  private final CredentialsService credentialsService;
  private final LoginAttemptService loginAttemptService;

  @Value("${jwtExpirationMs}")
  private int jwtExpirationMs;

  @Value("${jwtRefreshExpirationMs}")
  private int jwtRefreshExpirationMs;

  public LoginResponseDto login(LoginRequest loginRequest) {
    String login = loginRequest.getLogin();
    String password = loginRequest.getPassword();

    // throws NotFoundException()
    Credentials credentials = credentialsService.findByLogin(login);

    // if lockTime has expired credentials mutate
    // throws PersonAccountLockedException()
    credentials = checkLockTimeAndUnlockCredentials(credentials);

    Predicate<Credentials> checkPasswordMatch =
        c -> passwordEncoder.matches(password, c.getPassword());

    // check if password is correct
    if (!validateCredentials(credentials, checkPasswordMatch)) {
      // add a user to login attempt cache
      loginAttemptService.addUserToLoginAttemptCache(login);

      if (loginAttemptService.hasExceededMaxAttempts(login)) {
        log.error("Bad credentials presented. Account is locked. Login :: {}", login);
        // mutates credentials
        credentials = credentialsService.lockAfterMaximumAttemptsExceeded(credentials);
        // TODO: 21.07.2022 Change logic of working with time to avoid unnecessary transformations
        LocalDateTime lockExpirationTime = LocalDateTime.parse(credentials.getLockTime());
        throw new PersonAccountLockedException(getUnlockTimeInMs(lockExpirationTime));
      } else {
        int loginAttemptsLeft = loginAttemptService.getLoginAttemptsLeft(loginRequest.getLogin());

        log.error(
            "Bad credentials presented. Attempts are left. Login :: {}. Attempts :: {}",
            login,
            loginAttemptsLeft);
        throw new BadCredentialsException(String.valueOf(loginAttemptsLeft));
      }
    }

    checkAccountVerification(credentials);

    // build Authentication object
    // build and set SecurityContext and put it into SecurityContextHolder
    UserDetails userDetails = userDetailsService.loadUserByUsername(login);
    UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(authentication);

    // build JWT Tokens
    String jwt = jwtUtil.generateJwtToken(authentication);
    String refreshJwt = jwtUtil.generateRefreshToken(authentication);

    // mutatesCredentials
    credentials = credentialsService.setRefreshToken(credentials, refreshJwt);

    // reset login attempts counter
    loginAttemptService.evictUserFromLoginAttemptCache(login);

    return loginResponseMapper.toLoginResponseDto(
        jwt,
        refreshJwt,
        jwtExpirationMs,
        jwtRefreshExpirationMs,
        credentials.getPerson().getPersonId());
  }

  private Credentials checkLockTimeAndUnlockCredentials(Credentials credentials) {
    // check if the time of the lock has expired.
    // credentials are mutated here. If TRUE - lock=false and timeLock =""
    if (validateCredentials(credentials, checkLockTime(LocalDateTime.now()))) {
      credentials = credentialsService.unlockAfterLockTimeExpiration(credentials);
    }

    // if credentials are still locked, throw an exception
    if (validateCredentials(credentials, Credentials::isLock)) {
      // TODO: 21.07.2022 create TimeUtil and move all time calculation and parsing code there
      String unlockTime = getUnlockTimeInMs(LocalDateTime.parse(credentials.getLockTime()));

      log.error(
          "Account is locked until time. Credentials:: {}. Time :: {}", credentials, unlockTime);
      throw new PersonAccountLockedException(unlockTime);
    }
    return credentials;
  }

  public void checkAccountVerification(Credentials credentials) {
    if (!credentials.isAccountVerified()) {
      notificationSettingsService.checkPersonConfirmationFullLock(credentials.getPerson());

      throw new AccountConfirmationRequiredException(
          credentials.getPerson().getPersonId().toString());
    }
  }

  public LoginResponseDto refreshJwt(NewTokensRequest newTokensRequest) {
    var refreshToken = newTokensRequest.getRefreshToken();

    Credentials credentials;
    try {
      credentials = credentialsService.findByRefreshToken(refreshToken);
    } catch (NotFoundException e) {
      // rethrowing this exception in order to conform to the original version of the code
      throw new InvalidTokenException("Invalid token");
    }

    try {
      boolean isJwtToken = jwtUtil.isJwtToken(refreshToken);
    } catch (JwtException e) {
      throw new InvalidTokenException("Invalid token");
    }

    // if exception didn't happen get claims from the token
    // read subject (login) from the claims
    //    Claims jwtClaims = jwtUtil.getJwtClaims(refreshToken);
    //    String login = jwtClaims.getSubject();

    // JwtAuthenticationFilter is responsible for populating SecurityContext holder used here
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    String freshAccessToken = jwtUtil.generateJwtToken(authentication);
    String freshRefreshToken = jwtUtil.generateRefreshToken(authentication);

    // credentials mutated here
    credentials = credentialsService.setRefreshToken(credentials, freshRefreshToken);

    return loginResponseMapper.toLoginResponseDto(
        freshAccessToken,
        freshRefreshToken,
        jwtExpirationMs,
        jwtRefreshExpirationMs,
        credentials.getPerson().getPersonId());
  }

  @Transactional(transactionManager = "kafkaTransactionManager", propagation = Propagation.REQUIRED)
  public ChangePasswordResponseDto changePassword(ChangePasswordRequest changePasswordRequest) {
    Credentials credentials;
    // throws NotFoundException()
    credentials =
        credentialsService.findByPersonId(UUID.fromString(changePasswordRequest.getPersonId()));

    // if lockTime has expired credentials mutate
    credentials = checkLockTimeAndUnlockCredentials(credentials);

    notificationSettingsService.checkPersonConfirmationFullLock(credentials.getPerson());

    var temporaryPassword = passwordEncoder.encode(changePasswordRequest.getNewPassword());

    // credentials mutate here
    credentials = credentialsService.setTemporatyPassword(credentials, temporaryPassword);

    return notificationSettingsService.sendConfirmationCodeRequest(
        credentials.getPerson(), "changeCredentials");
  }
}
