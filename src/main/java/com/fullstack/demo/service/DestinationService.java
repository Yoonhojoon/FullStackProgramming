package com.fullstack.demo.service;

import com.fullstack.demo.entity.Destination;
import com.fullstack.demo.repository.DestinationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DestinationService {

    private final DestinationRepository destinationRepository;

    @Autowired
    public DestinationService(DestinationRepository destinationRepository) {
        this.destinationRepository = destinationRepository;
    }

    public void saveDestination(Destination destination) {
        destinationRepository.save(destination);
    }
}
