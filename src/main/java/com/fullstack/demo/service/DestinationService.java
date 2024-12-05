package com.fullstack.demo.service;

import com.fullstack.demo.entity.Destination;
import com.fullstack.demo.repository.DestinationRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class DestinationService {
    private final DestinationRepository destinationRepository;

    @Autowired
    public DestinationService(DestinationRepository destinationRepository) {
        this.destinationRepository = destinationRepository;
    }

    public Destination save(Destination destination) {
        return destinationRepository.save(destination);
    }

}