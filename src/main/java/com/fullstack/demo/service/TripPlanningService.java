package com.fullstack.demo.service;

import com.fullstack.demo.entity.*;
import com.fullstack.demo.repository.ItineraryRepository;
import com.google.maps.model.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TripPlanningService {
    private final GoogleMapsService googleMapsService;
    private final ItineraryRepository itineraryRepository; // 추가

    public TripPlanningService(GoogleMapsService googleMapsService, ItineraryRepository itineraryRepository) {
        this.googleMapsService = googleMapsService;
        this.itineraryRepository = itineraryRepository;
    }


    private void optimizeDailyRoute(DailyPlan dailyPlan, DailyPlan previousDayPlan, TravelMode travelMode) {
        List<Destination> spots = dailyPlan.getDestinations().stream()
                .filter(d -> d.getType() == DestinationType.SPOT)
                .collect(Collectors.toList());

        if (spots.isEmpty()) {
            return;
        }

        // 출발지는 이전 날의 숙소 (첫째날이면 당일 숙소)
        String origin = (previousDayPlan != null)
                ? previousDayPlan.getAccommodation().getAddress()
                : dailyPlan.getAccommodation().getAddress();

        // 도착지는 당일 숙소
        String destination = dailyPlan.getAccommodation().getAddress();

        String[] waypoints = spots.stream()
                .map(Destination::getAddress)
                .toArray(String[]::new);

        DirectionsResult result = null;
        try {
            result = googleMapsService.getOptimizedRoute(
                    origin,
                    destination,
                    waypoints,
                    travelMode
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        List<Destination> optimizedSpots = new ArrayList<>();
        int[] waypointOrder = result.routes[0].waypointOrder;

        if (waypointOrder.length == 0) {
            optimizedSpots.addAll(spots); // 최적화 불가 시 기존 순서 유지
        } else {
            for (int order : waypointOrder) {
                Destination spot = spots.get(order);
                optimizedSpots.add(spot);
            }
        }

        dailyPlan.getDestinations().clear();
        dailyPlan.getDestinations().addAll(optimizedSpots);

        updateRouteDetails(dailyPlan, result, travelMode);
    }
    private void distributeSpots(List<Destination> spots, List<DailyPlan> dailyPlans, TravelMode travelMode) {
        Map<DailyPlan, List<Destination>> planSpots = new HashMap<>();

        for (Destination spot : spots) {
            DailyPlan bestPlan = findBestDayPlan(spot, dailyPlans, travelMode);
            planSpots.computeIfAbsent(bestPlan, k -> new ArrayList<>()).add(spot);
        }

        // 각 일자별로 할당된 관광지 설정
        for (Map.Entry<DailyPlan, List<Destination>> entry : planSpots.entrySet()) {
            DailyPlan plan = entry.getKey();
            List<Destination> plannedSpots = entry.getValue();

            // destinations 리스트에 spot들을 추가하고 관계 설정
            for (Destination spot : plannedSpots) {
                spot.setDailyPlan(plan);
                plan.getDestinations().add(spot);
            }
        }
    }

    private void optimizeDailyRoute(DailyPlan dailyPlan, TravelMode travelMode) {
        Destination accommodation = dailyPlan.getAccommodation();
        List<Destination> spots = dailyPlan.getDestinations().stream()
                .filter(d -> d.getType() == DestinationType.SPOT)
                .collect(Collectors.toList());

        if (spots.isEmpty()) {
            return;
        }

        String[] waypoints = spots.stream()
                .map(Destination::getAddress)
                .toArray(String[]::new);

        DirectionsResult result = null;
        try {
            result = googleMapsService.getOptimizedRoute(
                    accommodation.getAddress(),
                    accommodation.getAddress(),
                    waypoints,
                    travelMode
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 최적화된 경로대로 orderInDay 설정
        List<Destination> optimizedSpots = new ArrayList<>();
        int[] waypointOrder = result.routes[0].waypointOrder;

        for (int order : waypointOrder) {
            Destination spot = spots.get(order);
            optimizedSpots.add(spot);
        }

        // destinations 리스트 업데이트
        dailyPlan.getDestinations().clear();
        dailyPlan.getDestinations().addAll(optimizedSpots);

        updateRouteDetails(dailyPlan, result, travelMode);
    }

    private void updateRouteDetails(DailyPlan dailyPlan, DirectionsResult result, TravelMode travelMode) {
        List<Destination> spots = dailyPlan.getDestinations();
        double totalDistanceInKm = 0.0;
        int totalTimeInMinutes = 0;

        for (int i = 0; i < spots.size(); i++) {
            Destination current = spots.get(i);

            if (i < spots.size() - 1) {
                DirectionsLeg leg = result.routes[0].legs[i];
                double distanceToNext = leg.distance.inMeters / 1000.0;
                int timeToNext = (int)(leg.duration.inSeconds / 60);

                if (travelMode == TravelMode.TRANSIT && leg.steps != null) {
                    StringBuilder transitDetails = new StringBuilder();
                    for (DirectionsStep step : leg.steps) {
                        if (step.travelMode == TravelMode.TRANSIT) {
                            TransitDetails transit = step.transitDetails;
                            transitDetails.append(String.format(
                                    "- %s에서 %s 탑승 (%s)\n",
                                    transit.departureStop.name,
                                    transit.line.shortName != null ? transit.line.shortName : transit.line.name,
                                    transit.departureTime.toString()
                            ));
                            transitDetails.append(String.format(
                                    "- %s에서 하차 (%s)\n",
                                    transit.arrivalStop.name,
                                    transit.arrivalTime.toString()
                            ));
                        } else if (step.travelMode == TravelMode.WALKING) {
                            transitDetails.append(String.format(
                                    "- 도보 %d분\n",
                                    (int)(step.duration.inSeconds / 60)
                            ));
                        }
                    }
                    current.setTransitDetails(transitDetails.toString());
                } else if (travelMode == TravelMode.DRIVING) {
                    // 운전 경로 정보 (선택적)
                    if (leg.steps != null) {
                        StringBuilder drivingDetails = new StringBuilder();
                        for (DirectionsStep step : leg.steps) {
                            drivingDetails.append(String.format(
                                    "- %s\n",
                                    step.htmlInstructions.replaceAll("<[^>]*>", "")
                            ));
                        }
                        current.setDrivingDetails(drivingDetails.toString());
                    }
                }

                current.setDistanceToNext(distanceToNext);
                current.setTimeToNext(timeToNext);

                totalDistanceInKm += distanceToNext;
                totalTimeInMinutes += timeToNext;
            }

            current.setOrderInDay(i + 1);
        }

        // 마지막 관광지에서 숙소로 돌아가는 구간
        if (!spots.isEmpty()) {
            DirectionsLeg lastLeg = result.routes[0].legs[spots.size()];
            totalDistanceInKm += lastLeg.distance.inMeters / 1000.0;
            totalTimeInMinutes += lastLeg.duration.inSeconds / 60;

            if (travelMode == TravelMode.TRANSIT && lastLeg.steps != null) {
                StringBuilder transitDetails = new StringBuilder();
                for (DirectionsStep step : lastLeg.steps) {
                    if (step.travelMode == TravelMode.TRANSIT) {
                        TransitDetails transit = step.transitDetails;
                        transitDetails.append(String.format(
                                "- %s에서 %s 탑승 (%s)\n",
                                transit.departureStop.name,
                                transit.line.shortName != null ? transit.line.shortName : transit.line.name,
                                transit.departureTime.toString()
                        ));
                        transitDetails.append(String.format(
                                "- %s에서 하차 (%s)\n",
                                transit.arrivalStop.name,
                                transit.arrivalTime.toString()
                        ));
                    } else if (step.travelMode == TravelMode.WALKING) {
                        transitDetails.append(String.format(
                                "- 도보 %d분\n",
                                (int)(step.duration.inSeconds / 60)
                        ));
                    }
                }
                spots.get(spots.size()-1).setLastTransitDetails(transitDetails.toString());
            } else if (travelMode == TravelMode.DRIVING && lastLeg.steps != null) {
                StringBuilder drivingDetails = new StringBuilder();
                for (DirectionsStep step : lastLeg.steps) {
                    drivingDetails.append(String.format(
                            "- %s\n",
                            step.htmlInstructions.replaceAll("<[^>]*>", "")
                    ));
                }
                spots.get(spots.size()-1).setLastDrivingDetails(drivingDetails.toString());
            }
        }

        // 관광지 체류 시간 추가 (각 1시간)
        totalTimeInMinutes += spots.size() * 60;

        dailyPlan.setTotalDistance(totalDistanceInKm);
        dailyPlan.setTotalTravelTime(totalTimeInMinutes);
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

    private DailyPlan findBestDayPlan(Destination spot, List<DailyPlan> dailyPlans, TravelMode travelMode) {
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

        return bestPlan;
    }

    private boolean canAddSpotToPlan(DailyPlan plan, Destination spot) {
        // 하루 최대 방문 가능 관광지 수나 다른 제약 조건을 여기서 체크할 수 있습니다
        // 현재는 단순히 true를 반환하지만, 필요한 경우 로직을 추가할 수 있습니다
        return true;
    }

    public List<DailyPlan> optimizeTrip(Itinerary itinerary, Map<Integer, Destination> accommodationsByDay,
                                        List<Destination> spots,
                                        TravelMode travelMode) {
        List<DailyPlan> dailyPlans = new ArrayList<>();
        for (int day = 0; day < accommodationsByDay.size(); day++) {
            DailyPlan dailyPlan = new DailyPlan();
            dailyPlan.setDayNumber(day + 1);  // 표시용 날짜는 1부터 시작
            dailyPlan.setItinerary(itinerary);
            dailyPlan.setAccommodation(accommodationsByDay.get(day + 1));
            dailyPlan.setDestinations(new ArrayList<>()); // 이 부분 추가
            dailyPlans.add(dailyPlan);
        }

        distributeSpots(spots, dailyPlans, travelMode);

        // 각 일자별로 방문 순서 최적화할 때 이전 날짜 정보 전달
        for (int i = 0; i < dailyPlans.size(); i++) {
            DailyPlan previousDayPlan = (i > 0) ? dailyPlans.get(i-1) : null;
            optimizeDailyRoute(dailyPlans.get(i), previousDayPlan, travelMode);
        }

        return dailyPlans;
    }

    // 최적화된 일정을 저장하는 메서드
    public Itinerary saveOptimizedItinerary(
            User user,
            String title,
            String description,
            LocalDate startDate,
            LocalDate endDate,
            List<DailyPlan> optimizedPlans
    ) {
        // 새로운 Itinerary 생성
        Itinerary itinerary = Itinerary.builder()
                .user(user)
                .title(title)
                .description(description)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        // 최적화된 DailyPlan들을 Itinerary에 연결
        for (DailyPlan dailyPlan : optimizedPlans) {
            itinerary.addDailyPlan(dailyPlan);
        }

        // 저장 및 반환
        return itineraryRepository.save(itinerary);
    }

}