package com.example.service;

import com.example.dto.LoginResponseDto;
import com.example.mapper.LoginResponseMapper;
import com.example.repository.CredentialsRepository;
import com.example.security.AuthEntryPointJwt;
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
  @Mock private AuthEntryPointJwt authEntryPointJwt;

  @Test
  void loginMethodReturnTokensTest() {
    LoginResponseDto expectedResult = getLoginResponseDto("eyJhbGciOiJI", "UCkErJNzC0rcAd", 0, 0);

    when(authenticationManager.authenticate(any())).thenReturn(getAuthentication());
    when(jwtUtils.generateJwtToken((Authentication) any())).thenReturn("eyJhbGciOiJI");
    when(jwtUtils.generateRefreshToken((Authentication) any())).thenReturn("UCkErJNzC0rcAd");
    when(authEntryPointJwt.getIncorrectPasswordCounter()).thenReturn(0);
    when(credentialsRepository.findByLogin("postgres"))
        .thenReturn(
            Optional.ofNullable(
                getCredentials(
                    "f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454", "postgres", "postgres", "")));

    when(credentialsRepository.save(
            getCredentials(
                "f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454", "postgres", "postgres", "UCkErJNzC0rcAd")))
        .thenReturn(
            getCredentials(
                "f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454", "postgres", "postgres", "UCkErJNzC0rcAd"));
    when(loginResponseMapper.toLoginResponseDto("eyJhbGciOiJI", "UCkErJNzC0rcAd", 0, 0))
        .thenReturn(expectedResult);

    LoginResponseDto actualResult = authService.login(getLoginRequest("postgres", "postgres"));

    assertEquals(expectedResult, actualResult);
  }

  @Test
  void refreshJwtMethodReturnNewTokensTest() {
    LoginResponseDto expectedResult = getLoginResponseDto("XOFsm1P2tSDo", "P3yzy8a91ixRrB", 0, 0);

    when(credentialsRepository.findByRefreshToken("UCkErJNzC0rcAd"))
        .thenReturn(
            Optional.ofNullable(
                getCredentials(
                    "f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454",
                    "postgres",
                    "postgres",
                    "UCkErJNzC0rcAd")));
    when(jwtUtils.validateJwtToken("UCkErJNzC0rcAd")).thenReturn(true);
    when(jwtUtils.getLoginFromJwtToken("UCkErJNzC0rcAd")).thenReturn("postgres");
    when(jwtUtils.generateJwtToken("postgres")).thenReturn("XOFsm1P2tSDo");
    when(loginResponseMapper.toLoginResponseDto("XOFsm1P2tSDo", "P3yzy8a91ixRrB", 0, 0))
        .thenReturn(getLoginResponseDto("XOFsm1P2tSDo", "P3yzy8a91ixRrB", 0, 0));
    when(jwtUtils.generateRefreshToken("postgres")).thenReturn("P3yzy8a91ixRrB");
    when(credentialsRepository.save(
            getCredentials(
                "f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454", "postgres", "postgres", "P3yzy8a91ixRrB")))
        .thenReturn(
            getCredentials(
                "f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454", "postgres", "postgres", "P3yzy8a91ixRrB"));

    LoginResponseDto actualResult =
        authService.refreshJwt(getRequestNewTokensDto("UCkErJNzC0rcAd"));
    assertEquals(expectedResult, actualResult);
  }

  @Test
  void changePasswordTest() {
    when(credentialsRepository.findByPersonId(getUUID()))
        .thenReturn(
            Optional.ofNullable(
                getCredentials(
                    "f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454",
                    "postgres",
                    "postgres",
                    "P3yzy8a91ixRrB")));
    when(passwordEncoder.encode(getChangePasswordRequest("newPassword").getNewPassword()))
        .thenReturn("encodedPassword");
    when(credentialsRepository.save(
            getCredentials(
                "f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454",
                "postgres",
                "encodedPassword",
                "P3yzy8a91ixRrB")))
        .thenReturn(
            getCredentials(
                "f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454",
                "postgres",
                "encodedPassword",
                "P3yzy8a91ixRrB"));

    String actualResult =
        authService.changePassword(getChangePasswordRequest("newPassword"), getUUID());
    assertEquals(getChangePasswordResult(), actualResult);
  }
}
