package com.example.security;

import com.example.model.Credentials;
import com.example.model.Person;
import com.example.model.PersonRole;
import com.example.repository.CredentialsRepository;
import com.example.security.exceptionhandler.AuthenticationEntryPoint;
import com.example.security.userdetails.JwtUserDetails;
import com.example.security.userdetails.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Disabled;
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
  @Mock private AuthenticationEntryPoint authenticationEntryPoint;

  Person person =
      getPerson(
          123456L, "", "", "f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454", "", "", PersonRole.ROLE_USER);
  Credentials credentials =
      getCredentials("f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454", "", "",true, "","", person, false, "");
  Credentials credentials2 =
      getCredentials("f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454", "", "",true, "","", person, true, "");

  @Test
  @Disabled
  void lockPersonAfter5TimesIncorrectEntryTest() {
    when(credentialsRepository.findByLogin("login")).thenReturn(Optional.of(credentials));
//    when(authenticationEntryPoint.getIncorrectPasswordCounter()).thenReturn(5);
    when(credentialsRepository.save(any(Credentials.class))).thenReturn(credentials2);
    boolean expectedResult =
        JwtUserDetails.build(credentials2.getPerson(), credentials2).isAccountNonLocked();
    boolean actualResult = userDetailsService.loadUserByUsername("login").isAccountNonLocked();

    assertEquals(expectedResult, actualResult);
  }

  @Test
  @Disabled
  void unlockPersonAfter5MinutesTest() {
    credentials.setLock(true);
    credentials.setLockTime(LocalDateTime.now().toString());
    credentials2.setLock(false);
    credentials2.setLockTime("");
    when(credentialsRepository.findByLogin("login")).thenReturn(Optional.of(credentials));
    when(credentialsRepository.save(credentials2)).thenReturn(credentials2);
//    when(authenticationEntryPoint.getIncorrectPasswordCounter()).thenReturn(3);
    boolean expectedResult =
        JwtUserDetails.build(credentials2.getPerson(), credentials2).isAccountNonLocked();
    boolean actualResult = userDetailsService.loadUserByUsername("login").isAccountNonLocked();
    assertEquals(expectedResult, actualResult);
  }
}
