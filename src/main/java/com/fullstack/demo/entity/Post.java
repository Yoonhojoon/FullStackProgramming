package com.fullstack.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
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

    @ManyToMany
    @JoinTable(
            name = "PostCategory",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new HashSet<>();

    // Getters and setters
}
