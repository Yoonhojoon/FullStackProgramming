package com.fullstack.demo.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;
import java.util.List;

@Data
@Builder
public class UITripItemDTO {
    private String name;
    private String address;
    private String type;
    private String color;
    private Integer travelTimeMinutes;
    private Double distanceToNext;
    private List<GuideDTO> guides;
}