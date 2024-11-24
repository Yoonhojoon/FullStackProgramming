package com.fullstack.demo.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


// DailyPlan.java
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dailyPlanId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "itinerary_id", nullable = false)
    private Itinerary itinerary;

    @OneToMany(mappedBy = "dailyPlan", cascade = CascadeType.ALL)
    private List<Destination> destinations = new ArrayList<>();

    @Column(nullable = false)
    private Integer dayNumber;

    @Column
    private Double totalDistance;  // 일일 총 이동 거리

    @Column
    private Integer totalTravelTime;  // 일일 총 이동 시간(분)

    @Builder
    public DailyPlan(Itinerary itinerary, Integer dayNumber, Double totalDistance, Integer totalTravelTime) {
        this.itinerary = itinerary;
        this.dayNumber = dayNumber;
        this.totalDistance = totalDistance;
        this.totalTravelTime = totalTravelTime;
    }

    public void addDestination(Destination destination) {
        this.destinations.add(destination);
        destination.setDailyPlan(this);
    }

    void setItinerary(Itinerary itinerary) {
        this.itinerary = itinerary;
    }
}
