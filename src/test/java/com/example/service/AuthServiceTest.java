package com.example.service;

import com.example.dto.LoginResponseDto;
import com.example.mapper.LoginResponseMapper;
import com.example.repository.CredentialsRepository;
import com.example.security.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import static com.example.Fixtures.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class AuthServiceTest {
  @InjectMocks private AuthService authService;
  @Mock private JwtUtils jwtUtils;
  @Mock private AuthenticationManager authenticationManager;
  @Mock private CredentialsRepository credentialsRepository;
  @Mock private LoginResponseMapper loginResponseMapper;
  @Mock private PasswordEncoder passwordEncoder;

  @Test
  void loginMethodReturnTokensTest() {
    LoginResponseDto expectedResult = getLoginResponseDto();

    when(authenticationManager.authenticate(any())).thenReturn(getAuthentication1());
    when(jwtUtils.generateJwtToken((Authentication) any())).thenReturn(token1);
    when(jwtUtils.generateRefreshToken((Authentication) any())).thenReturn(refreshToken1);
    when(credentialsRepository.findByLogin(login))
        .thenReturn(Optional.ofNullable(getCredentials2()));
    when(credentialsRepository.save(getCredentials1())).thenReturn(getCredentials1());
    when(loginResponseMapper.toLoginResponseDto(token1, refreshToken1)).thenReturn(expectedResult);

    LoginResponseDto actualResult = authService.login(getLoginRequest1());

    assertEquals(expectedResult, actualResult);
  }

  @Test
  void refreshJwtMethodReturnNewTokensTest() {
    LoginResponseDto expectedResult = getLoginResponseDto2();

    when(credentialsRepository.findByRefreshToken(refreshToken1))
        .thenReturn(Optional.ofNullable(getCredentials1()));
    when(jwtUtils.validateJwtToken(refreshToken1)).thenReturn(true);
    when(jwtUtils.getLoginFromJwtToken(refreshToken1)).thenReturn(login);
    when(jwtUtils.generateJwtToken(login)).thenReturn(token2);
    when(loginResponseMapper.toLoginResponseDto(token2, refreshToken2))
        .thenReturn(getLoginResponseDto2());
    when(jwtUtils.generateRefreshToken(login)).thenReturn(refreshToken2);
    when(credentialsRepository.save(getCredentials3())).thenReturn(getCredentials3());

    LoginResponseDto actualResult = authService.refreshJwt(getRequestNewTokensDto());
    assertEquals(expectedResult, actualResult);
  }

  @Test
  void changePasswordTest() {
    when(credentialsRepository.findByPersonId(getUUID()))
        .thenReturn(Optional.ofNullable(getCredentials3()));
    when(passwordEncoder.encode(getChangePasswordRequest().getNewPassword()))
        .thenReturn("encodedPassword");
    when(credentialsRepository.save(getCredentials4())).thenReturn(getCredentials4());

    String actualResult = authService.changePassword(getChangePasswordRequest(), getUUID());
    assertEquals(getChangePasswordResult(), actualResult);
  }
}
