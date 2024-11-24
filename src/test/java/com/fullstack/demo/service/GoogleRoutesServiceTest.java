package com.fullstack.demo.service;

import static org.junit.jupiter.api.Assertions.*;

import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsStep;
import org.junit.jupiter.api.Test;

class GoogleRoutesServiceTest {

    @Test
    void testGetDirectionsWithRealApiKey() throws Exception {
        String apiKey = "AIzaSyB7eNjy6eGdGPYh1CEtq9mX8-jJZQoheDk";
        GoogleRoutesService service = new GoogleRoutesService(apiKey);

        String origin = "San Francisco, CA";
        String destination = "Berkeley, CA";

        DirectionsResult result = service.getDirections(origin, destination);

        assertNotNull(result);
        assertTrue(result.routes.length > 0, "Routes should not be empty");

        // steps 정보 출력
        DirectionsLeg leg = result.routes[0].legs[0];
        DirectionsStep[] steps = leg.steps;

        System.out.println("=== 상세 이동 방법 ===");
        for (int i = 0; i < steps.length; i++) {
            System.out.println("Step " + (i+1) + ":");
            System.out.println("- 설명: " + steps[i].htmlInstructions);  // HTML 태그가 포함된 이동 지시사항
            System.out.println("- 거리: " + steps[i].distance);          // 해당 step의 거리
            System.out.println("- 예상 시간: " + steps[i].duration);     // 해당 step의 예상 소요 시간
            System.out.println();
        }
    }
}
