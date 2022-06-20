package com.example.security;

import com.example.exception.NotFoundSuchUserException;
import com.example.model.Credentials;
import com.example.repository.CredentialsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
        credentialsRepository
            .findByLogin(login) //TODO: 422 status
            .orElseThrow(() -> new NotFoundSuchUserException("Not found user with this login"));
    credentials = setAuthLock(credentials);
    String lockTime = credentials.getLockTime();
    if (!lockTime.equals("") && LocalDateTime.now().isAfter(LocalDateTime.parse(lockTime))) {
      credentials.setLock(false);
      credentials.setLockTime("");
      credentialsRepository.save(credentials);
    }
    return JwtPerson.build(credentials.getPerson(), credentials);
  }

  public Credentials setAuthLock(Credentials credentials) {
    if (authEntryPointJwt.getIncorrectPasswordCounter() == 5) {
      authEntryPointJwt.setIncorrectPasswordCounter(0);
      credentials.setLock(true);
      credentials.setLockTime(LocalDateTime.now().plusMinutes(5L).toString());
      return credentialsRepository.save(credentials);
    } else return credentials;
  }
}
