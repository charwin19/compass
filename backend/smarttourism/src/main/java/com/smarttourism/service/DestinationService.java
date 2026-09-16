package com.smarttourism.service;

import com.smarttourism.dto.TravelPlanResponse.*;
import com.smarttourism.entity.*;
import com.smarttourism.exception.ResourceNotFoundException;
import com.smarttourism.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Provides destination, hotel, guide, place, hidden place and route data.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DestinationService {

    private final DestinationRepository destinationRepo;
    private final PlaceRepository       placeRepo;
    private final HiddenPlaceRepository hiddenPlaceRepo;
    private final HotelRepository       hotelRepo;
    private final GuideRepository       guideRepo;
    private final RouteRepository       routeRepo;

    public List<DestinationInfo> getAllDestinations() {
        return destinationRepo.findAll().stream()
                .map(this::mapDestination)
                .collect(Collectors.toList());
    }

    public DestinationInfo getDestinationById(Long id) {
        return destinationRepo.findById(id)
                .map(this::mapDestination)
                .orElseThrow(() -> new ResourceNotFoundException("Destination not found: " + id));
    }

    public List<HotelInfo> getHotelsByDestination(Long destId) {
        validateDestination(destId);
        return hotelRepo.findByDestinationId(destId).stream()
                .map(this::mapHotel).collect(Collectors.toList());
    }

    public List<HotelInfo> getHotelsByBudget(Long destId, String budgetType) {
        validateDestination(destId);
        return hotelRepo.findByDestinationIdAndBudgetType(destId, budgetType.toUpperCase()).stream()
                .map(this::mapHotel).collect(Collectors.toList());
    }

    public List<GuideInfo> getGuidesByDestination(Long destId) {
        validateDestination(destId);
        return guideRepo.findByDestinationId(destId).stream()
                .map(this::mapGuide).collect(Collectors.toList());
    }

    public List<PlaceInfo> getPlacesByDestination(Long destId) {
        validateDestination(destId);
        return placeRepo.findByDestinationId(destId).stream()
                .map(this::mapPlace).collect(Collectors.toList());
    }

    public List<HiddenPlaceInfo> getHiddenPlacesByDestination(Long destId) {
        validateDestination(destId);
        return hiddenPlaceRepo.findByDestinationId(destId).stream()
                .map(this::mapHidden).collect(Collectors.toList());
    }

    public RouteInfo getRouteByDestination(Long destId) {
        validateDestination(destId);
        return routeRepo.findFirstByDestinationId(destId)
                .map(this::mapRoute)
                .orElseThrow(() -> new ResourceNotFoundException("No route found for destination: " + destId));
    }

    // ---- private ----

    private void validateDestination(Long destId) {
        if (!destinationRepo.existsById(destId)) {
            throw new ResourceNotFoundException("Destination not found: " + destId);
        }
    }

    private DestinationInfo mapDestination(Destination d) {
        return DestinationInfo.builder()
                .id(d.getId()).name(d.getName()).state(d.getState())
                .description(d.getDescription()).imageUrl(d.getImageUrl())
                .rating(d.getRating()).category(d.getCategory())
                .bestTimeToVisit(d.getBestTimeToVisit()).build();
    }

    private HotelInfo mapHotel(Hotel h) {
        return HotelInfo.builder()
                .id(h.getId()).name(h.getName()).description(h.getDescription())
                .imageUrl(h.getImageUrl()).rating(h.getRating()).reviewCount(h.getReviewCount())
                .pricePerNight(h.getPricePerNight()).distanceFromCenter(h.getDistanceFromCenter())
                .budgetType(h.getBudgetType()).facilities(h.getFacilities()).address(h.getAddress()).build();
    }

    private GuideInfo mapGuide(Guide g) {
        return GuideInfo.builder()
                .id(g.getId()).name(g.getName()).bio(g.getBio()).photoUrl(g.getPhotoUrl())
                .experienceYears(g.getExperienceYears()).languages(g.getLanguages())
                .rating(g.getRating()).reviewCount(g.getReviewCount())
                .contactMasked(g.getContactMasked()).pricePerDay(g.getPricePerDay())
                .specialization(g.getSpecialization()).build();
    }

    private PlaceInfo mapPlace(Place p) {
        return PlaceInfo.builder()
                .id(p.getId()).name(p.getName()).description(p.getDescription())
                .imageUrl(p.getImageUrl()).location(p.getLocation())
                .visitingTimeHours(p.getVisitingTimeHours()).entryFee(p.getEntryFee())
                .openTime(p.getOpenTime()).closeTime(p.getCloseTime())
                .budgetCategory(p.getBudgetCategory()).tags(p.getTags()).build();
    }

    private HiddenPlaceInfo mapHidden(HiddenPlace h) {
        return HiddenPlaceInfo.builder()
                .id(h.getId()).name(h.getName()).location(h.getLocation())
                .description(h.getDescription()).whyVisit(h.getWhyVisit())
                .bestTimeToVisit(h.getBestTimeToVisit()).visitingTimeHours(h.getVisitingTimeHours())
                .imageUrl(h.getImageUrl()).difficultyLevel(h.getDifficultyLevel()).build();
    }

    private RouteInfo mapRoute(Route r) {
        return RouteInfo.builder()
                .id(r.getId()).startLocation(r.getStartLocation()).endLocation(r.getEndLocation())
                .distanceKm(r.getDistanceKm()).estimatedTimeHours(r.getEstimatedTimeHours())
                .roadType(r.getRoadType()).roadCondition(r.getRoadCondition())
                .importantStops(r.getImportantStops()).alternativeRoute(r.getAlternativeRoute())
                .travelMode(r.getTravelMode()).build();
    }
}
