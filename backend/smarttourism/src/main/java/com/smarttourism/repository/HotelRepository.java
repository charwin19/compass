package com.smarttourism.repository;

import com.smarttourism.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {
    List<Hotel> findByDestinationId(Long destinationId);
    List<Hotel> findByDestinationIdAndBudgetType(Long destinationId, String budgetType);
    List<Hotel> findByDestinationIdAndPricePerNightLessThanEqual(Long destinationId, Integer maxPrice);
}
