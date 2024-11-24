package com.fullstack.demo.repository;

import com.fullstack.demo.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceRepository extends JpaRepository<Place, Long> {


}
