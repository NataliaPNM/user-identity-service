package com.example.security;

import com.example.model.Credentials;
import com.example.model.Person;
import com.example.model.PersonRole;
import com.example.repository.CredentialsRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.example.Fixtures.getCredentials;
import static com.example.Fixtures.getPerson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
class UserDetailsServiceImplTest {

  @InjectMocks private UserDetailsServiceImpl userDetailsService;
  @Mock private CredentialsRepository credentialsRepository;
  @Mock private AuthEntryPointJwt authEntryPointJwt;

  Person person =
      getPerson(
          123456L, "", "", "f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454", "", "", PersonRole.ROLE_USER);
  Credentials credentials =
      getCredentials("f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454", "", "", "", person, false, "");
  Credentials credentials2 =
      getCredentials("f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454", "", "", "", person, true, "");

  @Test
  void lockPersonAfter5TimesIncorrectEntryTest() {
    when(credentialsRepository.findByLogin("login")).thenReturn(Optional.of(credentials));
    when(authEntryPointJwt.getIncorrectPasswordCounter()).thenReturn(5);
    when(credentialsRepository.save(any(Credentials.class))).thenReturn(credentials2);
    boolean expectedResult =
        JwtPerson.build(credentials2.getPerson(), credentials2).isAccountNonLocked();
    boolean actualResult = userDetailsService.loadUserByUsername("login").isAccountNonLocked();

    assertEquals(expectedResult, actualResult);
  }

  @Test
  void unlockPersonAfter5MinutesTest() {
    credentials.setLock(true);
    credentials.setLockTime(LocalDateTime.now().toString());
    credentials2.setLock(false);
    credentials2.setLockTime("");
    when(credentialsRepository.findByLogin("login")).thenReturn(Optional.of(credentials));
    when(credentialsRepository.save(credentials2)).thenReturn(credentials2);
    when(authEntryPointJwt.getIncorrectPasswordCounter()).thenReturn(3);
    boolean expectedResult =
        JwtPerson.build(credentials2.getPerson(), credentials2).isAccountNonLocked();
    boolean actualResult = userDetailsService.loadUserByUsername("login").isAccountNonLocked();
    assertEquals(expectedResult, actualResult);
  }
}
