package com.fullstack.demo.repository;

import com.fullstack.demo.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // 추가적인 커스텀 쿼리 메서드가 필요하다면 여기 작성
}
