package com.example.security;

import com.example.exception.NotFoundSuchUserException;
import com.example.model.Credentials;
import com.example.repository.CredentialsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

  private final CredentialsRepository credentialsRepository;
  private final AuthEntryPointJwt authEntryPointJwt;

  @Override
  @Transactional
  public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
    Credentials credentials =
        credentialsRepository.findByLogin(login).orElseThrow(NotFoundSuchUserException::new);
    setAuthLock(credentials);
    String lockTime = credentials.getLockTime();
    if (!lockTime.equals("")) {
      if (LocalDateTime.now().isAfter(LocalDateTime.parse(lockTime))) {
        credentials.setLock(false);
        credentials.setLockTime("");
        credentialsRepository.save(credentials);
      }
    }
    return JwtPerson.build(credentials.getPerson(), credentials);
  }

  public void setAuthLock(Credentials credentials) {
    if (authEntryPointJwt.getIncorrectPasswordCounter() == 5) {
      authEntryPointJwt.setIncorrectPasswordCounter(0);
      credentials.setLock(true);
      credentials.setLockTime(LocalDateTime.now().plusMinutes(5L).toString());
      credentialsRepository.save(credentials);
    }
  }
}
