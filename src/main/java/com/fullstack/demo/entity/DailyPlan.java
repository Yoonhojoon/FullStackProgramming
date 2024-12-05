package com.fullstack.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


// DailyPlan.java
@Entity
@Getter
@Setter
@NoArgsConstructor
public class DailyPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer dayNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "itinerary_id", nullable = false)
    private Itinerary itinerary;

    @OneToOne
    @JoinColumn(name = "accommodation_id")
    private Destination accommodation;

    @OneToMany(mappedBy = "dailyPlan", cascade = CascadeType.ALL)
    private List<Destination> destinations = new ArrayList<>();

    // 하루 전체 이동 거리
    private Double totalDistance;

    // 하루 전체 이동 시간 (분)
    private Integer totalTravelTime;
}
