// TripController.java
package com.fullstack.demo.controller;

import com.fullstack.demo.dto.*;
import com.fullstack.demo.dto.Request.SaveItineraryRequest;
import com.fullstack.demo.entity.*;
import com.fullstack.demo.service.TripPlanningService;
import com.google.maps.model.TravelMode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
@Tag(name = "Trip", description = "여행 일정 관련 API")
public class TripController {
    private final TripPlanningService tripPlanningService;

    @Operation(summary = "여행 일정 최적화")
    @PostMapping("/optimize")
    public ResponseEntity<List<DailyPlan>> optimizeTrip(@RequestBody OptimizeTripRequest request) {
        Map<Integer, Destination> accommodations = request.getAccommodationsByDay();
        List<Destination> spots = request.getSpots();
        TravelMode travelMode = TravelMode.valueOf(request.getTravelMode());

        List<DailyPlan> optimizedPlans = tripPlanningService.optimizeTrip(
                new Itinerary(),
                accommodations,
                spots,
                travelMode
        );

        return ResponseEntity.ok(optimizedPlans);
    }

    @Operation(summary = "최적화된 일정 저장")
    @PostMapping("/save")
    public ResponseEntity<Long> saveItinerary(
            @RequestBody SaveItineraryRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        Itinerary savedItinerary = tripPlanningService.saveOptimizedItinerary(
                currentUser,
                request.getTitle(),
                request.getDescription(),
                request.getStartDate(),
                request.getEndDate(),
                request.getOptimizedPlans()
        );

        return ResponseEntity.ok(savedItinerary.getItineraryId());
    }






}