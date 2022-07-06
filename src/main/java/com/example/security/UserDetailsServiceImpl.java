package com.example.security;

import com.example.exception.NotFoundException;
import com.example.exception.PersonAccountLockedException;
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
  @Transactional(noRollbackFor = PersonAccountLockedException.class)
  public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
    Credentials credentials =
        credentialsRepository
            .findByLogin(login)
            .orElseThrow(() -> new NotFoundException("Not found user with this login"));
    if (authEntryPointJwt.getIncorrectPasswordCounter() == 5) {
      authEntryPointJwt.setIncorrectPasswordCounter(0);
      credentials.setLock(true);
      if (credentials.isAccountVerified()) {
        credentials.setLockTime(LocalDateTime.now().plusMinutes(60L).toString());
      }else{
        credentials.setLockTime(LocalDateTime.now().plusHours(24L).toString());
      }

      var cred = credentialsRepository.save(credentials);
      var locktime = cred.getLockTime();
      throw new PersonAccountLockedException(locktime);
    }
    String lockTime = credentials.getLockTime();
    if (!lockTime.equals("") && LocalDateTime.now().isAfter(LocalDateTime.parse(lockTime))) {
      credentials.setLock(false);
      credentials.setLockTime("");
      credentialsRepository.save(credentials);
    }
    return JwtPerson.build(credentials.getPerson(), credentials);
  }
}
