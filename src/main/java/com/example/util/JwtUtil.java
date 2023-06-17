package com.example.util;

import com.example.security.userdetails.JwtUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {

    @Value("${jwtSecret}")
    private String jwtSecret;

    @Value("${jwtExpirationMs}")
    private int jwtExpirationMs = 960000;

    @Value("${jwtRefreshExpirationMs}")
    private int jwtRefreshExpirationMs = 1200000;

    public String generateJwtToken(Authentication authentication) {
        JwtUserDetails userPrincipal = (JwtUserDetails) authentication.getPrincipal();
        return generateJwtToken(userPrincipal.getLogin(), jwtExpirationMs);
    }

    public String generateJwtToken(String login, int jwtExpirationMs) {
        return Jwts.builder()
                .setSubject(login)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String generateRefreshToken(Authentication authentication) {
        JwtUserDetails userPrincipal = (JwtUserDetails) authentication.getPrincipal();
        return generateRefreshToken(userPrincipal.getLogin(), jwtRefreshExpirationMs);
    }

    public String generateRefreshToken(String login, int jwtRefreshExpirationMs) {
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
            e.printStackTrace();
        }
        return false;
    }

    public String parseJwt(HttpServletRequest request) {
        var headerAuth = request.getHeader(HttpHeaders.AUTHORIZATION);
        return parseJwt(headerAuth);
    }

    public String parseJwt(String headerAuth) {
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }

    public boolean doesRequestHasAuthorizationHeader(HttpServletRequest request) {
        return request.getHeader(HttpHeaders.AUTHORIZATION) != null;
    }

    public boolean isAuthorizationTypeBearer(HttpServletRequest request) {
        return request.getHeader(HttpHeaders.AUTHORIZATION).startsWith("Bearer ");
    }

    public boolean isJwtToken(String authToken) throws JwtException {
        Claims claims = getJwtClaims(authToken);
        return claims != null;
    }

    public Claims getJwtClaims(String authToken) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(authToken)
                .getBody();
    }
}
