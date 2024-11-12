package com.fullstack.demo.repository;

import com.fullstack.demo.entity.Itinerary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItineraryRepository extends JpaRepository<Itinerary, Long> {

    // 특정 사용자에 대한 여행 일정을 조회
    List<Itinerary> findByUser_userId(Long userId);
}
