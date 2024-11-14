package com.fullstack.demo.repository;

import com.fullstack.demo.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    // 필요한 경우, 추가적인 메서드를 작성할 수 있습니다.
}
