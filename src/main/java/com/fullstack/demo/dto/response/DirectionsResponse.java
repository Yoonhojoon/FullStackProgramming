package com.fullstack.demo.dto.response;

import lombok.Data;

import java.nio.file.Path;
import java.util.List;

@Data
public class DirectionsResponse {
    private int code;
    private String message;
    private Route route;
    private String currentDateTime;

    @Data
    public static class Route {
        private List<Trafast> trafast;  // guide를 직접 갖지 않고 traoptimal 배열을 가짐

        @Data
        public static class Trafast {
            private List<Guide> guide;
            private List<List<Double>> path;  // 좌표 배열
            private List<Section> section;
            private Summary summary;
        }

        @Data
        public static class Section {
            private int congestion;
            private int distance;
            private String name;
            private int pointCount;
            private int pointIndex;
            private int speed;
        }

        @Data
        public static class Guide {
            private int distance;
            private int duration;
            private String instructions;
            private int type;
            private int pointIndex;
        }

        @Data
        public static class Summary {
            private String departureTime;
            private int distance;
            private int duration;
            private int tollFare;
            private int taxiFare;
            private int fuelPrice;
            private List<List<Double>> bbox;
            private WaypointLocation start;
            private WaypointLocation goal;
            private List<WaypointLocation> waypoints;
        }

        @Data
        public static class WaypointLocation {
            private List<Double> location;
            private int distance;
            private int duration;
            private int dir;
            private int pointIndex;
        }
    }
}