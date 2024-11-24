package com.fullstack.demo.service;

import com.fullstack.demo.dto.UserCreateRequestDto;
import com.fullstack.demo.dto.UserResponseDto;
import com.fullstack.demo.entity.User;
import com.fullstack.demo.entity.UserRole;
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
        // 이메일 중복 체크
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다: " + requestDto.getEmail());
        }

        // DTO -> Entity 변환
        User user = User.builder()
                .username(requestDto.getUsername())
                .email(requestDto.getEmail())
                .googleId(requestDto.getGoogleId())
                .build();

        // 유저 저장
        User savedUser = userRepository.save(user);

        // 저장된 유저를 응답 DTO로 변환하여 반환
        return UserResponseDto.from(savedUser);
    }

    public UserResponseDto findById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return UserResponseDto.from(user);
    }
}