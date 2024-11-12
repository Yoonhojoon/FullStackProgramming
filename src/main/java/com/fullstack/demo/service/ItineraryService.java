package com.fullstack.demo.service;

import com.fullstack.demo.dto.ItineraryCreateRequestDto;
import com.fullstack.demo.dto.ItineraryResponseDto;
import com.fullstack.demo.entity.Itinerary;
import com.fullstack.demo.entity.User;
import com.fullstack.demo.repository.ItineraryRepository;
import com.fullstack.demo.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

// ItineraryService.java
@Service
@RequiredArgsConstructor
@Transactional
public class ItineraryService {
    private final ItineraryRepository itineraryRepository;
    private final UserRepository userRepository;

    @Transactional
    public ItineraryResponseDto createItinerary(Long userId, ItineraryCreateRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Itinerary itinerary = requestDto.toEntity(user);
        Itinerary savedItinerary = itineraryRepository.save(itinerary);
        return ItineraryResponseDto.from(savedItinerary);
    }

    public List<ItineraryResponseDto> getUserItineraries(Long userId) {
        return itineraryRepository.findByUser_userId(userId).stream()
                .map(ItineraryResponseDto::from)
                .collect(Collectors.toList());
    }
}
