package com.fullstack.demo.repository;

import com.fullstack.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // 추가적인 커스텀 쿼리 메서드가 필요하다면 여기 작성
    Boolean existsByEmail(String email);
}
