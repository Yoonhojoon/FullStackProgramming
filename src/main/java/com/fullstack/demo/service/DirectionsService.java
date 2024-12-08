package com.fullstack.demo.service;

import com.fullstack.demo.dto.GuideDTO;
import com.fullstack.demo.dto.response.DirectionsResponse;
import com.fullstack.demo.entity.naver.LatLng;
import com.fullstack.demo.entity.naver.OptimizedRoute;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;



@Service
@RequiredArgsConstructor
public class DirectionsService {

    @Value("${naver.client.id}")
    private String clientId;

    @Value("${naver.client.secret}")
    private String clientSecret;

    private final RestTemplate restTemplate;

    public DirectionsResponse getDirections(LatLng start, LatLng goal, List<LatLng> waypoints) {
        String url = buildDirectionsUrl(start, goal, waypoints);

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-NCP-APIGW-API-KEY-ID", clientId);
        headers.set("X-NCP-APIGW-API-KEY", clientSecret);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<DirectionsResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                DirectionsResponse.class
        );

        return response.getBody();
    }

    private String buildDirectionsUrl(LatLng start, LatLng goal, List<LatLng> waypoints) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl("https://naveropenapi.apigw.ntruss.com/map-direction/v1/driving")
                .queryParam("start", formatLatLng(start))
                .queryParam("goal", formatLatLng(goal))
                .queryParam("option", "trafast");

        if (waypoints != null && !waypoints.isEmpty()) {
            String waypointsParam = waypoints.stream()
                    .map(this::formatLatLng)
                    .collect(Collectors.joining("|"));
            builder.queryParam("waypoints", waypointsParam);
        }
        System.out.println("Generated URL: " + builder.encode().toUriString());
        return builder.build().encode().toUriString();

    }

    // LatLng를 "경도,위도" 형식의 문자열로 변환하는 헬퍼 메서드
    private String formatLatLng(LatLng latLng) {
        return String.format("%f,%f", latLng.getLng(), latLng.getLat());
    }

    public OptimizedRoute getOptimizedRoute(LatLng start, LatLng goal, LatLng[] waypoints) {
        // 입력값 검증
        if (waypoints == null || waypoints.length == 0) {
            // 경유지가 없는 경우 직접 경로만 반환
            DirectionsResponse response = getDirections(start, goal, new ArrayList<>());
            return new OptimizedRoute(new int[0], response);
        }

        if (isSameLocation(start, goal)) {
            return handleSameStartAndGoal(start, waypoints);
        }

        try {
            // waypoints만의 최적 순서를 찾음
            int n = waypoints.length;
            boolean[] visited = new boolean[n];
            List<Integer> optimizedOrder = new ArrayList<>();

            // 시작점에서 가장 가까운 waypoint 찾기
            int current = findNearestFromPoint(start, waypoints, visited);
            visited[current] = true;
            optimizedOrder.add(current);

            // 나머지 waypoint들에 대해 Nearest Neighbor 적용
            for (int i = 0; i < n - 1; i++) {
                current = findNearestFromPoint(waypoints[current], waypoints, visited);
                visited[current] = true;
                optimizedOrder.add(current);
            }

            // 최적화된 순서로 waypoint 리스트 재구성
            List<LatLng> optimizedWaypoints = optimizedOrder.stream()
                    .map(i -> waypoints[i])
                    .collect(Collectors.toList());

            // API 호출
            DirectionsResponse response = getDirections(start, goal, optimizedWaypoints);

            return new OptimizedRoute(
                    optimizedOrder.stream()
                            .mapToInt(Integer::intValue)
                            .toArray(),
                    response
            );
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new RuntimeException("Failed to optimize route: " + e.getMessage(), e);
        }
    }

    private boolean isSameLocation(LatLng location1, LatLng location2) {
        final double EPSILON = 0.0001; // 약 11m 정도의 오차 허용
        return Math.abs(location1.getLat() - location2.getLat()) < EPSILON &&
                Math.abs(location1.getLng() - location2.getLng()) < EPSILON;
    }


    // 주어진 위치에서 가장 가까운 방문하지 않은 waypoint 찾기
    private int findNearestFromPoint(LatLng point, LatLng[] waypoints, boolean[] visited) {
        if (waypoints == null || waypoints.length == 0) {
            throw new IllegalArgumentException("Waypoints array cannot be empty");
        }

        double minDist = Double.MAX_VALUE;
        int nearest = -1;
        boolean hasUnvisited = false;

        for (int i = 0; i < waypoints.length; i++) {
            if (!visited[i]) {
                hasUnvisited = true;
                double dist = calculateDistance(
                        point.getLat(),
                        point.getLng(),
                        waypoints[i].getLat(),
                        waypoints[i].getLng()
                );
                if (dist < minDist) {
                    minDist = dist;
                    nearest = i;
                }
            }
        }

        if (!hasUnvisited) {
            throw new IllegalStateException("All waypoints have been visited");
        }

        return nearest;
    }



    private OptimizedRoute handleSameStartAndGoal(LatLng location, LatLng[] waypoints) {
        int n = waypoints.length;
        boolean[] visited = new boolean[n];
        List<Integer> optimizedOrder = new ArrayList<>();

        // 시작점에서 가장 가까운 waypoint 찾기
        int current = findNearestFromPoint(location, waypoints, visited);
        visited[current] = true;
        optimizedOrder.add(current);

        // 나머지 waypoint들에 대해 Nearest Neighbor 적용
        for (int i = 0; i < n - 1; i++) {
            current = findNearestFromPoint(waypoints[current], waypoints, visited);
            visited[current] = true;
            optimizedOrder.add(current);
        }

        // 최적화된 순서로 waypoint 리스트 재구성
        List<LatLng> optimizedWaypoints = optimizedOrder.stream()
                .map(i -> waypoints[i])
                .collect(Collectors.toList());

        // 시작/도착점이 같으므로 location을 시작점과 도착점으로 사용
        DirectionsResponse response = getDirections(location, location, optimizedWaypoints);

        return new OptimizedRoute(
                optimizedOrder.stream()
                        .mapToInt(Integer::intValue)
                        .toArray(),
                response
        );
    }


    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // 하버사인 공식을 사용한 거리 계산
        double R = 6371; // 지구의 반경 (km)
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c;
    }


}
