package com.fullstack.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "Post")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @Column(nullable = false, length = 50)
    private String postTitle;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String postContent;

    @Column(length = 255)
    private String thumbnailImageUrl;

    @Column(length = 255)
    private String contentImageUrl;

    @Column
    private Date startDate;

    @Column
    private Date endDate;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column
    private Integer viewCount;

    @Column
    private Integer likeCount;

    @Column(length = 255)
    private String postCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;  // Board와의 관계 설정


    @ManyToMany
    @JoinTable(
            name = "PostCategory",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new HashSet<>();


    // Getters and setters

    public void setBoard(Board board) {
        this.board = board;
    }

    // Getters and setters
}
