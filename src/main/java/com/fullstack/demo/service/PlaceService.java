package com.fullstack.demo.service;

import com.fullstack.demo.entity.Place;
import com.fullstack.demo.repository.PlaceRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class PlaceService {
    private final PlaceRepository placeRepository;

    @Autowired
    public PlaceService(PlaceRepository placeRepository) {
        this.placeRepository = placeRepository;
    }

    public Place save(Place place) {
        return placeRepository.save(place);
    }

}