package com.fullstack.demo.controller;

import com.fullstack.demo.dto.request.GoogleLoginRequest;
import com.fullstack.demo.entity.User;
import com.fullstack.demo.entity.UserRole;
import com.fullstack.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;

    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestBody GoogleLoginRequest request) {
        User user = userRepository.findByGoogleId(request.getGoogleId())
                .orElseGet(() -> createUser(request));

        return ResponseEntity.ok().build();
    }

    private User createUser(GoogleLoginRequest request) {
        User user = User.builder()
                .googleId(request.getGoogleId())
                .email(request.getEmail())
                .username(request.getUsername())
                .role(UserRole.USER)
                .build();

        return userRepository.save(user);
    }
}

