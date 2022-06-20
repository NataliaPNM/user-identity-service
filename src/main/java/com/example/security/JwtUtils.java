package com.example.security;

import com.example.repository.CredentialsRepository;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtils {

  @Value("${jwtSecret}")
  private String jwtSecret;

  //@Value("${jwtExpirationMs}")
  private int jwtExpirationMs=960000;

  //@Value("${jwtRefreshExpirationMs}")
  private int jwtRefreshExpirationMs=1200000;

  private final CredentialsRepository credentialsRepository;

  public String generateJwtToken(Authentication authentication) {

    JwtPerson userPrincipal = (JwtPerson) authentication.getPrincipal();
    return generateJwtToken(userPrincipal.getLogin());
  }

  public String generateJwtToken(String login) {
    //credentialsRepository.findByLogin(login).orElseThrow(RuntimeException::new);
    return Jwts.builder()
        .setSubject(login)
        .setIssuedAt(new Date())
        .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
        .signWith(SignatureAlgorithm.HS512, jwtSecret)
        .compact();
  }

  public String generateRefreshToken(Authentication authentication) {
    JwtPerson userPrincipal = (JwtPerson) authentication.getPrincipal();
    return generateRefreshToken(userPrincipal.getLogin());
  }

  public String generateRefreshToken(String login) {
    return Jwts.builder()
        .setSubject(login)
        .setIssuedAt(new Date())
        .setExpiration(new Date((new Date()).getTime() + jwtRefreshExpirationMs))
        .signWith(SignatureAlgorithm.HS512, jwtSecret)
        .compact();
  }

  public String getLoginFromJwtToken(String token) {
    return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
  }

  public boolean validateJwtToken(String authToken) {
    try {
      Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
      return true;
    } catch (JwtException e) {
      log.error("Invalid token: ", e);
    }
    return false;
  }

  public String parseJwt(HttpServletRequest request) {
    var headerAuth = request.getHeader("Authorization");
    return parseJwt(headerAuth);
  }

  public String parseJwt(String headerAuth) {
    if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
      return headerAuth.substring(7);
    }
    return null;
  }
}
