package com.fullstack.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TravelScheduleResponse {
    private List<DaySchedule> schedules;
}
