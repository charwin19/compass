package com.smarttourism.controller;

import com.smarttourism.dto.TravelPlanResponse.*;
import com.smarttourism.service.DestinationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Public read-only endpoints for destination data.
 * No authentication required for GET requests.
 */
@RestController
@RequestMapping("/api/destinations")
@RequiredArgsConstructor
public class DestinationController {

    private final DestinationService destinationService;

    /** GET /api/destinations — list all destinations */
    @GetMapping
    public ResponseEntity<List<DestinationInfo>> getAllDestinations() {
        return ResponseEntity.ok(destinationService.getAllDestinations());
    }

    /** GET /api/destinations/{id} — get destination detail */
    @GetMapping("/{id}")
    public ResponseEntity<DestinationInfo> getById(@PathVariable Long id) {
        return ResponseEntity.ok(destinationService.getDestinationById(id));
    }

    /** GET /api/destinations/{id}/hotels — hotels for destination */
    @GetMapping("/{id}/hotels")
    public ResponseEntity<List<HotelInfo>> getHotels(
            @PathVariable Long id,
            @RequestParam(required = false) String budgetType) {

        if (budgetType != null && !budgetType.isBlank()) {
            return ResponseEntity.ok(destinationService.getHotelsByBudget(id, budgetType));
        }
        return ResponseEntity.ok(destinationService.getHotelsByDestination(id));
    }

    /** GET /api/destinations/{id}/guides — guides for destination */
    @GetMapping("/{id}/guides")
    public ResponseEntity<List<GuideInfo>> getGuides(@PathVariable Long id) {
        return ResponseEntity.ok(destinationService.getGuidesByDestination(id));
    }

    /** GET /api/destinations/{id}/places — tourist places */
    @GetMapping("/{id}/places")
    public ResponseEntity<List<PlaceInfo>> getPlaces(@PathVariable Long id) {
        return ResponseEntity.ok(destinationService.getPlacesByDestination(id));
    }

    /** GET /api/destinations/{id}/hidden-places — hidden gems */
    @GetMapping("/{id}/hidden-places")
    public ResponseEntity<List<HiddenPlaceInfo>> getHiddenPlaces(@PathVariable Long id) {
        return ResponseEntity.ok(destinationService.getHiddenPlacesByDestination(id));
    }

    /** GET /api/destinations/{id}/route — recommended route */
    @GetMapping("/{id}/route")
    public ResponseEntity<RouteInfo> getRoute(@PathVariable Long id) {
        return ResponseEntity.ok(destinationService.getRouteByDestination(id));
    }
}
