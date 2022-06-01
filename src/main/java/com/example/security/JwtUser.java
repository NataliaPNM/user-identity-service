package com.example.security;

import com.example.model.Secure;
import com.example.model.User;
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
    private String login;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    public JwtUser(Long id, Long phone, String username,String surname, String login,String password,
                   Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.phone = phone;
        this.username = username;
        this.surname = surname;
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
