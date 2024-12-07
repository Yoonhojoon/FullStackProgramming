package com.fullstack.demo.entity.naver;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Route {
    private Leg[] legs;
    private int[] waypointOrder;
}
