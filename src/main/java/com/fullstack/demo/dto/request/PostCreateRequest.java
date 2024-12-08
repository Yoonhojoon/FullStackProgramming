package com.fullstack.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class PostCreateRequest {
    @NotBlank
    private String postTitle;

    @NotBlank
    private String postContent;

    private String thumbnailImageUrl;
    private String contentImageUrl;
    private Long boardId;
    private Set<Long> categoryIds;
}