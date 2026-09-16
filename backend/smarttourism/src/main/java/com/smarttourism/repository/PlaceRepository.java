package com.smarttourism.repository;

import com.smarttourism.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {
    List<Place> findByDestinationId(Long destinationId);
    List<Place> findByDestinationIdAndBudgetCategory(Long destinationId, String budgetCategory);
    List<Place> findByDestinationIdAndTagsContainingIgnoreCase(Long destinationId, String tag);
}
