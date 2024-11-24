package com.fullstack.demo.entity;

import jakarta.persistence.*;
import lombok.*;

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

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    // orderInDay는 일정이 생성된 후에 설정되므로 nullable 허용
    private Integer orderInDay;

    // 다음 목적지까지의 거리와 시간도 마지막 목적지는 null이므로 nullable 허용
    private Double distanceToNext;
    private Integer timeToNext;

    @ManyToOne
    @JoinColumn(name = "daily_plan_id")
    // DailyPlan은 목적지가 처음 생성될 때는 없을 수 있으므로 nullable 허용
    private DailyPlan dailyPlan;
    @Builder
    public Destination(String name, String address,
                       Double latitude, Double longitude) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    void setDailyPlan(DailyPlan dailyPlan) {
        this.dailyPlan = dailyPlan;
    }


    public void updateOrderAndDistanceInfo(Integer order, Double distanceToNext, Integer timeToNext) {
        this.orderInDay = order;
        this.distanceToNext = distanceToNext;
        this.timeToNext = timeToNext;
    }

    // 정적 팩토리 메서드
    public static Destination createDestination(String name, String address, double latitude, double longitude) {
        Destination destination = new Destination();
        destination.setName(name);
        destination.setAddress(address);
        destination.setLatitude(latitude);
        destination.setLongitude(longitude);
        return destination;
    }

}