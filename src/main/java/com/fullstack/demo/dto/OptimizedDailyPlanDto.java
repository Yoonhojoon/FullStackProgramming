package com.fullstack.demo.dto;

import com.fullstack.demo.entity.DailyPlan;
import lombok.Data;

import java.util.List;

@Data
public class OptimizedDailyPlanDto
{
    private DailyPlan dailyPlan;
    private List<GuideDTO> guides;
}