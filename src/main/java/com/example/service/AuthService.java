package com.example.service;

import com.example.dto.ChangePasswordRequest;
import com.example.dto.LoginRequest;
import com.example.dto.LoginResponseDto;
import com.example.dto.RequestNewTokensDto;
import com.example.mapper.LoginResponseMapper;
import com.example.repository.CredentialsRepository;
import com.example.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwtUtils jwtUtils;
  private final CredentialsRepository credentialsRepository;
  private final LoginResponseMapper loginResponseMapper;

  public LoginResponseDto login(LoginRequest loginRequest) {
    Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getLogin(), loginRequest.getPassword()));

    String jwt = jwtUtils.generateJwtToken(authentication);
    String refreshJwt = jwtUtils.generateRefreshToken(authentication);
    credentialsRepository
        .findByLogin(loginRequest.getLogin())
        .ifPresent(
            credentials -> {
              credentials.setRefreshToken(refreshJwt);
              credentialsRepository.save(credentials);
            });
    return loginResponseMapper.toLoginResponseDto(jwt, refreshJwt);
  }

  public LoginResponseDto refreshJwt(RequestNewTokensDto authHeader) {
    var token = authHeader.getRefreshToken();
    var credentials =
        credentialsRepository
            .findByRefreshToken(token)
            .orElseThrow(
                () -> new AuthenticationServiceException("Auth failed! Not found such user"));

    if (!jwtUtils.validateJwtToken(token)) {
      throw new AuthenticationServiceException("Auth failed");
    }
    String login = jwtUtils.getLoginFromJwtToken(token);
    String accessToken = jwtUtils.generateJwtToken(login);
    String refreshToken = jwtUtils.generateRefreshToken(login);

    credentials.setRefreshToken(refreshToken);
    credentialsRepository.save(credentials);

    return loginResponseMapper.toLoginResponseDto(accessToken, refreshToken);
  }

  public String changePassword(ChangePasswordRequest changePasswordRequest, UUID personId) {
    var credentials = credentialsRepository.findById(personId);
    credentials.ifPresent(
        credentials1 -> {
          credentials1.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
          credentialsRepository.save(credentials1);
        });
    return "Password changed!";
  }
}
