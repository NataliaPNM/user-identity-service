package com.example.service;

import com.example.dto.UserDto;
import com.example.model.User;
import com.example.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserDto sendResult(Long userId) {
        User user= userRepository.getById(userId);

        return UserDto.builder().id(userId).email(user.getEmail()).phone(user.getPhone()).build();
    }
}
