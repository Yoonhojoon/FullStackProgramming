package com.fullstack.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;


@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class) // Auditing 기능 활성화
public abstract class BaseTimeEntity {

    @CreatedDate
    private LocalDateTime createdAt; // 엔티티 생성 시 자동 값 설정

    @LastModifiedDate
    private LocalDateTime updatedAt; // 엔티티 업데이트 시 자동 값 설정
}
