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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    private void setName(String name) {
        this.name = name;
    }

    private void setAddress(String address) {
        this.address = address;
    }

    private void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    private void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    private void setType(DestinationType type) {
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