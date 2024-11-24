package com.fullstack.demo.dto;

import com.fullstack.demo.entity.Itinerary;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class ItineraryResponseDto {

    private Long itineraryId;  // 여행 일정 ID
    private Long userId;       // 사용자 ID
    private LocalDate startDate;  // 여행 시작일
    private LocalDate endDate;    // 여행 종료일

    // 엔티티를 DTO로 변환하는 메서드
    public static ItineraryResponseDto from(Itinerary itinerary) {
        return new ItineraryResponseDto(
                itinerary.getItineraryId(),
                itinerary.getUser().getUserId(),
                itinerary.getStartDate(),
                itinerary.getEndDate()
        );
    }
}
