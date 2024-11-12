package com.fullstack.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Table(name = "Category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    @Column(nullable = false, length = 50)
    private String categoryThema;

    @Column(length = 50)
    private String categoryName;

    @Column(columnDefinition = "TEXT")
    private String categoryNumber;

    @Column(nullable = false)
    private Integer displayOrder;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(length = 255)
    private String imageUrl;

    @ManyToMany(mappedBy = "categories")
    private Set<Post> posts = new HashSet<>();

    // Getters and setters
}
