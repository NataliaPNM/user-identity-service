package com.example.service;

import com.example.dto.ChangePasswordRequest;
import com.example.dto.LoginRequest;
import com.example.dto.LoginResponseDto;
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
import java.util.Optional;


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

        return loginResponseMapper.toLoginResponseDto(jwt,refreshJwt);
    }

    public LoginResponseDto refreshJwt(String authHeader) {
        String token = jwtUtils.parseJwt(authHeader);
        if (!jwtUtils.validateJwtToken(token)) {
            throw new AuthenticationServiceException("Auth failed");
        }
        String email = jwtUtils.getLoginFromJwtToken(token);
        String accessToken = jwtUtils.generateJwtToken(email);
        String refreshToken = jwtUtils.generateRefreshToken(email);

        return loginResponseMapper.toLoginResponseDto(accessToken,refreshToken);
    }

    public void changePassword(ChangePasswordRequest changePasswordRequest, Long userId) {
        Optional<Secure> secureData = secureRepository.findById(userId);
         if(secureData.isPresent()) {
             Secure secure = secureData.get();
             secure.setPassword(new BCryptPasswordEncoder().encode(changePasswordRequest.getNewPassword()));
             secureRepository.save(secure);
         }
    }
}
