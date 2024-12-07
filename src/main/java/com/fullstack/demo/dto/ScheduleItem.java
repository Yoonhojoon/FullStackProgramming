package com.fullstack.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class ScheduleItem {
    private String placeName;          // 장소명
    private LocalTime startTime;       // 시작 시간
    private LocalTime endTime;         // 종료 시간
    private int transitTime;           // 이동 시간(분)
    private String transitType = "차량"; // 이동 수단
    private String color;              // UI 색상 구분
}