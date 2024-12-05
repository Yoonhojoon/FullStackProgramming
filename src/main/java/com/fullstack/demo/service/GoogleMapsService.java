package com.fullstack.demo.service;

import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.annotation.PreDestroy;

@Service
public class GoogleMapsService {

    private final GeoApiContext context;

    public GoogleMapsService(@Value("${google.api.key}") String apiKey) {
        this.context = new GeoApiContext.Builder()
                .apiKey(apiKey)
                .build();
    }

    public DirectionsResult getOptimizedRoute(String origin, String destination,
                                              String[] waypoints, TravelMode travelMode) throws Exception {
        return DirectionsApi.newRequest(context)
                .mode(travelMode)
                .origin(origin)
                .destination(destination)
                .waypoints(waypoints)
                .optimizeWaypoints(true)  // optimize -> optimizeWaypoints로 수정
                .await();
    }

    public DirectionsResult getDirections(String origin, String destination,
                                          TravelMode travelMode) throws Exception {
        return DirectionsApi.newRequest(context)
                .mode(travelMode)
                .origin(origin)
                .destination(destination)
                .await();
    }

    @PreDestroy
    public void closeContext() {
        if (context != null) {
            context.shutdown();
        }
    }
}