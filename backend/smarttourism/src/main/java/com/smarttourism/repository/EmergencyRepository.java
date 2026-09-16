package com.smarttourism.repository;

import com.smarttourism.entity.EmergencyAlert;
import com.smarttourism.entity.EmergencyAlert.AlertStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmergencyRepository extends JpaRepository<EmergencyAlert, Long> {
    List<EmergencyAlert> findAllByOrderByCreatedAtDesc();
    List<EmergencyAlert> findByStatus(AlertStatus status);
    List<EmergencyAlert> findByUserId(Long userId);
}
