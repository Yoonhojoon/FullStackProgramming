package com.fullstack.demo.entity.naver;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LatLng {
    private double lat;
    private double lng;

    @Override
    public String toString() {
        return lng + "," + lat;
    }
}