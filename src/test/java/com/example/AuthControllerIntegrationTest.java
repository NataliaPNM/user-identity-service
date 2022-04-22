package com.example;

import com.example.repository.SecureRepository;
import com.example.security.JwtUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class AuthControllerIntegrationTest extends AuthorizationServiceApplicationTests {

    @Autowired
    protected JwtUtils jwtUtils;
    @Autowired
    protected SecureRepository secureRepository;

    @Test
    public void getResponseStatusOkInChangeCredentialsMethod() {
        headers.add("Authorization", "Bearer " + getToken());
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/auth/secure"),
                HttpMethod.POST, new HttpEntity<>(InitDataForTests.getChangePasswordRequestForTest(), headers),
                new ParameterizedTypeReference<>() {
                });
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

}
