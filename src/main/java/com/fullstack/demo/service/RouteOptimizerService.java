package com.fullstack.demo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fullstack.demo.entity.DailyPlan;
import com.fullstack.demo.entity.Destination;
import com.fullstack.demo.entity.DestinationType;
import com.google.maps.DistanceMatrixApi;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.TravelMode;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RouteOptimizerService {

    @Value("${google.maps.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public List<DailyPlan> optimizeRoute(List<Destination> destinations, int numberOfDays) {
        // 숙소와 관광지 분리
        List<Destination> accommodations = destinations.stream()
                .filter(d -> d.getType() == DestinationType.ACCOMMODATION)
                .collect(Collectors.toList());

        List<Destination> attractions = destinations.stream()
                .filter(d -> d.getType() != DestinationType.ACCOMMODATION)
                .collect(Collectors.toList());

        // 일자별 관광지 분배
        int attractionsPerDay = (int) Math.ceil((double) attractions.size() / numberOfDays);
        List<DailyPlan> dailyPlans = new ArrayList<>();

        for (int day = 0; day < numberOfDays; day++) {
            DailyPlan dailyPlan = new DailyPlan();
            Destination accommodation = accommodations.get(0); // 첫 번째 숙소 기준

            int startIdx = day * attractionsPerDay;
            int endIdx = Math.min((day + 1) * attractionsPerDay, attractions.size());
            List<Destination> dailyAttractions = attractions.subList(startIdx, endIdx);

            // Google Directions API로 최적 경로 계산
            String optimizedRoute = getOptimizedRoute(accommodation, dailyAttractions);
            updateDestinationsOrder(dailyPlan, accommodation, dailyAttractions, optimizedRoute);

            dailyPlans.add(dailyPlan);
        }

        return dailyPlans;
    }

    private String getOptimizedRoute(Destination accommodation, List<Destination> attractions) {
        StringBuilder waypointsBuilder = new StringBuilder();
        for (Destination attraction : attractions) {
            waypointsBuilder.append(attraction.getLatitude())
                    .append(",")
                    .append(attraction.getLongitude())
                    .append("|");
        }

        String url = String.format(
                "https://maps.googleapis.com/maps/api/directions/json?" +
                        "origin=%f,%f&destination=%f,%f&waypoints=optimize:true|%s&key=%s",
                accommodation.getLatitude(), accommodation.getLongitude(),
                accommodation.getLatitude(), accommodation.getLongitude(),
                waypointsBuilder.toString(),
                apiKey
        );

        return restTemplate.getForObject(url, String.class);
    }

    private void updateDestinationsOrder(
            DailyPlan dailyPlan,
            Destination accommodation,
            List<Destination> attractions,
            String optimizedRoute
    ) {
        // Google API 응답 파싱 및 순서 업데이트 로직
        ObjectMapper mapper = new ObjectMapper();
        JsonNode response = null;
        try {
            response = mapper.readTree(optimizedRoute);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // waypoint_order 배열 가져오기
        JsonNode waypointOrder = response.get("routes").get(0).get("waypoint_order");

        int order = 1;
        // 시작점: 숙소
        accommodation.setOrderInDay(0);
        accommodation.setDailyPlan(dailyPlan);

        // 중간 경유지: 관광지
        for (JsonNode index : waypointOrder) {
            Destination destination = attractions.get(index.asInt());
            destination.setOrderInDay(order++);
            destination.setDailyPlan(dailyPlan);

            // 이전 장소와의 거리/시간 정보 업데이트
            updateDistanceAndTime(destination, response, order);
        }

        // 종료점: 숙소로 돌아오기
        accommodation.setOrderInDay(order);
    }

    private void updateDistanceAndTime(Destination destination, JsonNode response, int order) {
        JsonNode legs = response.get("routes").get(0).get("legs");
        JsonNode leg = legs.get(order - 1);

        int durationSeconds = leg.get("duration").get("value").asInt();
        int distanceMeters = leg.get("distance").get("value").asInt();

        destination.setTimeToNext(durationSeconds);
        destination.setDistanceToNext((double) distanceMeters);
    }
}