package com.fullstack.demo.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// Destination.java
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Destination {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long destinationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "daily_plan_id", nullable = false)
    private DailyPlan dailyPlan;

    @Column(length = 255)
    private String locationId;

    @Column
    private Integer orderInDay;

    @Column
    private Float distanceToNext;

    @Builder
    public Destination(DailyPlan dailyPlan, String locationId, Integer orderInDay, Float distanceToNext) {
        this.dailyPlan = dailyPlan;
        this.locationId = locationId;
        this.orderInDay = orderInDay;
        this.distanceToNext = distanceToNext;
    }
}