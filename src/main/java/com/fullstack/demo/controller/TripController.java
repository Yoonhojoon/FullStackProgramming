// TripController.java
package com.fullstack.demo.controller;

import com.fullstack.demo.dto.*;

import com.fullstack.demo.dto.request.OptimizeTripRequest;
import com.fullstack.demo.entity.*;
import com.fullstack.demo.service.TripPlanningService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Trip", description = "여행 일정 관련 API")
public class TripController {
    private final TripPlanningService tripPlanningService;

    @Operation(summary = "여행 일정 최적화")
    @PostMapping("/optimize")
    public ResponseEntity<List<UITripItemDTO>> optimizeTrip(@RequestBody @Valid OptimizeTripRequest request) {
        try {
            Map<Integer, Destination> accommodations = request.getAccommodationsByDay();
            List<Destination> spots = request.getSpots();

            // 기본적인 유효성 검사
            if (accommodations == null || accommodations.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            List<OptimizedDailyPlanDto> optimizedPlans = tripPlanningService.optimizeTrip(
                    new Itinerary(),
                    accommodations,
                    spots
            );

            List<UITripItemDTO> uiItems = new ArrayList<>();
            optimizedPlans.forEach(plan ->
                    uiItems.addAll(tripPlanningService.convertToUITripItems(plan))
            );

            // 응답 로그 추가
            log.info("Optimized trip response: {}", uiItems);
            // 또는 더 자세한 로그를 위해
            uiItems.forEach(item ->
                    log.info("Item: name={}, type={}, guide={}",
                            item.getName(),
                            item.getType(),
                            item.getGuide())
            );

            return ResponseEntity.ok(uiItems);
        } catch (Exception e) {
            // 로깅 추가
            return ResponseEntity.internalServerError().build();
        }
    }

//    @Operation(summary = "여행 일정 상세 조회")
//    @GetMapping("/{tripId}/schedule")
//    public ResponseEntity<TravelScheduleResponse> getTripSchedule(@PathVariable Long tripId) {
//        try {
//            // 1. tripId로 DailyPlan들 조회
//            List<DailyPlan> dailyPlans = tripPlanningService.getDailyPlans(tripId);
//
//            // 2. DailyPlan들을 TravelScheduleResponse로 변환
//            TravelScheduleResponse scheduleResponse = tripPlanningService.convertToScheduleResponse(dailyPlans);
//
//            return ResponseEntity.ok(scheduleResponse);
//        } catch (EntityNotFoundException e) {
//            return ResponseEntity.notFound().build();
//        } catch (Exception e) {
//            log.error("Failed to get trip schedule", e);
//            return ResponseEntity.internalServerError().build();
//        }
//    }
}
