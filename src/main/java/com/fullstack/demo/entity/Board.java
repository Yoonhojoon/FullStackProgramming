package com.fullstack.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "board")
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardId;  // 게시판 ID

    @Column(nullable = false, length = 50)
    private String boardName;  // 게시판 이름

    @Column(nullable = false)
    private LocalDateTime createdAt;  // 생성 시간

    @Column(nullable = false)
    private LocalDateTime updatedAt;  // 업데이트 시간

    // 게시판에 속한 게시글들
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();  // 게시글 목록

    // 생성자
    public Board(String boardName) {
        this.boardName = boardName;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // 게시판 이름을 변경하는 메서드
    public void updateBoardName(String boardName) {
        this.boardName = boardName;
        this.updatedAt = LocalDateTime.now();
    }

    // 게시판 생성일과 수정일을 자동 설정하기 위한 메서드
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
