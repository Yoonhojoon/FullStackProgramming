package com.fullstack.demo.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;
import java.util.List;


@Data
@Builder
public class GuideDTO {
    private int distance;
    private int duration;
    private String instructions;
    private int type;
    private int pointIndex;
}