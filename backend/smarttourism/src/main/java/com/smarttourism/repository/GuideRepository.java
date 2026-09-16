package com.smarttourism.repository;

import com.smarttourism.entity.Guide;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GuideRepository extends JpaRepository<Guide, Long> {
    List<Guide> findByDestinationId(Long destinationId);
    List<Guide> findByDestinationIdAndPricePerDayLessThanEqual(Long destinationId, Integer maxPrice);
}
