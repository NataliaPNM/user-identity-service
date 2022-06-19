package com.example.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
@Data
@RequiredArgsConstructor
public class AuthEntryPointJwt implements AuthenticationEntryPoint {
  private final ObjectMapper mapper;
  private LocalDateTime firstEntryTime;
  private int incorrectPasswordCounter = 0;

  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException)
      throws IOException {
    log.error("Unauthorized error: " + authException.getMessage());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    mapper.writeValue(response.getOutputStream(), checkLock(request, authException.getMessage()));
  }

  public Map<String, Object> checkLock(HttpServletRequest request, String exceptionMessage) {
    final Map<String, Object> body = new HashMap<>();
    body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
    body.put("path", request.getServletPath());
    body.put("error", "Unauthorized");
    body.put("message", exceptionMessage);
    if (exceptionMessage.equals("Bad credentials")) {
      incorrectPasswordCounter++;
      body.put("countOfIncorrectEntry", incorrectPasswordCounter);
      if (incorrectPasswordCounter == 1) {
        firstEntryTime = LocalDateTime.now();
      } else if (firstEntryTime.plusMinutes(5L).isBefore(LocalDateTime.now())) {
        incorrectPasswordCounter = 1;
        firstEntryTime = LocalDateTime.now();
      }
      if (incorrectPasswordCounter == 5) {
        body.put("error", "Authorization blocked");
        body.put("message", "You entered your password incorrectly 5 times");
        body.put(
            "timeToUnlock",
            DateTimeFormatter.ofPattern("HH:mm")
                .format(LocalDateTime.now().plusMinutes(5L)));
      }
    }
    return body;
  }
}
