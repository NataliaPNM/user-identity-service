package com.example.service;
//
//import com.example.dto.response.ChangePasswordResponseDto;
//import com.example.dto.response.LoginResponseDto;
//import com.example.mapper.LoginResponseMapper;
//import com.example.model.Credentials;
//import com.example.model.Person;
//import com.example.model.enums.PersonRole;
//import com.example.repository.CredentialsRepository;
//import com.example.security.exceptionhandler.AuthenticationEntryPoint;
//import com.example.util.JwtUtil;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Captor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import java.util.Optional;
//import java.util.UUID;
//
//import static com.example.Fixtures.*;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//@Slf4j
//
//public class AuthenticationServiceTest {
//  @InjectMocks private AuthenticationService authenticationService;
//  @Captor
//  private Credentials credentials;
//  @Mock private JwtUtil jwtUtil;
//  @Mock private AuthenticationManager authenticationManager;
//  @Mock private CredentialsRepository credentialsRepository;
//  @Mock private LoginResponseMapper loginResponseMapper;
//  @Mock private PasswordEncoder passwordEncoder;
//  @Mock private AuthenticationEntryPoint authenticationEntryPoint;
//  @Mock private NotificationSettingsService notificationSettingsService;
//
//  @Test
//  void loginMethodReturnTokensForVerificatedPersonAccountTest() {
//    LoginResponseDto expectedResult =
//        getLoginResponseDto(
//            "eyJhbGciOiJI",
//            "UCkErJNzC0rcAd",
//            780000,
//            600000,
//            UUID.fromString("d6d83746-d862-4562-99d4-4ec5a664a59a"));
//
//    Person person =
//        getPerson(
//            123456L, "", "", "d6d83746-d862-4562-99d4-4ec5a664a59a", "", "", PersonRole.ROLE_USER);
//    Credentials credentials =
//        getCredentials(
//            "f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454",
//            "postgres",
//            "postgres",
//            true,
//            "",
//            "",
//            person,
//            false,
//            "");
//
//    when(authenticationManager.authenticate(any())).thenReturn(getAuthentication());
//    when(jwtUtil.generateJwtToken((Authentication) any())).thenReturn("eyJhbGciOiJI");
//    when(jwtUtil.generateRefreshToken((Authentication) any())).thenReturn("UCkErJNzC0rcAd");
//    when(credentialsRepository.findByLogin("postgres"))
//        .thenReturn(Optional.ofNullable(credentials));
//
//    when(credentialsRepository.save(
//            getCredentials(
//                "f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454",
//                "postgres",
//                "postgres",
//                true,
//                "UCkErJNzC0rcAd",
//                "",
//                person,
//                false,
//                "")))
//        .thenReturn(
//            getCredentials(
//                "f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454",
//                "postgres",
//                "postgres",
//                true,
//                "UCkErJNzC0rcAd",
//                "",
//                person,
//                false,
//                ""));
//    when(loginResponseMapper.toLoginResponseDto(
//            "eyJhbGciOiJI", "UCkErJNzC0rcAd", 0, 0, credentials.getPerson().getPersonId()))
//        .thenReturn(expectedResult);
//
//    LoginResponseDto actualResult = authenticationService.login(getLoginRequest("postgres", "postgres"));
//
//    assertEquals(expectedResult, actualResult);
//  }
//
//  @Test
//  void refreshJwtMethodReturnNewTokensTest() {
//    LoginResponseDto expectedResult =
//        getLoginResponseDto(
//            "XOFsm1P2tSDo",
//            "P3yzy8a91ixRrB",
//            0,
//            0,
//            UUID.fromString("d6d83746-d862-4562-99d4-4ec5a664a59a"));
//
//    Person person =
//        getPerson(
//            123456L, "", "", "d6d83746-d862-4562-99d4-4ec5a664a59a", "", "", PersonRole.ROLE_USER);
//    Credentials credentials =
//        getCredentials(
//            "f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454",
//            "postgres",
//            "postgres",
//            true,
//            "UCkErJNzC0rcAd",
//            "",
//            person,
//            false,
//            "");
//    when(credentialsRepository.findByRefreshToken("UCkErJNzC0rcAd"))
//        .thenReturn(Optional.ofNullable(credentials));
//
//
//    when(jwtUtil.validateJwtToken("UCkErJNzC0rcAd")).thenReturn(true);
//    when(jwtUtil.getLoginFromJwtToken("UCkErJNzC0rcAd")).thenReturn("postgres");
//    when(jwtUtil.generateJwtToken(any())).thenReturn("XOFsm1P2tSDo");
//    when(loginResponseMapper.toLoginResponseDto(
//            "XOFsm1P2tSDo", "P3yzy8a91ixRrB", 0, 0, credentials.getPerson().getPersonId()))
//        .thenReturn(
//            getLoginResponseDto(
//                "XOFsm1P2tSDo", "P3yzy8a91ixRrB", 0, 0, credentials.getPerson().getPersonId()));
//
//    when(jwtUtil.generateRefreshToken(any())).thenReturn("P3yzy8a91ixRrB");
//    when(credentialsRepository.save(
//            getCredentials(
//                "f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454",
//                "postgres",
//                "postgres",
//                true,
//                "P3yzy8a91ixRrB",
//                "",
//                person,
//                false,
//                "")))
//        .thenReturn(
//            getCredentials(
//                "f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454",
//                "postgres",
//                "postgres",
//                true,
//                "P3yzy8a91ixRrB",
//                "",
//                person,
//                false,
//                ""));
//
//    LoginResponseDto actualResult =
//        authenticationService.refreshJwt(getRequestNewTokensDto("UCkErJNzC0rcAd"));
//    assertEquals(expectedResult, actualResult);
//  }
//
//  @Test
//  void changePasswordInVerificatedPersonAccountTest() {
//    ChangePasswordResponseDto expectedResult = getChangePasswordResult();
//
//    Person person =
//        getPerson(
//            123456L, "", "", "d6d83746-d862-4562-99d4-4ec5a664a59a", "", "", PersonRole.ROLE_USER);
//
//    when(credentialsRepository.findByPersonId(
//            UUID.fromString("d6d83746-d862-4562-99d4-4ec5a664a59a")))
//        .thenReturn(
//            Optional.ofNullable(
//                getCredentials(
//                    "f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454",
//                    "postgres",
//                    "postgres",
//                    true,
//                    "P3yzy8a91ixRrB",
//                    "",
//                    person,
//                    false,
//                    "")));
//    when(passwordEncoder.encode(
//            getChangePasswordRequest("newPassword", "f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454")
//                .getNewPassword()))
//        .thenReturn("encodedPassword");
//    when(credentialsRepository.save(
//            getCredentials(
//                "f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454",
//                "postgres",
//                "postgres",
//                true,
//                "P3yzy8a91ixRrB",
//                "encodedPassword",
//                person,
//                false,
//                "")))
//        .thenReturn(
//            getCredentials(
//                "f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454",
//                "postgres",
//                "postgres",
//                true,
//                "P3yzy8a91ixRrB",
//                "encodedPassword",
//                person,
//                false,
//                ""));
//
//    when(notificationSettingsService.sendConfirmationCodeRequest(person, "changeCredentials"))
//        .thenReturn(getChangePasswordResult());
//    ChangePasswordResponseDto actualResult =
//        authenticationService.changePassword(
//            getChangePasswordRequest("newPassword", "d6d83746-d862-4562-99d4-4ec5a664a59a"));
//    assertEquals(getChangePasswordResult(), actualResult);
//  }
//
////  @ParameterizedTest
////  @ValueSource(ints = { 1, 2, 3 })
////  void testParameterized(int i){
////    System.out.println(i);
////
////  }
//
//}
