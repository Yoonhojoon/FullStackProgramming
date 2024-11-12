package com.fullstack.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Date;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostCreateRequestDto {

    private String postTitle;
    private String postContent;
    private String thumbnailImageUrl;
    private String contentImageUrl;
    private String postCategory;
    private Date startDate;
    private Date endDate;
    private List<Long> categoryIds;  // 카테고리 IDs 목록
}
