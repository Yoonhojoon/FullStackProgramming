package com.fullstack.demo.entity.naver;

import lombok.Data;

import java.util.List;

@Data
public class GeocodeResponse {
    private List<Address> addresses;
}
