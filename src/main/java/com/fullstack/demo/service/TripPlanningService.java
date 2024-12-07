package com.fullstack.demo.service;

import com.fullstack.demo.dto.*;
import com.fullstack.demo.dto.response.DirectionsResponse;
import com.fullstack.demo.entity.*;
import com.fullstack.demo.entity.naver.LatLng;
import com.fullstack.demo.entity.naver.Leg;
import com.fullstack.demo.entity.naver.OptimizedRoute;
import com.fullstack.demo.repository.ItineraryRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class TripPlanningService {

    @Data
    @AllArgsConstructor
    public static class OptimizationResult {
        private List<DailyPlan> dailyPlans;
        private List<DirectionsResponse> directionsResponses;  // 각 일자별 경로 정보
    }
    private final DirectionsService directionsService;
    private final ItineraryRepository itineraryRepository;

    public TripPlanningService(DirectionsService directionsService, ItineraryRepository itineraryRepository) {
        this.directionsService = directionsService;
        this.itineraryRepository = itineraryRepository;
    }

    private List<GuideDTO> optimizeDailyRoute(DailyPlan dailyPlan, DailyPlan previousDayPlan) {
        List<Destination> spots = dailyPlan.getDestinations().stream()
                .filter(d -> d.getType() == DestinationType.SPOT)
                .collect(Collectors.toList());

        if (spots.isEmpty()) return new ArrayList<>();

        // 출발지 좌표
        LatLng originLatLng = (previousDayPlan != null)
                ? new LatLng(previousDayPlan.getAccommodation().getLatitude(),
                previousDayPlan.getAccommodation().getLongitude())
                : new LatLng(dailyPlan.getAccommodation().getLatitude(),
                dailyPlan.getAccommodation().getLongitude());

        // 도착지 좌표
        LatLng destinationLatLng = new LatLng(dailyPlan.getAccommodation().getLatitude(),
                dailyPlan.getAccommodation().getLongitude());

        // 경유지 좌표들
        LatLng[] waypointLatLngs = spots.stream()
                .map(spot -> new LatLng(spot.getLatitude(), spot.getLongitude()))
                .toArray(LatLng[]::new);

        OptimizedRoute result = null;
        try {
            result = directionsService.getOptimizedRoute(originLatLng, destinationLatLng, waypointLatLngs);
        } catch (Exception e) {
            throw new RuntimeException("Failed to optimize route", e);
        }

        List<Destination> optimizedSpots = new ArrayList<>();
        int[] waypointOrder = result.getWaypointOrder();

        for (int order : waypointOrder) {
            optimizedSpots.add(spots.get(order));
        }

        dailyPlan.getDestinations().clear();
        dailyPlan.getDestinations().addAll(optimizedSpots);

        updateRouteDetails(dailyPlan, result);

        // Guide 정보 추출 및 반환
        return result.getDirections().getRoute().getTrafast().stream()
                .flatMap(trafast -> trafast.getGuide().stream())
                .map(guide -> GuideDTO.builder()
                        .distance(guide.getDistance())
                        .duration(guide.getDuration())
                        .instructions(guide.getInstructions())
                        .type(guide.getType())
                        .pointIndex(guide.getPointIndex())
                        .build())
                .collect(Collectors.toList());
    }
    private void updateRouteDetails(DailyPlan dailyPlan, OptimizedRoute optimizedRoute) {
        DirectionsResponse response = optimizedRoute.getDirections();
        List<Destination> spots = dailyPlan.getDestinations();

        // Trafast의 첫 번째 요소에서 summary 정보를 가져옵니다
        DirectionsResponse.Route.Trafast routeInfo = response.getRoute().getTrafast().get(0);
        DirectionsResponse.Route.Summary summary = routeInfo.getSummary();

        // 거리는 미터, 시간은 밀리초 단위로 오므로 변환이 필요합니다
        double totalDistanceInKm = summary.getDistance() / 1000.0;  // meters to km
        int totalTimeInMinutes = summary.getDuration() / 60000;  // milliseconds to minutes

        // 순서대로 spots의 orderInDay 설정
        for (int i = 0; i < spots.size(); i++) {
            Destination current = spots.get(i);
            current.setOrderInDay(i + 1);
        }

        // 관광지당 1시간 체류 시간 추가
        totalTimeInMinutes += spots.size() * 60;

        dailyPlan.setTotalDistance(totalDistanceInKm);
        dailyPlan.setTotalTravelTime(totalTimeInMinutes);
    }

    private void distributeSpots(List<Destination> spots, List<DailyPlan> dailyPlans) {
        Map<DailyPlan, List<Destination>> planSpots = new HashMap<>();

        for (Destination spot : spots) {
            DailyPlan bestPlan = findBestDayPlan(spot, dailyPlans);
            planSpots.computeIfAbsent(bestPlan, k -> new ArrayList<>()).add(spot);
        }

        // 각 일자별로 할당된 관광지 설정
        for (Map.Entry<DailyPlan, List<Destination>> entry : planSpots.entrySet()) {
            DailyPlan dailyPlan = entry.getKey();  // DailyPlan으로 수정
            List<Destination> plannedSpots = entry.getValue();

            // destinations 리스트에 spot들을 추가하고 관계 설정
            for (Destination spot : plannedSpots) {
                spot.setDailyPlan(dailyPlan);  // dailyPlan으로 수정
                dailyPlan.getDestinations().add(spot);
            }
        }
    }

    private double calculateDistance(Destination a, Destination b) {
        // Haversine 공식을 사용한 대략적인 거리 계산
        double lat1 = Math.toRadians(a.getLatitude());
        double lat2 = Math.toRadians(b.getLatitude());
        double lon1 = Math.toRadians(a.getLongitude());
        double lon2 = Math.toRadians(b.getLongitude());

        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;

        double a_val = Math.pow(Math.sin(dlat/2), 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.pow(Math.sin(dlon/2), 2);

        double c = 2 * Math.atan2(Math.sqrt(a_val), Math.sqrt(1-a_val));
        return 6371 * c; // 지구 반경(km) * c
    }

    private DailyPlan findBestDayPlan(Destination spot, List<DailyPlan> dailyPlans) {
        DailyPlan bestPlan = null;
        double shortestDistance = Double.MAX_VALUE;

        for (DailyPlan plan : dailyPlans) {
            Destination accommodation = plan.getAccommodation();
            if (accommodation == null) continue; // 숙소가 지정되지 않은 날은 스킵

            double distance = calculateDistance(accommodation, spot);
            if (distance < shortestDistance && canAddSpotToPlan(plan, spot)) {
                shortestDistance = distance;
                bestPlan = plan;
            }
        }
        // 적절한 Plan이 없으면 기본 Plan 생성
        if (bestPlan == null) {
            bestPlan = createDefaultPlan(spot);
        }

        return bestPlan;
    }


    private DailyPlan createDefaultPlan(Destination spot) {
        DailyPlan defaultPlan = new DailyPlan();
        defaultPlan.setDestinations(new ArrayList<>());
        // 필요한 초기 설정 추가
        return defaultPlan;
    }


    private boolean canAddSpotToPlan(DailyPlan plan, Destination spot) {
        // 하루 최대 방문 가능 관광지 수나 다른 제약 조건을 여기서 체크할 수 있습니다
        return true;
    }
    public List<UITripItemDTO> convertToUITripItems(OptimizedDailyPlanDto optimizedPlan) {
        List<UITripItemDTO> uiItems = new ArrayList<>();
        DailyPlan dailyPlan = optimizedPlan.getDailyPlan();
        List<GuideDTO> guides = optimizedPlan.getGuides();

        // destinations를 UITripItemDTO로 변환
        for (int i = 0; i < dailyPlan.getDestinations().size(); i++) {
            Destination dest = dailyPlan.getDestinations().get(i);
            GuideDTO guide = guides.get(i);  // 인덱스 매칭이 맞는지 확인 필요

            uiItems.add(UITripItemDTO.builder()
                    .name(dest.getName())
                    .address(dest.getAddress())
                    .type(dest.getType().toString())
                    .color("red")  // color 결정 로직 필요
                    .travelTimeMinutes(guide.getDuration())
                    .distanceToNext((double) guide.getDistance())
                    .guide(guide)
                    .build());
        }
        return uiItems;
    }

    public List<OptimizedDailyPlanDto> optimizeTrip(Itinerary itinerary, Map<Integer, Destination> accommodationsByDay,
                                                    List<Destination> spots) {
        System.out.println("Accommodations map size: " + accommodationsByDay.size());
        accommodationsByDay.forEach((day, acc) ->
                System.out.println("Day " + day + " accommodation: " + acc));

        List<DailyPlan> dailyPlans = new ArrayList<>();
        List<OptimizedDailyPlanDto> optimizedPlans = new ArrayList<>();

        int totalDays = accommodationsByDay.keySet().stream()
                .mapToInt(Integer::intValue)
                .max()
                .orElse(0);

        // DailyPlan 생성 부분은 동일
        for (int day = 0; day < totalDays; day++) {
            DailyPlan dailyPlan = new DailyPlan();
            dailyPlan.setDayNumber(day + 1);
            dailyPlan.setItinerary(itinerary);

            Destination accommodation = accommodationsByDay.get(day + 1);
            dailyPlan.setAccommodation(accommodation);
            dailyPlan.setDestinations(new ArrayList<>());
            dailyPlans.add(dailyPlan);

            // 새로운 DTO 생성 및 추가
            OptimizedDailyPlanDto optimizedPlan = new OptimizedDailyPlanDto();
            optimizedPlan.setDailyPlan(dailyPlan);
            optimizedPlan.setGuides(new ArrayList<>());  // 가이드 정보는 나중에 채워짐
            optimizedPlans.add(optimizedPlan);
        }

        distributeSpots(spots, dailyPlans);

        // 최적화 및 가이드 정보 추가
        for (int i = 0; i < dailyPlans.size(); i++) {
            DailyPlan previousDayPlan = (i > 0) ? dailyPlans.get(i-1) : null;
            DailyPlan currentPlan = dailyPlans.get(i);

            // 경로 최적화 및 가이드 정보 획득
            List<GuideDTO> guides = optimizeDailyRoute(currentPlan, previousDayPlan);
            optimizedPlans.get(i).setGuides(guides);
        }

        return optimizedPlans;
    }
    public Itinerary saveOptimizedItinerary(
            User user,
            String title,
            String description,
            LocalDate startDate,
            LocalDate endDate,
            List<DailyPlan> optimizedPlans
    ) {
        Itinerary itinerary = Itinerary.builder()
                .user(user)
                .title(title)
                .description(description)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        for (DailyPlan dailyPlan : optimizedPlans) {
            itinerary.addDailyPlan(dailyPlan);
        }

        return itineraryRepository.save(itinerary);
    }


    private String getColorForIndex(int index) {
        // UI에 맞는 색상 코드 반환
        String[] colors = {"#FF0000", "#FF9800", "#FFEB3B", "#4CAF50"};  // 예시 색상들
        return colors[index % colors.length];
    }


    private List<UITripItemDTO> convertDestinationsToItems(DailyPlan plan, List<DirectionsResponse.Route.Guide> guides) {
        List<Destination> sortedDestinations = new ArrayList<>(plan.getDestinations());
        sortedDestinations.sort(Comparator.comparing(Destination::getOrderInDay));

        List<UITripItemDTO> items = new ArrayList<>();

        for (int i = 0; i < sortedDestinations.size(); i++) {
            Destination dest = sortedDestinations.get(i);
            DirectionsResponse.Route.Guide guide = (i < guides.size()) ? guides.get(i) : null;

            items.add(UITripItemDTO.builder()
                    .name(dest.getName())
                    .address(dest.getAddress())
                    .type(dest.getType().toString())
                    .color(determineColor(dest.getType()))
                    .travelTimeMinutes(dest.getTimeToNext())
                    .distanceToNext(dest.getDistanceToNext())
                    .guide(guide != null ? GuideDTO.builder()
                            .distance(guide.getDistance())
                            .duration(guide.getDuration())
                            .instructions(guide.getInstructions())
                            .type(guide.getType())
                            .build() : null)
                    .build());
        }

        return items;
    }
    private String determineColor(DestinationType type) {
        return switch (type) {
            case SPOT -> "#FFD700";     // 관광지
            case ACCOMMODATION -> "#FF6B6B";  // 숙소
            default -> "#4CAF50";
        };
    }


}