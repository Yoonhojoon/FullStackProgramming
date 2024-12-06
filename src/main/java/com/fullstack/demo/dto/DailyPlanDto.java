package com.fullstack.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;// DailyPlanDto.java

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Schema(description = "일일 여행 계획")
public class DailyPlanDto {
    @Schema(description = "일일 계획 ID")
    private Long id;

    @Schema(description = "일차", example = "1")
    private Integer dayNumber;

    @Schema(description = "여행 일정 ID")
    private Long itineraryId;

    @Schema(description = "숙소 정보")
    private DestinationDto accommodation;

    @Schema(description = "방문할 장소들")
    private List<DestinationDto> destinations;

    @Schema(description = "총 이동 거리 (km)", example = "15.5")
    private Double totalDistance;

    @Schema(description = "총 이동 시간 (분)", example = "120")
    private Integer totalTravelTime;
}
