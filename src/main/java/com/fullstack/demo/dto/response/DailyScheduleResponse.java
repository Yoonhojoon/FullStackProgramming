package com.fullstack.demo.dto.response;

import com.fullstack.demo.dto.UITripItemDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DailyScheduleResponse {
    private int dayNumber;
    private List<UITripItemDTO> items;
}