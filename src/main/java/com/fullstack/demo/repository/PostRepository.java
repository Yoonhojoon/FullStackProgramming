package com.fullstack.demo.repository;

import com.fullstack.demo.entity.Category;
import com.fullstack.demo.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByOrderByCreatedAtDesc();
    List<Post> findByCategoriesContainingOrderByCreatedAtDesc(Category category);
}