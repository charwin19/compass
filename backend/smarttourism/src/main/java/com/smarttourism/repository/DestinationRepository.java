package com.smarttourism.repository;

import com.smarttourism.entity.Destination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DestinationRepository extends JpaRepository<Destination, Long> {
    Optional<Destination> findByNameIgnoreCase(String name);
    List<Destination> findByCategory(String category);
    List<Destination> findByStateIgnoreCase(String state);
}
