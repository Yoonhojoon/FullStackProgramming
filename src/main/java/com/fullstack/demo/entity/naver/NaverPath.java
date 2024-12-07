package com.fullstack.demo.entity.naver;

import lombok.Data;

import java.util.List;

@Data
public class NaverPath {
    private int distance;
    private int duration;
    private Integer waypointIndex;
    private List<List<Double>> coords;
}
