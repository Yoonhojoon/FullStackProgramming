package com.fullstack.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fullstack.demo.dto.OptimizeTripRequest;
import com.fullstack.demo.dto.Request.SaveItineraryRequest;
import com.fullstack.demo.entity.*;
import com.fullstack.demo.service.TripPlanningService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@ExtendWith(MockitoExtension.class)
class TripControllerTest {
    @Mock
    private TripPlanningService tripPlanningService;

    @InjectMocks
    private TripController tripController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(tripController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void optimizeTrip_ShouldReturnOptimizedPlan() throws Exception {
        // Given
        OptimizeTripRequest request = createOptimizeTripRequest();
        List<DailyPlan> optimizedPlans = createSampleDailyPlans();

        when(tripPlanningService.optimizeTrip(any(), anyMap(), anyList(), any()))
                .thenReturn(optimizedPlans);

        // When & Then
        mockMvc.perform(post("/api/trips/optimize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].dayNumber").value(1))
                .andExpect(jsonPath("$[0].totalDistance").value(15.5));
    }

    @Test
    void saveItinerary_ShouldReturnSavedItineraryId() throws Exception {
        SaveItineraryRequest request = createSaveItineraryRequest();
        Itinerary savedItinerary = createSampleItinerary();
        User testUser = new User("123","dbsdfdsm","dbsghwns1209@gmail.com", UserRole.USER);
        SecurityContextHolder.getContext()
                .setAuthentication(new TestingAuthenticationToken(testUser, null, "ROLE_USER"));

        String requestJson = objectMapper.writeValueAsString(request);

        when(tripPlanningService.saveOptimizedItinerary(
                any(User.class), // User 타입 명시
                anyString(),
                anyString(),
                any(LocalDate.class),
                any(LocalDate.class),
                anyList()))
                .thenReturn(savedItinerary);

        mockMvc.perform(post("/api/trips/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().string(savedItinerary.getItineraryId().toString()))
                .andDo(print());
    }

    private OptimizeTripRequest createOptimizeTripRequest() {
        OptimizeTripRequest request = new OptimizeTripRequest();
        Map<Integer, Destination> accommodations = new HashMap<>();
        accommodations.put(1, createDestination("호텔A", "서울시 강남구"));
        request.setAccommodationsByDay(accommodations);
        request.setSpots(List.of(
                createDestination("경복궁", "서울시 종로구"),
                createDestination("남산타워", "서울시 용산구")
        ));
        request.setTravelMode("TRANSIT");
        return request;
    }

    private SaveItineraryRequest createSaveItineraryRequest() {
        SaveItineraryRequest request = new SaveItineraryRequest();
        request.setTitle("서울 여행");
        request.setDescription("주말 서울 여행");
        request.setStartDate(LocalDate.now());
        request.setEndDate(LocalDate.now().plusDays(2));
        request.setOptimizedPlans(createSampleDailyPlans());
        return request;
    }

    private Destination createDestination(String name, String address) {
        Destination destination = new Destination();
        destination.setName(name);
        destination.setAddress(address);
        destination.setLatitude(37.5665);
        destination.setLongitude(126.9780);
        return destination;
    }

    private List<DailyPlan> createSampleDailyPlans() {
        DailyPlan plan = new DailyPlan();
        plan.setDayNumber(1);
        plan.setTotalDistance(15.5);
        plan.setTotalTravelTime(120);
        plan.setAccommodation(createDestination("호텔A", "서울시 강남구"));
        plan.setDestinations(List.of(
                createDestination("경복궁", "서울시 종로구"),
                createDestination("남산타워", "서울시 용산구")
        ));
        return List.of(plan);
    }

    private Itinerary createSampleItinerary() {
        Itinerary itinerary = Itinerary.builder()
                .title("서울 여행")
                .description("주말 서울 여행")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(2))
                .build();

        ReflectionTestUtils.setField(itinerary, "itineraryId", 1L);
        itinerary.setDailyPlans(createSampleDailyPlans());
        return itinerary;
    }
}