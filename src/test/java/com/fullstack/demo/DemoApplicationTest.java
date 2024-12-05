package com.fullstack.demo;

import com.fullstack.demo.entity.DailyPlan;
import com.fullstack.demo.entity.Destination;
import com.fullstack.demo.entity.DestinationType;

import com.fullstack.demo.service.RouteOptimizerService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TripPlanningServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private RouteOptimizerService tripPlanningService;

    @Test
    void optimizeRoute_ShouldReturnOptimizedDailyPlans() {
        // Given
        List<Destination> destinations = Arrays.asList(
                createDestination(1L, DestinationType.ACCOMMODATION, "Hotel", 37.5665, 126.9780),
                createDestination(2L, DestinationType.SPOT, "Place1", 37.5662, 126.9784),
                createDestination(3L, DestinationType.SPOT, "Place2", 37.5664, 126.9779)
        );

        String mockResponse = createMockGoogleResponse();
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(mockResponse);

        // When
        List<DailyPlan> result = tripPlanningService.optimizeRoute(destinations, 1);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        DailyPlan dailyPlan = result.get(0);
        List<Destination> dailyDestinations = dailyPlan.getDestinations();
        assertEquals(3, dailyDestinations.size());
        assertEquals(0, dailyDestinations.get(0).getOrderInDay());
        assertEquals(1, dailyDestinations.get(1).getOrderInDay());
        assertEquals(2, dailyDestinations.get(2).getOrderInDay());
    }

    private Destination createDestination(Long id, DestinationType type, String name, double lat, double lng) {
        return Destination.builder()
                .destinationId(id)
                .type(type)
                .name(name)
                .latitude(lat)
                .longitude(lng)
                .build();
    }

    private String createMockGoogleResponse() {
        return """
            {
                "routes": [{
                    "legs": [{
                        "distance": {"value": 1000},
                        "duration": {"value": 600}
                    }],
                    "waypoint_order": [0, 1]
                }]
            }
            """;
    }
}