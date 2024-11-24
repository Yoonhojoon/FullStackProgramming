package com.fullstack.demo.dto;

import com.fullstack.demo.entity.Post;
import com.fullstack.demo.entity.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "게시글 응답 DTO")
public class PostResponseDto {

    @Schema(description = "게시글 ID", example = "1")
    private Long postId;

    @Schema(description = "게시글 제목", example = "여행 후기")
    private String postTitle;

    @Schema(description = "게시글 내용", example = "즐거운 여행이었습니다.")
    private String postContent;

    @Schema(description = "썸네일 이미지 URL", example = "http://example.com/thumbnail.jpg")
    private String thumbnailImageUrl;

    @Schema(description = "컨텐츠 이미지 URL", example = "http://example.com/content.jpg")
    private String contentImageUrl;

    @Schema(description = "시작 날짜", example = "2024-03-01")
    private Date startDate;

    @Schema(description = "종료 날짜", example = "2024-03-05")
    private Date endDate;

    @Schema(description = "작성일시")
    private LocalDateTime createdAt;

    @Schema(description = "수정일시")
    private LocalDateTime updatedAt;

    @Schema(description = "조회수", example = "42")
    private Integer viewCount;

    @Schema(description = "좋아요 수", example = "15")
    private Integer likeCount;

    @Schema(description = "게시글 카테고리", example = "여행")
    private String postCategory;

    @Schema(description = "게시판 ID", example = "1")
    private Long boardId;

    @Schema(description = "카테고리 목록")
    private Set<CategoryDto> categories;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CategoryDto {
        @Schema(description = "카테고리 ID", example = "1")
        private Long categoryId;

        @Schema(description = "카테고리 이름", example = "국내여행")
        private String categoryName;
    }

    // Entity를 DTO로 변환하는 정적 팩토리 메서드
    public static PostResponseDto from(Post post) {
        Set<CategoryDto> categoryDtos = post.getCategories().stream()
                .map(category -> CategoryDto.builder()
                        .categoryId(category.getCategoryId())
                        .categoryName(category.getCategoryName())
                        .build())
                .collect(Collectors.toSet());

        return PostResponseDto.builder()
                .postId(post.getPostId())
                .postTitle(post.getPostTitle())
                .postContent(post.getPostContent())
                .thumbnailImageUrl(post.getThumbnailImageUrl())
                .contentImageUrl(post.getContentImageUrl())
                .startDate(post.getStartDate())
                .endDate(post.getEndDate())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .postCategory(post.getPostCategory())
                .boardId(post.getBoard().getBoardId())
                .categories(categoryDtos)
                .build();
    }
}