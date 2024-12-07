package com.fullstack.demo.entity.naver;

import com.fullstack.demo.dto.response.DirectionsResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OptimizedRoute {
    private int[] waypointOrder;
    private DirectionsResponse directions;
}