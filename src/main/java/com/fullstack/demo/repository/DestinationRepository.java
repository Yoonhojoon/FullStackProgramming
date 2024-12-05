package com.fullstack.demo.repository;

import com.fullstack.demo.entity.Destination;
import com.fullstack.demo.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DestinationRepository extends JpaRepository<Destination, Long> {


}
