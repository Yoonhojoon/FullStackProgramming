// TripController.java
package com.fullstack.demo.controller;

import com.fullstack.demo.dto.*;

import com.fullstack.demo.dto.request.OptimizeTripRequest;
import com.fullstack.demo.dto.response.DailyScheduleResponse;
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
    public ResponseEntity<List<DailyScheduleResponse>> optimizeTrip(@RequestBody @Valid OptimizeTripRequest request) {
        try {
            Map<Integer, Destination> accommodations = request.getAccommodationsByDay();
            List<Destination> spots = request.getSpots();

            if (accommodations == null || accommodations.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            List<OptimizedDailyPlanDto> optimizedPlans = tripPlanningService.optimizeTrip(
                    new Itinerary(),
                    accommodations,
                    spots
            );

            List<DailyScheduleResponse> response = new ArrayList<>();

            // 이전 날짜의 계획을 추적하면서 변환
            DailyPlan previousPlan = null;
            for (OptimizedDailyPlanDto plan : optimizedPlans) {
                List<UITripItemDTO> items = tripPlanningService.convertToUITripItems(plan, previousPlan);
                response.add(new DailyScheduleResponse(
                        plan.getDailyPlan().getDayNumber(),
                        items
                ));
                previousPlan = plan.getDailyPlan();
            }

            log.info("Optimized trip response by day: {}", response);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to optimize trip", e);
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
