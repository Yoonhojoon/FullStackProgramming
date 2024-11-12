package com.fullstack.demo.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "Users")
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(length = 10, nullable = false)
    private String username;     // 앱에서 사용할 닉네임

    @Column(length = 30, nullable = false, unique = true)
    private String googleId;     // Google에서 제공하는 고유 ID

    @Column(length = 255, nullable = false, unique = true)
    private String email;        // Google 계정 이메일

    @Column(length = 10, nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @OneToMany(mappedBy = "user")
    private List<Itinerary> itineraries = new ArrayList<>();

    @Builder
    public User(String username, String googleId, String email) {
        this.username = username;
        this.googleId = googleId;
        this.email = email;
        this.role = UserRole.USER;  // 기본값으로 USER 역할 부여
    }
}