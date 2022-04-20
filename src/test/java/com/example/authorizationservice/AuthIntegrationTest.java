package com.example.authorizationservice;

import com.example.authorizationservice.AuthService.AuthService;
import com.example.authorizationservice.dto.LoginResponseDto;
import com.example.authorizationservice.repository.SecureRepository;
import com.example.authorizationservice.security.JwtUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AuthorizationServiceApplication.class)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthIntegrationTest {

    @Autowired
    protected AuthService authService;
    @Autowired
    protected JwtUtils jwtUtils;
    @Autowired
    protected SecureRepository secureRepository;

    @Test
    public void returnNotNullValueTest() {
        LoginResponseDto actualResult = authService.login(InitDataForTest.getLoginRequest());
        Assertions.assertTrue(actualResult.getToken() != null && actualResult.getRefreshToken() != null);
        Assertions.assertTrue(jwtUtils.validateJwtToken(actualResult.getToken()));

    }


}
