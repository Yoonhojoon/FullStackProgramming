package com.fullstack.demo.service;

import com.google.maps.GeoApiContext;
import com.google.maps.DirectionsApi;
import com.google.maps.model.DirectionsResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GoogleRoutesService {
    private final GeoApiContext context;

    public GoogleRoutesService(@Value("${google.api.key}") String apiKey) {
        this.context = new GeoApiContext.Builder()
                .apiKey(apiKey)
                .build();
    }

    public DirectionsResult getDirections(String origin, String destination) throws Exception {
        return DirectionsApi.getDirections(context, origin, destination).await();
    }

    public GeoApiContext getContext() {
        return this.context;
    }

    // 서비스가 종료될 때 context를 정리하기 위한 메서드
    public void shutdown() {
        if (context != null) {
            context.shutdown();
        }
    }
}