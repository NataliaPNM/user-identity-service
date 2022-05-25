package com.example.service;

import com.example.dto.UserContactsDto;
import com.example.mapper.UserContactsDtoMapper;
import com.example.model.User;
import com.example.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserContactsDtoMapper userContactsDtoMapper;

    public UserContactsDto sendResult(Long userId) {
        User user = userRepository.getById(userId);
        return userContactsDtoMapper.toUserContactsDto(userId, user.getEmail(), user.getPhone());
    }
}
