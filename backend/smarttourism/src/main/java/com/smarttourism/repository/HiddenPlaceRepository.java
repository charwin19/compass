package com.smarttourism.repository;

import com.smarttourism.entity.HiddenPlace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HiddenPlaceRepository extends JpaRepository<HiddenPlace, Long> {
    List<HiddenPlace> findByDestinationId(Long destinationId);
}
