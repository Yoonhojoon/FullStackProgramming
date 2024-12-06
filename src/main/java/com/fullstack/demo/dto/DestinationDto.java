package com.fullstack.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "목적지 정보")
public class DestinationDto {
    @Schema(description = "장소명", example = "경복궁")
    private String name;

    @Schema(description = "주소", example = "서울특별시 종로구 사직로 161")
    private String address;

    @Schema(description = "위도", example = "37.579617")
    private double latitude;

    @Schema(description = "경도", example = "126.977041")
    private double longitude;

    @Schema(description = "장소 타입", example = "SPOT")
    private String type;
}
