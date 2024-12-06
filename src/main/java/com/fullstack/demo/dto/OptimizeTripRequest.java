package com.fullstack.demo.dto;

import com.fullstack.demo.entity.Destination;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Schema(description = "여행 일정 최적화 요청")
public class OptimizeTripRequest {
    @Schema(description = "일자별 숙소 정보", example = "{1: {address: '서울시 강남구...'}}")
    private Map<Integer, Destination> accommodationsByDay;

    @Schema(description = "방문할 관광지 목록")
    private List<Destination> spots;

    @Schema(description = "이동 수단", example = "TRANSIT")
    private String travelMode;
}
