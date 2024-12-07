package com.fullstack.demo.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UIPlanDTO {
    private int day;
    private List<UITripItemDTO> items;
    private int totalDistance;     // Double -> int로 변경
    private Integer totalTravelTime;
}
