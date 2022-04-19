package com.example.authorizationservice.security;

import com.example.authorizationservice.model.Secure;
import com.example.authorizationservice.model.User;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
public class JwtUser implements UserDetails {

    private Long id;
    private Long phone;
    private String username;
    private String surname;
    private String patronymic;
    private String email;
    private String login;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    public JwtUser(Long id, Long phone, String username,String surname, String patronymic, String email,String login,String password,
                   Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.phone = phone;
        this.username = username;
        this.surname = surname;
        this.patronymic = patronymic;
        this.email = email;
        this.login = login;
        this.password = password;
        this.authorities = authorities;

    }
    public static JwtUser build(User user, Secure secure){
        List<GrantedAuthority> authorities =  List.of(new SimpleGrantedAuthority(user.getRole().name()));

        return new JwtUser(user.getId(),
                user.getPhone(),
                user.getName(),
                user.getSurname(),
                user.getPatronymic(),
                user.getEmail(),
                secure.getLogin(),
                secure.getPassword(),
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
        return true;
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
