package com.example.security;

import com.example.model.Secure;
import com.example.repository.SecureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {

    SecureRepository secureRepository;

    @Autowired
    public UserDetailsServiceImpl(SecureRepository secureRepository) {
        this.secureRepository = secureRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Secure secure = secureRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with login: " + login));

        return JwtUser.build(secure.getUser(), secure);
    }
}
