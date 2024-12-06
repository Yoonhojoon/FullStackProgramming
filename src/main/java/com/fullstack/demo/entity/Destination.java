package com.fullstack.demo.entity;

import com.fullstack.demo.entity.DailyPlan;
import jakarta.persistence.*;
import lombok.*;
import com.fullstack.demo.entity.DestinationType;

// Destination.java
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Destination {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long destinationId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DestinationType type;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    @Column(length = 1000)
    private String transitDetails;  // 대중교통 이용시 이 장소로 오는 경로

    @Column(length = 1000)
    private String lastTransitDetails;  // 대중교통 이용시 이 장소에서 숙소로 가는 경로

    @Column(length = 1000)
    private String drivingDetails;  // 자동차 이용시 이 장소로 오는 경로

    @Column(length = 1000)
    private String lastDrivingDetails;  // 자동차 이용시 이 장소에서 숙소로 가는 경로

    private Integer orderInDay;
    private Double distanceToNext;
    private Integer timeToNext;

    @ManyToOne
    @JoinColumn(name = "daily_plan_id")
    private DailyPlan dailyPlan;

    @Builder
    public Destination(String name, String address, Double latitude, Double longitude, DestinationType type) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.type = type;
    }


    public void setDailyPlan(DailyPlan dailyPlan) {
        this.dailyPlan = dailyPlan;
    }

    public void updateOrderAndDistanceInfo(Integer order, Double distanceToNext, Integer timeToNext) {
        this.orderInDay = order;
        this.distanceToNext = distanceToNext;
        this.timeToNext = timeToNext;
    }

    public static Destination createDestination(String name, String address, double latitude, double longitude, DestinationType type) {
        Destination destination = new Destination();
        destination.setName(name);
        destination.setAddress(address);
        destination.setLatitude(latitude);
        destination.setLongitude(longitude);
        destination.setType(type);
        return destination;
    }
}