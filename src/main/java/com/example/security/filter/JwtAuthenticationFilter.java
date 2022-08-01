package com.example.security.filter;

import com.example.exception.InvalidTokenException;
import com.example.security.userdetails.UserDetailsServiceImpl;
import com.example.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private final JwtUtil jwtUtil;
  private final UserDetailsServiceImpl userDetailsService;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {

    //expect to have AUTHORIZATION header and Bearer inside it
    if (jwtUtil.doesRequestHasAuthorizationHeader(request) &&
            jwtUtil.isAuthorizationTypeBearer(request)) {
      try {
        //All is ok get Jwt token string from the request
        String jwtToken = jwtUtil.parseJwt(request);

        //throws JwtException() if token is invalid
        boolean isJwtToken = jwtUtil.isJwtToken(jwtToken);

        //if exception didn't happen get claims from the token
        //read subject (login) from the claims
        Claims jwtClaims = jwtUtil.getJwtClaims(jwtToken);
        String login = jwtClaims.getSubject();

        //returns JwtPerson as a result
        //throws NotFoundException() if there is no such a user in the DB
        UserDetails userDetails = userDetailsService.loadUserByUsername(login);

        //build and set SecurityContext and put it into SecurityContextHolder
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request,response);
      } catch (JwtException e) {
        log.error("Jwt token is invalid. Error message :: " + e.getMessage());
        //rethrowing this exception in order to conform to the original version of the code
        throw new InvalidTokenException("Invalid token");
      }
    } else {
      filterChain.doFilter(request, response);
    }
  }
}
