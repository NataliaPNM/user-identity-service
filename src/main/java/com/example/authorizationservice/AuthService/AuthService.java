package com.example.authorizationservice.AuthService;

import com.example.authorizationservice.dto.ChangePasswordRequest;
import com.example.authorizationservice.dto.LoginRequest;
import com.example.authorizationservice.dto.LoginResponseDto;
import com.example.authorizationservice.model.Secure;
import com.example.authorizationservice.repository.SecureRepository;
import com.example.authorizationservice.security.JwtUser;
import com.example.authorizationservice.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

        private final AuthenticationManager authenticationManager;
        private final JwtUtils jwtUtils;
        private final SecureRepository secureRepository;


        public LoginResponseDto login(LoginRequest loginRequest) {

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getLogin(), loginRequest.getPassword()));

            String jwt = jwtUtils.generateJwtToken(authentication);
            String refreshJwt = jwtUtils.generateRefreshToken(authentication);
            LoginResponseDto loginResponseDto = new LoginResponseDto();
            loginResponseDto.setRefreshToken(refreshJwt);
            loginResponseDto.setToken(jwt);
            return loginResponseDto;
        }

        public LoginResponseDto refreshJwt(String authHeader) {
            var token = jwtUtils.parseJwt(authHeader);
            if (!jwtUtils.validateJwtToken(token)) {
                throw new RuntimeException("Auth failed"); // TODO: custom exception
            }

            String email = jwtUtils.getLoginFromJwtToken(token);

            var accessToken = jwtUtils.generateJwtToken(email);
            var refreshToken = jwtUtils.generateRefreshToken(email);
            LoginResponseDto loginResponseDto = new LoginResponseDto();
            loginResponseDto.setRefreshToken(refreshToken);
            loginResponseDto.setToken(accessToken);
            return loginResponseDto;
        }

        public void changePassword(ChangePasswordRequest changePasswordRequest, Long userId) {
            Secure secure = secureRepository.findById(userId).get();
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            secure.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
            secureRepository.save(secure);

        }
}
