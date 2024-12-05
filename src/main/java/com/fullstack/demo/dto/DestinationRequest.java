package com.fullstack.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DestinationRequest {

    @NotBlank(message = "유형은 필수입니다.")
    private String type; // "숙박" 또는 "장소"

    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    @NotBlank(message = "주소는 필수입니다.")
    private String address;

    @NotNull(message = "위도는 필수입니다.")
    private Double latitude;

    @NotNull(message = "경도는 필수입니다.")
    private Double longitude;

    @Size(max = 1000, message = "대중교통 경로는 최대 1000자까지 입력 가능합니다.")
    private String transitDetails;

    @Size(max = 1000, message = "숙소까지 대중교통 경로는 최대 1000자까지 입력 가능합니다.")
    private String lastTransitDetails;

    @Size(max = 1000, message = "자동차 경로는 최대 1000자까지 입력 가능합니다.")
    private String drivingDetails;

    @Size(max = 1000, message = "숙소까지 자동차 경로는 최대 1000자까지 입력 가능합니다.")
    private String lastDrivingDetails;

    private Integer orderInDay;

    private Double distanceToNext;

    private Integer timeToNext;
}
