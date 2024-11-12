package com.fullstack.demo.service;

import com.fullstack.demo.dto.PostCreateRequestDto;
import com.fullstack.demo.dto.PostResponseDto;
import com.fullstack.demo.entity.Post;
import com.fullstack.demo.entity.Category;
import com.fullstack.demo.entity.Board;
import com.fullstack.demo.repository.PostRepository;
import com.fullstack.demo.repository.CategoryRepository;
import com.fullstack.demo.repository.BoardRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final BoardRepository boardRepository;
    private final CategoryRepository categoryRepository;

    // 게시글 생성
    @Transactional
    public PostResponseDto createPost(Long boardId, PostCreateRequestDto requestDto) {
        // 게시판 존재 여부 확인
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("Board not found"));

        // 카테고리 ID 목록을 기반으로 카테고리들 찾기
        List<Category> categories = categoryRepository.findAllById(requestDto.getCategoryIds());

        // Post 엔티티 생성
        Post post = new Post();
        post.setPostTitle(requestDto.getPostTitle());
        post.setPostContent(requestDto.getPostContent());
        post.setThumbnailImageUrl(requestDto.getThumbnailImageUrl());
        post.setContentImageUrl(requestDto.getContentImageUrl());
        post.setPostCategory(requestDto.getPostCategory());
        post.setStartDate(requestDto.getStartDate());
        post.setEndDate(requestDto.getEndDate());
        post.setCategories(new HashSet<>(categories));  // 카테고리와 관계 설정

        post.setBoard(board);
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        post.setViewCount(0);  // 기본 조회수
        post.setLikeCount(0);  // 기본 좋아요 수

        // 게시글 저장
        Post savedPost = postRepository.save(post);

        // PostResponseDto로 반환
        return PostResponseDto.from(savedPost);
    }

    // 특정 게시판의 게시글 목록 조회
    public List<PostResponseDto> getBoardPosts(Long boardId) {
        // 게시판에 속한 게시글들 조회
        List<Post> posts = postRepository.findByBoard_boardId(boardId);

        // PostResponseDto로 변환하여 반환
        return posts.stream()
                .map(PostResponseDto::from)
                .collect(Collectors.toList());
    }

    // 게시글 조회수 증가
    @Transactional
    public void increaseViewCount(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        post.setViewCount(post.getViewCount() + 1);  // Post 엔티티에서 조회수 증가 처리
        postRepository.save(post);  // 변경된 게시글 저장
    }
}
