package com.fullstack.demo.dto;

import com.fullstack.demo.entity.Category;
import com.fullstack.demo.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class PostResponseDto {

    private Long postId;  // 게시글 ID
    private String postTitle;  // 게시글 제목
    private String postContent;  // 게시글 내용
    private String thumbnailImageUrl;  // 썸네일 이미지 URL
    private String contentImageUrl;  // 내용 이미지 URL
    private LocalDateTime createdAt;  // 게시글 작성 시간
    private LocalDateTime updatedAt;  // 게시글 수정 시간
    private Integer viewCount;  // 게시글 조회수
    private Integer likeCount;  // 게시글 좋아요 수
    private String postCategory;  // 게시글 카테고리
    private Set<String> categories;  // 카테고리 리스트

    // 엔티티를 DTO로 변환하는 메서드
    public static PostResponseDto from(Post post) {
        return new PostResponseDto(
                post.getPostId(),
                post.getPostTitle(),
                post.getPostContent(),
                post.getThumbnailImageUrl(),
                post.getContentImageUrl(),
                post.getCreatedAt(),
                post.getUpdatedAt(),
                post.getViewCount(),
                post.getLikeCount(),
                post.getPostCategory(),
                post.getCategories().stream()
                    .map(Category::getCategoryName) // 카테고리 이름만 추출
                .collect(Collectors.toSet())
        );
    }
}
