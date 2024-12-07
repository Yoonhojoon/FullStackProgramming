
package com.fullstack.demo.entity.naver;

import lombok.Data;

import java.util.List;

@Data
public class NaverRoute {
    private String summary;
    private List<NaverPath> path;
}
