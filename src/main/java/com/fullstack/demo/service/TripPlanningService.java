package com.fullstack.demo.service;

import com.fullstack.demo.entity.DailyPlan;
import com.fullstack.demo.entity.Destination;
import com.google.maps.DistanceMatrixApi;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.TravelMode;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TripPlanningService {
    private final GoogleRoutesService googleRoutesService;
    private final EntityManager em;

    public List<DailyPlan> createOptimizedPlan(List<Destination> destinations, int numberOfDays) throws Exception {
        // 1. 거리/시간 매트릭스 생성
        TimeDistanceMatrix matrix = createTimeDistanceMatrix(destinations);

        // 2. 최적 경로 계산
        List<Destination> optimizedRoute = optimizeRoute(destinations, matrix);

        // 3. N일 일정으로 분배
        return splitIntoDailyPlans(optimizedRoute, matrix, numberOfDays);
    }

    @Getter
    @AllArgsConstructor
    private static class TimeDistanceMatrix {
        private final long[][] timeMatrix; // 분 단위
        private final double[][] distanceMatrix; // km 단위
    }

    private TimeDistanceMatrix createTimeDistanceMatrix(List<Destination> destinations) throws Exception {
        int n = destinations.size();
        long[][] timeMatrix = new long[n][n];
        double[][] distanceMatrix = new double[n][n];

        String[] origins = destinations.stream()
                .map(d -> d.getLatitude() + "," + d.getLongitude())
                .toArray(String[]::new);

        DistanceMatrix matrix = DistanceMatrixApi.newRequest(googleRoutesService.getContext())
                .origins(origins)
                .destinations(origins)
                .mode(TravelMode.DRIVING)
                .await();

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    timeMatrix[i][j] = matrix.rows[i].elements[j].duration.inSeconds / 60; // 분 단위로 변환
                    distanceMatrix[i][j] = matrix.rows[i].elements[j].distance.inMeters / 1000.0; // km 단위로 변환
                }
            }
        }

        return new TimeDistanceMatrix(timeMatrix, distanceMatrix);
    }

    private List<Destination> optimizeRoute(List<Destination> destinations, TimeDistanceMatrix matrix) {
        List<Destination> optimizedRoute = new ArrayList<>();
        List<Destination> unvisited = new ArrayList<>(destinations);

        // 첫 번째 장소를 시작점으로 선택 (가장 북쪽에 있는 장소)
        Destination current = unvisited.stream()
                .max(Comparator.comparing(Destination::getLatitude))
                .orElseThrow();
        optimizedRoute.add(current);
        unvisited.remove(current);

        // Nearest Neighbor 알고리즘으로 다음 장소 선택
        while (!unvisited.isEmpty()) {
            int currentIdx = destinations.indexOf(current);
            Destination next = findNearestDestination(current, unvisited, currentIdx, matrix);
            optimizedRoute.add(next);
            unvisited.remove(next);
            current = next;
        }

        return optimizedRoute;
    }

    private Destination findNearestDestination(Destination current, List<Destination> unvisited,
                                               int currentIdx, TimeDistanceMatrix matrix) {
        return unvisited.stream()
                .min((a, b) -> {
                    int aIdx = unvisited.indexOf(a);
                    int bIdx = unvisited.indexOf(b);
                    return Double.compare(
                            matrix.getTimeMatrix()[currentIdx][aIdx],
                            matrix.getTimeMatrix()[currentIdx][bIdx]
                    );
                })
                .orElseThrow();
    }

    private List<DailyPlan> splitIntoDailyPlans(List<Destination> optimizedRoute,
                                                TimeDistanceMatrix matrix, int numberOfDays) {
        List<DailyPlan> dailyPlans = new ArrayList<>();

        int destinationsPerDay = (int) Math.ceil((double) optimizedRoute.size() / numberOfDays);

        for (int day = 0; day < numberOfDays; day++) {
            // DailyPlan 생성을 Builder를 사용하도록 수정
            DailyPlan dailyPlan = DailyPlan.builder()
                    .dayNumber(day + 1)
                    .totalDistance(0.0)
                    .totalTravelTime(0)
                    .build();

            int startIndex = day * destinationsPerDay;
            int endIndex = Math.min((day + 1) * destinationsPerDay, optimizedRoute.size());

            List<Destination> dailyDestinations = optimizedRoute.subList(startIndex, endIndex);

            double dailyTotalDistance = 0.0;
            int dailyTotalTime = 0;

            // 각 장소의 순서와 다음 장소까지의 거리/시간 정보 업데이트
            for (int i = 0; i < dailyDestinations.size(); i++) {
                Destination current = dailyDestinations.get(i);

                // dailyPlan에 destination 추가
                dailyPlan.addDestination(current);

                // orderInDay 설정 (1부터 시작)
                int order = i + 1;

                // 마지막 장소가 아닌 경우, 다음 장소까지의 거리와 시간 계산
                double distanceToNext = 0.0;
                int timeToNext = 0;

                if (i < dailyDestinations.size() - 1) {
                    int currentIdx = optimizedRoute.indexOf(current);
                    int nextIdx = optimizedRoute.indexOf(dailyDestinations.get(i + 1));

                    distanceToNext = matrix.getDistanceMatrix()[currentIdx][nextIdx];
                    timeToNext = (int) matrix.getTimeMatrix()[currentIdx][nextIdx];

                    dailyTotalDistance += distanceToNext;
                    dailyTotalTime += timeToNext;
                }

                // 장소 정보 업데이트
                updateDestinationInfo(current, order, distanceToNext, timeToNext);
            }

            // 일일 총 이동 거리와 시간 업데이트
            dailyPlan.updateTotalDistance(dailyTotalDistance);
            dailyPlan.updateTotalTravelTime(dailyTotalTime);

            dailyPlans.add(dailyPlan);
        }

        return dailyPlans;
    }


    private void updateDestinationInfo(Destination destination, int order,
                                       double distanceToNext, int timeToNext) {
        destination.setOrderInDay(order);
        destination.setDistanceToNext(distanceToNext);
        destination.setTimeToNext(timeToNext);
        em.merge(destination);  // 변경사항을 병합
    }

}
