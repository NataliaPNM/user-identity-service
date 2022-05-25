package com.example.service;

import com.example.dto.ChangePasswordRequest;
import com.example.dto.LoginRequest;
import com.example.dto.LoginResponseDto;
import com.example.dto.RequestNewTokensDto;
import com.example.mapper.LoginResponseMapper;
import com.example.model.Secure;
import com.example.repository.SecureRepository;
import com.example.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final SecureRepository secureRepository;
    private final LoginResponseMapper loginResponseMapper;

    public LoginResponseDto login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getLogin(), loginRequest.getPassword()));

        String jwt = jwtUtils.generateJwtToken(authentication);
        String refreshJwt = jwtUtils.generateRefreshToken(authentication);
        secureRepository.findByLogin(loginRequest.getLogin()).ifPresent(secure -> {
                    secure.setRefreshToken(refreshJwt);
                    secureRepository.save(secure);
                }
        );
        return loginResponseMapper.toLoginResponseDto(jwt, refreshJwt);
    }

    public LoginResponseDto refreshJwt(RequestNewTokensDto authHeader) {
        String token = authHeader.getRefreshToken();
        Secure secureData = secureRepository.findByRefreshToken(token)
                .orElseThrow(() -> new AuthenticationServiceException("Auth failed! Not found such user"));

        if (!jwtUtils.validateJwtToken(token)) {
            throw new AuthenticationServiceException("Auth failed");
        }
        String login = jwtUtils.getLoginFromJwtToken(token);
        String accessToken = jwtUtils.generateJwtToken(login);
        String refreshToken = jwtUtils.generateRefreshToken(login);

        secureData.setRefreshToken(refreshToken);
        secureRepository.save(secureData);

        return loginResponseMapper.toLoginResponseDto(accessToken, refreshToken);
    }

    public String changePassword(ChangePasswordRequest changePasswordRequest, Long userId) {
        Secure secureData = secureRepository.findById(userId)
                .orElseThrow(() -> new AuthenticationServiceException("Auth failed! Not found such user"));
        secureData.setPassword(new BCryptPasswordEncoder().encode(changePasswordRequest.getNewPassword()));
        secureRepository.save(secureData);

        return "Password changed!";
    }
}
