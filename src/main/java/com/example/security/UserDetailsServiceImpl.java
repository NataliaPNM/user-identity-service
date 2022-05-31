package com.example.security;

import com.example.repository.CredentialsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

  private final CredentialsRepository credentialsRepository;

  @Override
  @Transactional
  public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
    var credentials =
        credentialsRepository
            .findByLogin(login)
            .orElseThrow(
                () -> new UsernameNotFoundException("User Not Found with login: " + login));

    return JwtPerson.build(credentials.getPerson(), credentials);
  }
}
