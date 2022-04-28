package com.example;

import com.example.repository.SecureRepository;
import com.example.repository.UserRepository;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Objects;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthorizationServiceApplicationTests {

    @Autowired
    protected SecureRepository secureRepository;
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected TestRestTemplate restTemplate;
    @LocalServerPort
    private int port;

    protected final HttpHeaders headers = new HttpHeaders();

    public void init() {
        userRepository.save(InitDataForTests.getUserForTests());
        secureRepository.save(InitDataForTests.getSecureForTests());
    }

    protected String getToken() {
        init();
        String loginAndPassword =
                """
                        {
                          "login": "gektor",
                          "password": "11111"
                        }""";

        headers.add("Content-Type", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(loginAndPassword, headers);
        ResponseEntity<String> response
                = restTemplate.exchange(createURLWithPort("/auth/signin"), HttpMethod.POST, entity, String.class);

        String resp = response.getBody();

        return Objects.requireNonNull(resp).substring(10, resp.indexOf("\"", 10));
    }

    protected String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }

}
