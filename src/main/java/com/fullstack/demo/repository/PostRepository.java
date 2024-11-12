package com.fullstack.demo.repository;

import com.fullstack.demo.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    // 특정 게시판에 속하는 게시글을 조회하는 메서드
    List<Post> findByBoard_boardId(Long boardId);  // boardId를 통해 게시글 목록 조회

    // 추가적인 메서드가 필요하면 여기다 작성할 수 있습니다.
}
