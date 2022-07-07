package com.example.service;

import com.example.dto.request.ChangePasswordRequest;
import com.example.dto.request.LoginRequest;
import com.example.dto.request.NewTokensRequest;
import com.example.dto.response.ChangePasswordResponseDto;
import com.example.dto.response.LoginResponseDto;
import com.example.exception.AccountConfirmationRequiredException;
import com.example.exception.InvalidTokenException;
import com.example.exception.NotFoundException;
import com.example.exception.PersonAccountLockedException;
import com.example.mapper.LoginResponseMapper;
import com.example.model.Credentials;
import com.example.repository.CredentialsRepository;
import com.example.security.AuthEntryPointJwt;
import com.example.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwtUtils jwtUtils;
  private final CredentialsRepository credentialsRepository;
  private final LoginResponseMapper loginResponseMapper;
  private final AuthEntryPointJwt authEntryPointJwt;
  private final NotificationSettingsService notificationSettingsService;

  @Value("${jwtExpirationMs}")
  private int jwtExpirationMs;

  @Value("${jwtRefreshExpirationMs}")
  private int jwtRefreshExpirationMs;

  public void checkAccountVerification(Credentials credentials) {
    if (!credentials.isAccountVerified()) {
      notificationSettingsService.checkPersonConfirmationFullLock(credentials.getPerson());
      throw new AccountConfirmationRequiredException(
          credentials.getPerson().getPersonId().toString());
    }
  }

  public LoginResponseDto login(LoginRequest loginRequest) {
    var credentials =
        credentialsRepository
            .findByLogin(loginRequest.getLogin())
            .orElseThrow(() -> new NotFoundException("Not found person with this login"));
    if (credentials.isLock()) {
      throw new PersonAccountLockedException(credentials.getLockTime());
    }
    Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getLogin(), loginRequest.getPassword()));
    checkAccountVerification(credentials);

    String jwt = jwtUtils.generateJwtToken(authentication);
    String refreshJwt = jwtUtils.generateRefreshToken(authentication);
    credentials.setRefreshToken(refreshJwt);
    credentialsRepository.save(credentials);
    authEntryPointJwt.setIncorrectPasswordCounter(0);

    return loginResponseMapper.toLoginResponseDto(
        jwt,
        refreshJwt,
        jwtExpirationMs,
        jwtRefreshExpirationMs,
        credentials.getPerson().getPersonId());
  }

  public LoginResponseDto refreshJwt(NewTokensRequest authHeader) {
    var token = authHeader.getRefreshToken();
    var credentials =
        credentialsRepository
            .findByRefreshToken(token)
            .orElseThrow(() -> new InvalidTokenException("Invalid token"));

    if (!jwtUtils.validateJwtToken(token)) {
      throw new AuthenticationServiceException("Auth failed");
    }
    String login = jwtUtils.getLoginFromJwtToken(token);
    String accessToken = jwtUtils.generateJwtToken(login);
    String refreshToken = jwtUtils.generateRefreshToken(login);

    credentials.setRefreshToken(refreshToken);
    credentialsRepository.save(credentials);

    return loginResponseMapper.toLoginResponseDto(
        accessToken,
        refreshToken,
        jwtExpirationMs,
        jwtRefreshExpirationMs,
        credentials.getPerson().getPersonId());
  }

  @Transactional(transactionManager = "kafkaTransactionManager", propagation = Propagation.REQUIRED)
  public ChangePasswordResponseDto changePassword(ChangePasswordRequest changePasswordRequest) {
    var credentials =
        credentialsRepository
            .findByPersonId(UUID.fromString(changePasswordRequest.getPersonId()))
            .orElseThrow(() -> new NotFoundException("Not found person with this id"));
    if (credentials.isLock()) {
      throw new PersonAccountLockedException(credentials.getLockTime());
    }
    notificationSettingsService.checkPersonConfirmationFullLock(credentials.getPerson());
    var temporaryPassword = passwordEncoder.encode(changePasswordRequest.getNewPassword());

    credentials.setTemporaryPassword(temporaryPassword);
    credentialsRepository.save(credentials);

    return notificationSettingsService.sendConfirmationCodeRequest(
        credentials.getPerson(), "changeCredentials");
  }


}
