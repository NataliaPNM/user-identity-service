package com.example.security.userdetails;

import com.example.exception.NotFoundException;
import com.example.model.Credentials;
import com.example.service.CredentialsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final CredentialsService credentialsService;

    @Override
    public UserDetails loadUserByUsername(String login) throws NotFoundException {
        Credentials credentials = credentialsService.findByLogin(login);

        return JwtUserDetails.build(credentials.getPerson(), credentials);
    }

}
