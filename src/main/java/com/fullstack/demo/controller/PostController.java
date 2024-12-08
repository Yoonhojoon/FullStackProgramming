package com.fullstack.demo.controller;

import com.fullstack.demo.dto.request.PostCreateRequest;
import com.fullstack.demo.entity.Board;
import com.fullstack.demo.entity.Category;
import com.fullstack.demo.entity.Post;
import com.fullstack.demo.service.CategoryService;
import com.fullstack.demo.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final CategoryService categoryService;


    // 게시글 목록 조회
    @GetMapping("/posts")
    public ResponseEntity<List<Post>> getPosts(
            @RequestParam(required = false) String categoryName) {
        if (categoryName != null) {
            return ResponseEntity.ok(postService.getPostsByCategory(categoryName));
        }
        return ResponseEntity.ok(postService.getAllPosts());
    }

    // 게시글 작성
    @PostMapping("/posts")
    public ResponseEntity<Post> createPost(@RequestBody PostCreateRequest request) {
        Post post = postService.createPost(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(post);
    }

    // 좋아요 토글
    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<Post> toggleLike(@PathVariable Long postId) {
        Post post = postService.toggleLike(postId);
        return ResponseEntity.ok(post);
    }

    // 조회수 증가
    @PostMapping("/posts/{postId}/view")
    public ResponseEntity<Post> incrementViewCount(@PathVariable Long postId) {
        Post post = postService.incrementViewCount(postId);
        return ResponseEntity.ok(post);
    }

    // 카테고리 목록 조회
    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }
}