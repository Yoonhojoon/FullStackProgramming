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
public class DailyPlan extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dailyPlanId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "itinerary_id", nullable = false)
    private Itinerary itinerary;

    @Column
    private LocalDate dayNumber;

    @OneToMany(mappedBy = "dailyPlan")
    private List<Destination> destinations = new ArrayList<>();

    @Builder
    public DailyPlan(Itinerary itinerary, LocalDate dayNumber) {
        this.itinerary = itinerary;
        this.dayNumber = dayNumber;
    }
}