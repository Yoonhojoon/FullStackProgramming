package com.fullstack.demo.controller;

import com.fullstack.demo.dto.response.DirectionsResponse;
import com.fullstack.demo.entity.naver.LatLng;
import com.fullstack.demo.service.DirectionsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/directions")
@RequiredArgsConstructor
public class DirectionsController {

    private final DirectionsService directionsService;

    @GetMapping
    public ResponseEntity<DirectionsResponse> getDirections(
            @RequestParam("startLat") double startLat,
            @RequestParam("startLng") double startLng,
            @RequestParam("goalLat") double goalLat,
            @RequestParam("goalLng") double goalLng,
            @RequestParam(value = "waypoints", required = false) List<LatLng> waypoints) {

        LatLng start = new LatLng(startLat, startLng);
        LatLng goal = new LatLng(goalLat, goalLng);

        return ResponseEntity.ok(directionsService.getDirections(start, goal, waypoints));
    }
}