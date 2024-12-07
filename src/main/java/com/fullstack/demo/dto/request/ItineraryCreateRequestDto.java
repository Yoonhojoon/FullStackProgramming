package com.fullstack.demo.dto.request;

import com.fullstack.demo.entity.Itinerary;
import com.fullstack.demo.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ItineraryCreateRequestDto {

    private Long userId;       // 사용자 ID
    private LocalDate startDate;  // 여행 시작일
    private LocalDate endDate;    // 여행 종료일

    // DTO를 엔티티로 변환하는 메서드
    public Itinerary toEntity(User user) {
        return Itinerary.builder()
                .user(user)
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }
}