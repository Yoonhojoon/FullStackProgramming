package com.fullstack.demo.service;

import com.fullstack.demo.dto.UserCreateRequestDto;
import com.fullstack.demo.dto.UserResponseDto;
import com.fullstack.demo.entity.User;
import com.fullstack.demo.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

// UserService.java
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public UserResponseDto createUser(UserCreateRequestDto requestDto) {
        User user = requestDto.toEntity();
        User savedUser = userRepository.save(user);
        return UserResponseDto.from(savedUser);
    }

    public UserResponseDto findById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return UserResponseDto.from(user);
    }
}