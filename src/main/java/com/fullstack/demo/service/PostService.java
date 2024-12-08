package com.fullstack.demo.service;

import com.fullstack.demo.dto.request.PostCreateRequest;
import com.fullstack.demo.entity.Board;
import com.fullstack.demo.entity.Category;
import com.fullstack.demo.entity.Post;
import com.fullstack.demo.exception.ResourceNotFoundException;
import com.fullstack.demo.repository.BoardRepository;
import com.fullstack.demo.repository.CategoryRepository;
import com.fullstack.demo.repository.PostRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {
    private final PostRepository postRepository;
    private final BoardRepository boardRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<Post> getAllPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public List<Post> getPostsByCategory(String categoryName) {
        Category category = categoryRepository.findByCategoryName(categoryName)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        return postRepository.findByCategoriesContainingOrderByCreatedAtDesc(category);
    }

    public Post createPost(PostCreateRequest request) {
        Board board = new Board();
        board.setBoardId(1L);  // 또는 하드코딩된 ID
//        Board board = boardRepository.findById(request.getBoardId())
//                .orElseThrow(() -> new ResourceNotFoundException("Board not found"));

        Set<Category> categories = new HashSet<>();
        if (request.getCategoryIds() != null) {
            categories = categoryRepository.findAllById(request.getCategoryIds())
                    .stream()
                    .collect(Collectors.toSet());
        }

        Post post = new Post();
        post.setPostTitle(request.getPostTitle());
        post.setPostContent(request.getPostContent());
        post.setThumbnailImageUrl(request.getThumbnailImageUrl());
        post.setContentImageUrl(request.getContentImageUrl());
        post.setBoard(board);
        post.setCategories(categories);
        post.setViewCount(0);
        post.setLikeCount(0);
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());

        return postRepository.save(post);
    }

    public Post toggleLike(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        post.setLikeCount(post.getLikeCount() + 1);
        return postRepository.save(post);
    }

    public Post incrementViewCount(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        post.setViewCount(post.getViewCount() + 1);
        return postRepository.save(post);
    }
}

