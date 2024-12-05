package com.fullstack.demo.controller;

import com.fullstack.demo.entity.Destination;
import com.fullstack.demo.entity.Place;
import com.fullstack.demo.service.DestinationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/destination")
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
    public ResponseEntity<Destination> createDestination(@RequestBody Destination destination) {
        Destination savedDestination = destinationService.save(destination);
        return ResponseEntity.ok(savedDestination);
    }
}