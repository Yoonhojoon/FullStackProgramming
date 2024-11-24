package com.fullstack.demo.controller;

import com.fullstack.demo.dto.PostCreateRequestDto;
import com.fullstack.demo.dto.PostResponseDto;
import com.fullstack.demo.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// PostController.java
@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
@Tag(name = "Post", description = "게시글 관련 API")
public class PostController {
    private final PostService postService;

    @Operation(summary = "게시글 작성", description = "새로운 게시글을 작성합니다.")
    @PostMapping("/boards/{boardId}")
    @ResponseStatus(HttpStatus.CREATED)
    public PostResponseDto createPost(
            @PathVariable Long boardId,
            @RequestBody @Valid PostCreateRequestDto requestDto) {
        return postService.createPost(boardId, requestDto);
    }

    @Operation(summary = "게시판의 게시글 목록 조회", description = "특정 게시판의 모든 게시글을 조회합니다.")
    @GetMapping("/boards/{boardId}")
    public List<PostResponseDto> getBoardPosts(@PathVariable Long boardId) {
        return postService.getBoardPosts(boardId);
    }

    @Operation(summary = "게시글 조회수 증가", description = "게시글의 조회수를 증가시킵니다.")
    @PostMapping("/{postId}/view")
    @ResponseStatus(HttpStatus.OK)
    public void increaseViewCount(@PathVariable Long postId) {
        postService.increaseViewCount(postId);
    }
}