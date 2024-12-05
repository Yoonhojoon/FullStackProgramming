package com.fullstack.demo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fullstack.demo.controller.DirectionsController;
import com.fullstack.demo.dto.TripOptimizationRequest;
import com.fullstack.demo.entity.DailyPlan;
import com.fullstack.demo.entity.Destination;
import com.fullstack.demo.entity.DestinationType;
import com.fullstack.demo.entity.Itinerary;
import com.google.maps.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
public class DirectionsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GoogleMapsService googleMapsService;

    @MockBean
    private TripPlanningService tripPlanningService;

    private DirectionsResult mockDirectionsResult;
    private TripOptimizationRequest mockRequest;
    private Map<Integer, Destination> mockAccommodations;
    private List<Destination> mockSpots;
    private Itinerary mockItinerary;

    @BeforeEach
    void setUp() {
        // Initialize basic objects
        mockDirectionsResult = new DirectionsResult();
        mockRequest = createMockRequest();

        // Initialize mockItinerary
        mockItinerary = new Itinerary();
        mockItinerary.setItineraryId(1L);

        // Initialize accommodations
        mockAccommodations = new HashMap<>();
        mockAccommodations.put(1, Destination.createDestination(
                "Hotel A",
                "Seoul Hotel",
                37.5665,
                126.9780,
                DestinationType.ACCOMMODATION
        ));

        // Initialize spots
        mockSpots = Arrays.asList(
                Destination.createDestination(
                        "Spot A",
                        "Gyeongbokgung",
                        37.5796,
                        126.9770,
                        DestinationType.SPOT
                ),
                Destination.createDestination(
                        "Spot B",
                        "Namsan Tower",
                        37.5511,
                        126.9882,
                        DestinationType.SPOT
                )
        );

        // Mock Google Maps API Response
        DirectionsRoute mockRoute = new DirectionsRoute();
        DirectionsLeg[] legs = new DirectionsLeg[3];

        int[][] routeInfo = {
                {8000, 1200},
                {6000, 900},
                {4000, 600}
        };

        for (int i = 0; i < legs.length; i++) {
            legs[i] = new DirectionsLeg();
            legs[i].duration = new Duration();
            legs[i].duration.inSeconds = routeInfo[i][1];
            legs[i].distance = new Distance();
            legs[i].distance.inMeters = routeInfo[i][0];
        }

        mockRoute.legs = legs;
        mockRoute.waypointOrder = new int[]{0, 1};
        mockDirectionsResult.routes = new DirectionsRoute[]{mockRoute};

        // Setup Mocks
        try {
            when(googleMapsService.getOptimizedRoute(anyString(), anyString(), any(String[].class), any(TravelMode.class)))
                    .thenReturn(mockDirectionsResult);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // Mock tripPlanningService response
        List<DailyPlan> mockDailyPlans = createMockDailyPlans();
        try {
            when(tripPlanningService.optimizeTrip(any(), any(), any(), any()))
                    .thenReturn(mockDailyPlans);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    void getDirections_ShouldReturnDirectionsResult() throws Exception {
        when(googleMapsService.getDirections(any(), any(), any()))
                .thenReturn(mockDirectionsResult);

        mockMvc.perform(get("/api/directions/simple")
                        .param("origin", "Seoul")
                        .param("destination", "Busan")
                        .param("travelMode", "DRIVING"))
                .andDo(print())  // 요청/응답 전체 출력
                .andExpect(status().isOk());
    }

    @Test
    void getOptimizedRoute_ShouldReturnOptimizedDirectionsResult() throws Exception {
        // DirectionsResult Mock 데이터에 의미있는 값 설정
        DirectionsRoute route = new DirectionsRoute();
        DirectionsLeg[] legs = new DirectionsLeg[3];

        for (int i = 0; i < legs.length; i++) {
            legs[i] = new DirectionsLeg();
            legs[i].duration = new Duration();
            legs[i].duration.inSeconds = 1800L; // 30 minutes
            legs[i].distance = new Distance();
            legs[i].distance.inMeters = 10000; // 10 km

            log.info("Leg {}: distance = {}km, duration = {} minutes",
                    i,
                    legs[i].distance.inMeters/1000.0,
                    legs[i].duration.inSeconds/60);
        }

        route.legs = legs;
        route.waypointOrder = new int[]{0, 1};
        mockDirectionsResult.routes = new DirectionsRoute[]{route};

        when(googleMapsService.getOptimizedRoute(any(), any(), any(), any()))
                .thenReturn(mockDirectionsResult);

        MvcResult result = mockMvc.perform(post("/api/directions/optimize")
                        .param("origin", "Seoul")
                        .param("destination", "Busan")
                        .param("waypoints", "Daejeon", "Daegu")
                        .param("travelMode", "DRIVING"))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        log.info("Request params: origin={}, destination={}, waypoints={}",
                "Seoul", "Busan", Arrays.asList("Daejeon", "Daegu"));

        // 응답 결과의 주요 정보만 로깅
        DirectionsResult response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                DirectionsResult.class);

        if (response.routes != null && response.routes.length > 0) {
            log.info("Optimized route order: {}",
                    Arrays.toString(response.routes[0].waypointOrder));

            DirectionsLeg[] routeLegs = response.routes[0].legs;
            for (int i = 0; i < routeLegs.length; i++) {
                log.info("Route segment {}: {} km, {} minutes",
                        i,
                        routeLegs[i].distance.inMeters/1000.0,
                        routeLegs[i].duration.inSeconds/60);
            }
        }
    }
    @Test
    void optimizeTrip_ShouldReturnOptimizedDailyPlans() throws Exception {
        List<DailyPlan> mockDailyPlans = createMockDailyPlans();
        when(tripPlanningService.optimizeTrip(any(), any(), any(), any()))
                .thenReturn(mockDailyPlans);

        mockMvc.perform(post("/api/directions/trip")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockRequest)))
                .andExpect(status().isOk());
    }

    private TripOptimizationRequest createMockRequest() {
        TripOptimizationRequest request = new TripOptimizationRequest();

        Itinerary itinerary = new Itinerary();
        itinerary.setItineraryId(1L);
        request.setItinerary(itinerary);

        Map<Integer, Destination> accommodations = new HashMap<>();
        accommodations.put(1, Destination.createDestination(
                "Hotel A",
                "Seoul Hotel",
                37.5665,
                126.9780,
                DestinationType.ACCOMMODATION
        ));
        accommodations.put(2, Destination.createDestination(
                "Hotel B",
                "Busan Hotel",
                35.1796,
                129.0756,
                DestinationType.ACCOMMODATION
        ));
        request.setAccommodationsByDay(accommodations);

        List<Destination> spots = Arrays.asList(
                Destination.createDestination(
                        "Spot A",
                        "Gyeongbokgung",
                        37.5796,
                        126.9770,
                        DestinationType.SPOT
                ),
                Destination.createDestination(
                        "Spot B",
                        "Namsan Tower",
                        37.5511,
                        126.9882,
                        DestinationType.SPOT
                )
        );
        request.setSpots(spots);

        request.setTravelMode(TravelMode.DRIVING);

        return request;
    }

    private List<DailyPlan> createMockDailyPlans() {
        List<DailyPlan> dailyPlans = new ArrayList<>();

        DailyPlan plan = new DailyPlan();
        plan.setDayNumber(1);
        plan.setDestinations(mockSpots);  // 이미 생성된 mockSpots 사용
        plan.setAccommodation(mockAccommodations.get(1));  // 이미 생성된 mockAccommodations 사용
        plan.setTotalDistance(18000.0);  // 18km
        plan.setTotalTravelTime(2700);   // 45분
        return dailyPlans;
    }

    @Test
    void optimizeTrip_ShouldTestBothDrivingAndTransit() throws Exception {
        // Mock 응답에 경로 정보 추가
        List<DailyPlan> mockDailyPlans = new ArrayList<>();
        DailyPlan mockPlan = new DailyPlan();
        mockPlan.setDayNumber(1);
        mockPlan.setAccommodation(mockAccommodations.get(1));

        // 목적지들에 경로 정보 추가
        List<Destination> destinationsWithRoute = new ArrayList<>();
        for (int i = 0; i < mockSpots.size(); i++) {
            Destination dest = mockSpots.get(i);
            dest.setDistanceToNext(i < mockSpots.size() - 1 ? 5000.0 : 3000.0); // 마지막은 숙소까지 거리
            dest.setTimeToNext((int) (i < mockSpots.size() - 1 ? 1200L : 900L)); // 마지막은 숙소까지 시간
            destinationsWithRoute.add(dest);
        }

        mockPlan.setDestinations(destinationsWithRoute);
        mockPlan.setTotalDistance(18000.0);
        mockPlan.setTotalTravelTime(2700);
        mockDailyPlans.add(mockPlan);

        when(tripPlanningService.optimizeTrip(
                any(Itinerary.class),
                any(Map.class),
                any(List.class),
                any(TravelMode.class)
        )).thenReturn(mockDailyPlans);

        TravelMode[] modes = {TravelMode.DRIVING, TravelMode.TRANSIT};

        for (TravelMode mode : modes) {
            log.info("\n\n=== {} 이동 경로 ===",
                    mode == TravelMode.DRIVING ? "자동차" : "대중교통");

            List<DailyPlan> result = tripPlanningService.optimizeTrip(
                    mockItinerary,
                    mockAccommodations,
                    mockSpots,
                    mode
            );

            // 상세 경로 정보 출력
            for (DailyPlan plan : result) {
                log.info("\n{}일차 여행 경로:", plan.getDayNumber());

                // 시작 숙소 정보
                log.info("▶ 출발: {} ({})",
                        plan.getAccommodation().getName(),
                        plan.getAccommodation().getAddress());

                // 각 목적지와 경로 정보
                for (int i = 0; i < plan.getDestinations().size(); i++) {
                    Destination current = plan.getDestinations().get(i);

                    // 이동 정보 출력
                    if (i == 0) {
                        log.info("   ↓ {:.1f}km, {}분 이동",
                                current.getDistanceToNext() / 1000.0,
                                current.getTimeToNext() / 60);
                    }

                    // 목적지 정보 출력
                    log.info("▶ 경유: {} ({})",
                            current.getName(),
                            current.getAddress());

                    // 다음 목적지까지의 이동 정보
                    if (current.getDistanceToNext() != null) {
                        log.info("   ↓ {:.1f}km, {}분 이동",
                                current.getDistanceToNext() / 1000.0,
                                current.getTimeToNext() / 60);
                    }
                }

                // 마지막 숙소로 복귀
                log.info("▶ 도착: {} ({})",
                        plan.getAccommodation().getName(),
                        plan.getAccommodation().getAddress());

                // 총 이동 거리와 시간
                log.info("\n총 이동 거리: {:.1f}km", plan.getTotalDistance() / 1000.0);
                log.info("총 이동 시간: {}분", plan.getTotalTravelTime() / 60);
            }
        }
    }

    private DirectionsResult createMockDirectionsResult(TravelMode mode) {
        DirectionsResult result = new DirectionsResult();
        DirectionsRoute route = new DirectionsRoute();
        DirectionsLeg[] legs = new DirectionsLeg[3];

        // 이동수단에 따른 예상 시간 차이 반영
        int[][] routeInfo;
        if (mode == TravelMode.DRIVING) {
            routeInfo = new int[][] {
                    {5000, 900},    // 5km, 15분
                    {8000, 1200},   // 8km, 20분
                    {6000, 1080}    // 6km, 18분
            };
        } else {  // TRANSIT
            routeInfo = new int[][] {
                    {5000, 1500},   // 5km, 25분
                    {8000, 2100},   // 8km, 35분
                    {6000, 1800}    // 6km, 30분
            };
        }

        for (int i = 0; i < legs.length; i++) {
            legs[i] = new DirectionsLeg();
            legs[i].duration = new Duration();
            legs[i].duration.inSeconds = routeInfo[i][1];
            legs[i].distance = new Distance();
            legs[i].distance.inMeters = routeInfo[i][0];
        }

        route.legs = legs;
        route.waypointOrder = new int[]{0, 1};
        result.routes = new DirectionsRoute[]{route};

        return result;
    }
}