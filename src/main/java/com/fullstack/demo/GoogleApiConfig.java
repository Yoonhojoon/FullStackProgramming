package com.fullstack.demo;

import com.fullstack.demo.service.GoogleRoutesService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GoogleApiConfig {
    @Value("${google.api.key}")
    private String apiKey;

    @Bean
    public GoogleRoutesService googleRoutesService() {
        return new GoogleRoutesService(apiKey);
    }
}