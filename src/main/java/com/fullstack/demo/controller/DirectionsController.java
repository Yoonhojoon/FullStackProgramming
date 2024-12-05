package com.fullstack.demo.controller;

import com.fullstack.demo.dto.TripOptimizationRequest;
import com.fullstack.demo.entity.DailyPlan;
import com.fullstack.demo.service.GoogleMapsService;
import com.fullstack.demo.service.TripPlanningService;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/directions")
public class DirectionsController {

    private final GoogleMapsService googleMapsService;
    private final TripPlanningService tripPlanningService;

    public DirectionsController(GoogleMapsService googleMapsService,
                                TripPlanningService tripPlanningService) {
        this.googleMapsService = googleMapsService;
        this.tripPlanningService = tripPlanningService;
    }

    @GetMapping("/simple")
    public DirectionsResult getDirections(
            @RequestParam String origin,
            @RequestParam String destination,
            @RequestParam(defaultValue = "DRIVING") TravelMode travelMode) throws Exception {
        return googleMapsService.getDirections(origin, destination, travelMode);
    }

    @PostMapping("/optimize")
    public DirectionsResult getOptimizedRoute(
            @RequestParam String origin,
            @RequestParam String destination,
            @RequestParam String[] waypoints,
            @RequestParam(defaultValue = "DRIVING") TravelMode travelMode) throws Exception {
        return googleMapsService.getOptimizedRoute(origin, destination, waypoints, travelMode);
    }

    @PostMapping("/trip")
    public List<DailyPlan> optimizeTrip(
            @RequestBody TripOptimizationRequest request) {
        return tripPlanningService.optimizeTrip(
                request.getItinerary(),
                request.getAccommodationsByDay(),
                request.getSpots(),
                request.getTravelMode()
        );
    }
}

