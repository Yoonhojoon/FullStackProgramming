package com.fullstack.demo.controller;

import com.fullstack.demo.entity.Place;
import com.fullstack.demo.service.PlaceService;
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
@RequestMapping("/api/places")
public class PlaceController {

    private final PlaceService placeService;

    @Autowired
    public PlaceController(PlaceService placeService) {
        this.placeService = placeService;
    }

    @PostMapping
    @Operation(summary = "장소 등록", description = "Google Places API로부터 받은 장소 정보를 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "장소 등록 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 장소")
    })
    public ResponseEntity<Place> createPlace(@RequestBody Place place) {
        Place savedPlace = placeService.save(place);
        return ResponseEntity.ok(savedPlace);
    }
}