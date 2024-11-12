package com.fullstack.demo.dto;

import com.fullstack.demo.entity.User;
import com.fullstack.demo.entity.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "사용자 생성 요청 DTO")
public class UserCreateRequestDto {

    @NotBlank(message = "사용자 이름은 필수입니다")
    @Size(max = 10, message = "사용자 이름은 10자를 초과할 수 없습니다")
    @Schema(description = "사용자 이름", example = "홍길동")
    private String username;

    @NotBlank(message = "Google ID는 필수입니다")
    @Size(max = 30, message = "Google ID는 30자를 초과할 수 없습니다")
    @Schema(description = "Google 고유 ID", example = "123456789")
    private String googleId;

    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    @Schema(description = "사용자 이메일", example = "user@gmail.com")
    private String email;

    // DTO를 Entity로 변환하는 메소드
    public User toEntity() {
        return User.builder()
                .username(this.username)
                .googleId(this.googleId)
                .email(this.email)
                .build();
    }
}
