package com.example.security;

import com.example.model.Credentials;
import com.example.model.Person;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Data
public class JwtPerson implements UserDetails {

  private UUID id;
  private Long phone;
  private String username;
  private String surname;
  private String login;
  private String password;
  private static boolean nonLocked;
  private Collection<? extends GrantedAuthority> authorities;

  public JwtPerson(
      UUID id,
      Long phone,
      String username,
      String surname,
      String login,
      String password,
      Collection<? extends GrantedAuthority> authorities) {
    this.id = id;
    this.phone = phone;
    this.username = username;
    this.surname = surname;
    this.login = login;
    this.password = password;
    this.authorities = authorities;
  }
  public static JwtPerson build(Person person, Credentials credentials) {
    List<GrantedAuthority> authorities =
        List.of(new SimpleGrantedAuthority(person.getRole().name()));
    nonLocked= !credentials.isLock();
    return new JwtPerson(
        person.getPersonId(),
        person.getPhone(),
        person.getName(),
        person.getSurname(),
        credentials.getLogin(),
        credentials.getPassword(),
        authorities);
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return nonLocked;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
