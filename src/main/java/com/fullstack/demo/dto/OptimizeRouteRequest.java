package com.fullstack.demo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fullstack.demo.entity.Place;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

// OptimizeRouteRequest.java
@Data
public class OptimizeRouteRequest {
    private String title;

    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    private List<Place> places;
}