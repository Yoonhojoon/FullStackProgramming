package com.fullstack.demo.controller;

import com.fullstack.demo.entity.Destination;
import com.fullstack.demo.entity.DestinationType;
import com.fullstack.demo.service.DestinationService;
import com.fullstack.demo.dto.DestinationRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/destinations")
public class DestinationController {

    private final DestinationService destinationService;

    @Autowired
    public DestinationController(DestinationService destinationService) {
        this.destinationService = destinationService;
    }

    @PostMapping
    @Operation(summary = "여행지 등록", description = "Google Places API로부터 받은 여행지 정보를 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "여행지 등록 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 여행지")
    })
    public ResponseEntity<?> addDestination(@Valid @RequestBody DestinationRequest request) {
        Destination destination = Destination.builder()
                .type(DestinationType.valueOf(request.getType().toUpperCase())) // 문자열 대문자 변환
                .name(request.getName())
                .address(request.getAddress())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .transitDetails(request.getTransitDetails())
                .lastTransitDetails(request.getLastTransitDetails())
                .drivingDetails(request.getDrivingDetails())
                .lastDrivingDetails(request.getLastDrivingDetails())
                .orderInDay(request.getOrderInDay())
                .distanceToNext(request.getDistanceToNext())
                .timeToNext(request.getTimeToNext())
                .build();

        destinationService.saveDestination(destination);
        return ResponseEntity.status(201).body("Destination added successfully");
    }
}
