package com.fullstack.demo;

import com.fullstack.demo.entity.DailyPlan;
import com.fullstack.demo.entity.Destination;
import com.fullstack.demo.service.TripPlanningService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TripPlanningServiceIntegrationTest {

    @Autowired
    private TripPlanningService tripPlanningService;

    @Autowired
    private EntityManager em;

    @Value("${google.api.key}")
    private String googleApiKey;

    @Test
    @Transactional
    void testCreateOptimizedPlan() throws Exception {
        // Given
        List<Destination> destinations = Arrays.asList(
                persistDestination(Destination.createDestination("Golden Gate Bridge", "San Francisco, CA", 37.8199, -122.4783)),
                persistDestination(Destination.createDestination("Pier 39", "San Francisco, CA", 37.8087, -122.4098)),
                persistDestination(Destination.createDestination("Palace of Fine Arts", "San Francisco, CA", 37.8029, -122.4484)),
                persistDestination(Destination.createDestination("Lombard Street", "San Francisco, CA", 37.8021, -122.4187)),
                persistDestination(Destination.createDestination("Alcatraz Island", "San Francisco, CA", 37.8270, -122.4230))
        );
        em.flush();
        em.clear();
        // When
        List<DailyPlan> dailyPlans = tripPlanningService.createOptimizedPlan(destinations, 2);

        // Then
        assertNotNull(dailyPlans);
        assertEquals(2, dailyPlans.size()); // 2일치 계획이 생성되었는지 확인

        // 각 일자별 계획 검증
        for (int i = 0; i < dailyPlans.size(); i++) {
            DailyPlan dailyPlan = dailyPlans.get(i);

            // 기본 정보 검증
            assertNotNull(dailyPlan.getDayNumber());
            assertNotNull(dailyPlan.getTotalDistance());
            assertNotNull(dailyPlan.getTotalTravelTime());

            // 목적지 검증
            assertFalse(dailyPlan.getDestinations().isEmpty());

            // 각 목적지의 순서와 거리 정보 검증
            List<Destination> dailyDestinations = dailyPlan.getDestinations();
            for (int j = 0; j < dailyDestinations.size(); j++) {
                Destination dest = dailyDestinations.get(j);
                assertEquals(j + 1, dest.getOrderInDay());

                // 마지막 목적지가 아닌 경우 거리와 시간 정보가 있어야 함
                if (j < dailyDestinations.size() - 1) {
                    assertNotNull(dest.getDistanceToNext());
                    assertNotNull(dest.getTimeToNext());
                    assertTrue(dest.getDistanceToNext() > 0);
                    assertTrue(dest.getTimeToNext() > 0);
                }
            }
        }

        // 전체 목적지가 모두 포함되었는지 확인
        int totalDestinations = dailyPlans.stream()
                .mapToInt(plan -> plan.getDestinations().size())
                .sum();
        assertEquals(destinations.size(), totalDestinations);

        // 로그 출력
        printPlanDetails(dailyPlans);
    }

    private Destination persistDestination(Destination destination) {
        em.persist(destination);
        return destination;
    }

    private Destination createDestination(String name, String address, double lat, double lng) {
        return Destination.builder()  // Destination에 @Builder 추가 필요
                .name(name)
                .address(address)
                .latitude(lat)
                .longitude(lng)
                .orderInDay(0)  // 초기값
                .distanceToNext(0.0)  // 초기값
                .timeToNext(0)  // 초기값
                .build();
    }

    private void printPlanDetails(List<DailyPlan> dailyPlans) {
        for (int i = 0; i < dailyPlans.size(); i++) {
            DailyPlan plan = dailyPlans.get(i);
            System.out.println("\nDay " + (i + 1) + ":");
            System.out.println("Total Distance: " + plan.getTotalDistance() + " km");
            System.out.println("Total Travel Time: " + plan.getTotalTravelTime() + " minutes");

            List<Destination> destinations = plan.getDestinations();
            for (int j = 0; j < destinations.size(); j++) {
                Destination dest = destinations.get(j);
                System.out.println("\nStop " + (j + 1) + ": " + dest.getName());
                System.out.println("Address: " + dest.getAddress());
                if (j < destinations.size() - 1) {
                    System.out.println("Distance to next: " + dest.getDistanceToNext() + " km");
                    System.out.println("Time to next: " + dest.getTimeToNext() + " minutes");
                }
            }
        }
    }
}