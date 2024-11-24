package com.fullstack.demo.dto;

import com.fullstack.demo.entity.User;
import com.fullstack.demo.entity.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "사용자 응답 DTO")
public class UserResponseDto {
    @Schema(description = "사용자 ID", example = "1")
    private Long userId;

    @Schema(description = "사용자 이름", example = "홍길동")
    private String username;

    @Schema(description = "Google 고유 ID", example = "123456789")
    private String googleId;

    @Schema(description = "사용자 이메일", example = "user@gmail.com")
    private String email;

    @Schema(description = "사용자 역할", example = "USER")
    private UserRole role;

    // Entity를 DTO로 변환하는 정적 팩토리 메서드
    public static UserResponseDto from(User user) {
        return UserResponseDto.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .googleId(user.getGoogleId())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}