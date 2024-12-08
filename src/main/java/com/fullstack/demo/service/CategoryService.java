package com.fullstack.demo.service;

import com.fullstack.demo.dto.request.CategoryCreateRequest;
import com.fullstack.demo.entity.Category;
import com.fullstack.demo.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category createCategory(CategoryCreateRequest request) {
        // 동일한 이름의 카테고리가 있는지 확인
        if (categoryRepository.existsByCategoryName(request.getCategoryName())) {
            throw new IllegalArgumentException("이미 존재하는 카테고리 이름입니다.");
        }

        Category category = new Category();
        category.setCategoryName(request.getCategoryName());
        category.setImageUrl(request.getImageUrl());
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());

        return categoryRepository.save(category);
    }
}