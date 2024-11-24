package com.fullstack.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users") // 테이블 이름을 소문자로 매핑
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT와 연동
    private Long userId;

    @Column(length = 10, nullable = false)
    private String username; // 앱에서 사용할 닉네임

    @Column(length = 30, nullable = false, unique = true)
    private String googleId; // Google에서 제공하는 고유 ID

    @Column(length = 255, nullable = false, unique = true)
    private String email; // Google 계정 이메일

    @Enumerated(EnumType.STRING) // 열거형을 문자열로 저장
    @Column(length = 10, nullable = false)
    private UserRole role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Itinerary> itineraries = new ArrayList<>();

    @Builder
    public User (String username, String googleId, String email, UserRole role) {
        this.username = username;
        this.googleId = googleId;
        this.email = email;
        this.role = role != null ? role : UserRole.USER; // 기본값으로 USER 역할 부여

    }


}
