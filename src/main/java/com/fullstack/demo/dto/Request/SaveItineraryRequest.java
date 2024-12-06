// SaveItineraryRequest.java
package com.fullstack.demo.dto.Request;

import com.fullstack.demo.dto.DailyPlanDto;
import com.fullstack.demo.entity.DailyPlan;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Schema(description = "여행 일정 저장 요청")
public class SaveItineraryRequest {
    @Schema(description = "일정 제목", example = "서울 3박 4일 여행")
    private String title;

    @Schema(description = "일정 설명", example = "가족과 함께하는 서울 여행")
    private String description;

    @Schema(description = "시작일", example = "2024-12-20")
    private LocalDate startDate;

    @Schema(description = "종료일", example = "2024-12-23")
    private LocalDate endDate;

    @Schema(description = "최적화된 일별 계획 목록")
    private List<DailyPlan> optimizedPlans;
}