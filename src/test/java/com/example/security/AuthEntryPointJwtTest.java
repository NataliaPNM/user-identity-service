package com.example.security;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
class AuthEntryPointJwtTest {

  @Value("${authLockTime}")
  private int authLockTime;

  @InjectMocks private AuthEntryPointJwt authEntryPointJwt;
  private static HttpServletRequest httpServletRequest = new MockHttpServletRequest();
  static final Map<String, Object> expectedBody = new HashMap<>();

  @Test
  void checkUnLockReturnMessageWithCountOfEntryTest() {
    String exceptionMessage = "Bad credentials";
    expectedBody.put("status", HttpServletResponse.SC_UNAUTHORIZED);
    expectedBody.put("path", httpServletRequest.getServletPath());
    expectedBody.put("error", "Unauthorized");
    expectedBody.put("countOfIncorrectEntry", 4);
    expectedBody.put("message", exceptionMessage);
    ArrayList<Integer> countOfEntry = new ArrayList<>();
    ArrayList<Integer> expectedCountOfEntry = new ArrayList<>(List.of(1, 2, 3, 4));
    for (int i = 0; i < 4; i++) {
      countOfEntry.add(
          (int)
              authEntryPointJwt
                  .checkLock(httpServletRequest, exceptionMessage)
                  .get("countOfIncorrectEntry"));
    }
    assertEquals(expectedCountOfEntry, countOfEntry);
  }

  @Test
  void checkLockReturnLockMessageTest() {
    String exceptionMessage = "Authorization blocked";
    expectedBody.put("status", HttpServletResponse.SC_UNAUTHORIZED);
    expectedBody.put("path", httpServletRequest.getServletPath());
    expectedBody.put("countOfIncorrectEntry", 5);
    expectedBody.put("error", exceptionMessage);
    expectedBody.put(
        "message",
        "Вы ввели неправильный пароль 5 раз за последние 5 минут. Доступ к авторизации заблокирован");
    expectedBody.put(
        "timeUntilUnlock",
        DateTimeFormatter.ofPattern("HH:mm").format(LocalDateTime.now().plusMinutes(authLockTime)));

    Map<String, Object> actualBody = null;
    for (int i = 0; i < 5; i++) {
      actualBody = authEntryPointJwt.checkLock(httpServletRequest,"Bad credentials");
    }
    assertEquals(expectedBody, actualBody);
  }
}
