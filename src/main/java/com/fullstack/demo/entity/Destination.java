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

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false)
    private Integer orderInDay;

    @Column
    private Double distanceToNext;  // km 단위

    @Column
    private Integer timeToNext;  // 다음 장소까지 이동 시간(분)

    @Builder
    public Destination(DailyPlan dailyPlan, String name, String address,
                       Double latitude, Double longitude, Integer orderInDay,
                       Double distanceToNext, Integer timeToNext) {
        this.dailyPlan = dailyPlan;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.orderInDay = orderInDay;
        this.distanceToNext = distanceToNext;
        this.timeToNext = timeToNext;
    }

    void setDailyPlan(DailyPlan dailyPlan) {
        this.dailyPlan = dailyPlan;
    }
}