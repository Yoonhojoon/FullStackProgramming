package com.fullstack.demo.controller;

import com.fullstack.demo.service.RouteOptimizationService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/routes")
public class RouteController {
    private final RouteOptimizationService routeService;

    @PostMapping("/optimize")
    @Operation(summary = "여행 경로 최적화", description = "주어진 장소들을 여행 일수에 맞게 최적화합니다.")
    public ResponseEntity<OptimizedRouteResponse> optimizeRoute(@RequestBody OptimizeRouteRequest request) {
        return ResponseEntity.ok(routeService.optimizeRoute(request));
    }
}