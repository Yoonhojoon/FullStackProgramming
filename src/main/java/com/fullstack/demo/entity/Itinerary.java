package com.fullstack.demo.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Itinerary.java
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Itinerary extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itineraryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column
    private LocalDate startDate;

    @Column
    private LocalDate endDate;

    @OneToMany(mappedBy = "itinerary")
    private List<DailyPlan> dailyPlans = new ArrayList<>();

    @Builder
    public Itinerary(User user, LocalDate startDate, LocalDate endDate) {
        this.user = user;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}