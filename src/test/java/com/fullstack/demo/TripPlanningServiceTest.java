package com.fullstack.demo;

import com.fullstack.demo.entity.DailyPlan;
import com.fullstack.demo.entity.Destination;
import com.fullstack.demo.entity.DestinationType;
import com.fullstack.demo.entity.Itinerary;
import com.fullstack.demo.service.GoogleMapsService;
import com.fullstack.demo.service.TripPlanningService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.google.maps.model.*;
import lombok.extern.slf4j.Slf4j;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class TripPlanningServiceTest {

    @Mock
    private GoogleMapsService googleMapsService;

    @InjectMocks
    private TripPlanningService tripPlanningService;

    private Itinerary mockItinerary;
    private Map<Integer, Destination> mockAccommodations;
    private List<Destination> mockSpots;

    @BeforeEach
    void setUp() {
        mockItinerary = new Itinerary();
        mockItinerary.setItineraryId(1L);

        // 숙소 설정 - 서울과 부산
        mockAccommodations = new HashMap<>();
        mockAccommodations.put(1, Destination.createDestination(
                "서울호텔",
                "서울시 중구 명동길 12",
                37.5665,
                126.9780,
                DestinationType.ACCOMMODATION
        ));
        mockAccommodations.put(2, Destination.createDestination(
                "부산호텔",
                "부산시 해운대구 해운대해변로 264",
                35.1796,
                129.0756,
                DestinationType.ACCOMMODATION
        ));

        // 관광지 설정
        mockSpots = Arrays.asList(
                Destination.createDestination(
                        "경복궁",
                        "서울시 종로구 사직로 161",
                        37.5796,
                        126.9770,
                        DestinationType.SPOT
                ),
                Destination.createDestination(
                        "남산타워",
                        "서울시 용산구 남산공원길 105",
                        37.5511,
                        126.9882,
                        DestinationType.SPOT
                ),
                Destination.createDestination(
                        "해운대해수욕장",
                        "부산시 해운대구 해운대해변로 264",
                        35.1587,
                        129.1598,
                        DestinationType.SPOT
                )
        );
    }

    @Test
    void optimizeTrip_ShouldTestBothDrivingAndTransit() throws Exception {
        // 각 이동 수단별 테스트
        TravelMode[] modes = {TravelMode.DRIVING, TravelMode.TRANSIT};

        for (TravelMode mode : modes) {
            // Mock 응답 설정
            when(googleMapsService.getOptimizedRoute(any(), any(), any(), any()))
                    .thenReturn(createMockDirectionsResult(mode));

            log.info("\n\n=== {} 이용시 여행 일정 ===",
                    mode == TravelMode.DRIVING ? "자동차" : "대중교통");

            List<DailyPlan> result = tripPlanningService.optimizeTrip(
                    mockItinerary,
                    mockAccommodations,
                    mockSpots,
                    mode
            );
            log.info("OptimizeTrip 결과: {}", result);

            // 결과 출력
            DailyPlan previousPlan = null;
            for (DailyPlan plan : result) {
                log.info("\n{}일차", plan.getDayNumber());
                String startAccom = previousPlan != null ?
                        previousPlan.getAccommodation().getName() :
                        plan.getAccommodation().getName();

                log.info("■ 출발: {} ({})",
                        startAccom,
                        previousPlan != null ?
                                previousPlan.getAccommodation().getAddress() :
                                plan.getAccommodation().getAddress());

                for (Destination dest : plan.getDestinations()) {
                    log.info("\n► {} ({})", dest.getName(), dest.getAddress());

                    if (mode == TravelMode.DRIVING) {
                        if (dest.getDrivingDetails() != null) {
                            log.info("[운전 경로]");
                            log.info(dest.getDrivingDetails());
                        }
                    } else {
                        if (dest.getTransitDetails() != null) {
                            log.info("[대중교통 경로]");
                            log.info(dest.getTransitDetails());
                        }
                    }

                    if (dest.getDistanceToNext() != null) {
                        log.info("이동: {}km, {} 분",
                                String.format("%.1f", dest.getDistanceToNext()),
                                dest.getTimeToNext());
                    }

                    // 마지막 목적지에서 숙소로 가는 경로
                    if (dest.equals(plan.getDestinations().get(plan.getDestinations().size() - 1))) {
                        log.info("\n■ 숙소 복귀: {} ({})",
                                plan.getAccommodation().getName(),
                                plan.getAccommodation().getAddress());

                        if (mode == TravelMode.DRIVING && dest.getLastDrivingDetails() != null) {
                            log.info("[운전 경로]");
                            log.info(dest.getLastDrivingDetails());
                        } else if (mode == TravelMode.TRANSIT && dest.getLastTransitDetails() != null) {
                            log.info("[대중교통 경로]");
                            log.info(dest.getLastTransitDetails());
                        }
                    }
                }

                log.info("\n▶ 하루 총 이동거리: {}km",
                        String.format("%.1f", plan.getTotalDistance()));
                log.info("▶ 하루 총 소요시간: {} 분 (관광지 체류시간 포함)",
                        plan.getTotalTravelTime());

                previousPlan = plan;
            }
        }
    }

    private DirectionsResult createMockDirectionsResult(TravelMode mode) {
        DirectionsResult result = new DirectionsResult();
        DirectionsRoute route = new DirectionsRoute();

        // 각 목적지 사이의 경로 + 마지막 목적지에서 숙소까지의 경로
        int numLegs = mockSpots.size() + 1;
        DirectionsLeg[] legs = new DirectionsLeg[numLegs];

        for (int i = 0; i < legs.length; i++) {
            legs[i] = new DirectionsLeg();
            legs[i].steps = new DirectionsStep[2];


            if (mode == TravelMode.DRIVING) {
                // 운전 경로 정보
                legs[i].steps[0] = createDrivingStep(
                        "직진 후 우회전",
                        2000,  // 2km
                        600    // 10분
                );
                legs[i].steps[1] = createDrivingStep(
                        "좌회전 후 목적지",
                        1000,  // 1km
                        300    // 5분
                );
            } else {
                // 대중교통 경로 정보
                legs[i].steps[0] = createWalkingStep(
                        "도보로 버스정류장까지",
                        300,   // 300m
                        300    // 5분
                );
                legs[i].steps[1] = createTransitStep(
                        "버스 정류장 A",
                        "버스 정류장 B",
                        "102번 버스",
                        2700,  // 2.7km
                        1200   // 20분
                );
            }

            // 전체 거리와 시간 설정
            legs[i].distance = new Distance();
            legs[i].duration = new Duration();
            legs[i].distance.inMeters = 3000;  // 3km
            legs[i].duration.inSeconds = 1500;  // 25분
        }

        route.legs = legs;
        route.waypointOrder = new int[]{0}; // spots 크기와 맞게 수정
        result.routes = new DirectionsRoute[]{route};

        return result;
    }

    private DirectionsStep createDrivingStep(String instruction, int distanceMeters, int durationSeconds) {
        DirectionsStep step = new DirectionsStep();
        step.htmlInstructions = instruction;
        step.distance = new Distance();
        step.duration = new Duration();
        step.distance.inMeters = distanceMeters;
        step.duration.inSeconds = durationSeconds;
        step.travelMode = TravelMode.DRIVING;
        return step;
    }

    private DirectionsStep createWalkingStep(String instruction, int distanceMeters, int durationSeconds) {
        DirectionsStep step = new DirectionsStep();
        step.htmlInstructions = instruction;
        step.distance = new Distance();
        step.duration = new Duration();
        step.distance.inMeters = distanceMeters;
        step.duration.inSeconds = durationSeconds;
        step.travelMode = TravelMode.WALKING;
        return step;
    }

    private DirectionsStep createTransitStep(String departureStop, String arrivalStop,
                                             String lineName, int distanceMeters, int durationSeconds) {
        DirectionsStep step = new DirectionsStep();
        step.transitDetails = new TransitDetails();
        step.transitDetails.arrivalStop = new StopDetails();
        step.transitDetails.departureStop = new StopDetails();
        step.transitDetails.line = new TransitLine();

        step.transitDetails.departureStop.name = departureStop;
        step.transitDetails.arrivalStop.name = arrivalStop;
        step.transitDetails.line.shortName = lineName;
        step.transitDetails.departureTime = ZonedDateTime.now();
        step.transitDetails.arrivalTime = ZonedDateTime.now().plusSeconds(durationSeconds);

        step.distance = new Distance();
        step.duration = new Duration();
        step.distance.inMeters = distanceMeters;
        step.duration.inSeconds = durationSeconds;
        step.travelMode = TravelMode.TRANSIT;

        return step;
    }
}