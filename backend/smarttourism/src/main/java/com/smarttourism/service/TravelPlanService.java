package com.smarttourism.service;

import com.smarttourism.dto.TravelPlanRequest;
import com.smarttourism.dto.TravelPlanResponse;
import com.smarttourism.dto.TravelPlanResponse.*;
import com.smarttourism.entity.*;
import com.smarttourism.exception.ResourceNotFoundException;
import com.smarttourism.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Generates personalised travel plans with day-wise itinerary,
 * hotel/guide/place recommendations, and budget breakdown.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TravelPlanService {

    private final DestinationRepository destinationRepo;
    private final PlaceRepository       placeRepo;
    private final HiddenPlaceRepository hiddenPlaceRepo;
    private final HotelRepository       hotelRepo;
    private final GuideRepository       guideRepo;
    private final RouteRepository       routeRepo;
    private final TravelPlanRepository  travelPlanRepo;
    private final UserRepository        userRepo;

    /**
     * Generate a new travel plan and persist it for the authenticated user.
     */
    @Transactional
    public TravelPlanResponse generatePlan(TravelPlanRequest req, Long userId) {

        // 1. Resolve destination (case-insensitive)
        Destination dest = destinationRepo
                .findByNameIgnoreCase(req.getDestination())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Destination '" + req.getDestination() + "' not found. " +
                        "Try: Ooty, Kodaikanal, Munnar, Mysore, Coimbatore, Chennai"));

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // 2. Determine travel style from budget/day ratio
        int budgetPerDay = req.getBudget() / req.getNumberOfDays();
        String travelStyle;
        if      (budgetPerDay < 1500) travelStyle = "BUDGET";
        else if (budgetPerDay < 4000) travelStyle = "STANDARD";
        else                          travelStyle = "LUXURY";

        // 3. Save the travel plan record
        TravelPlan plan = TravelPlan.builder()
                .destination(dest.getName())
                .budget(req.getBudget())
                .numberOfDays(req.getNumberOfDays())
                .numberOfTravelers(req.getNumberOfTravelers())
                .travelType(req.getTravelType() != null ? req.getTravelType().toUpperCase() : "SOLO")
                .travelStyle(travelStyle)
                .status(TravelPlan.Status.PLANNED)
                .user(user)
                .build();
        plan = travelPlanRepo.save(plan);

        // 4. Fetch recommendations
        List<Place>       places      = placeRepo.findByDestinationId(dest.getId());
        List<HiddenPlace> hidden      = hiddenPlaceRepo.findByDestinationId(dest.getId());
        List<Hotel>       allHotels   = hotelRepo.findByDestinationId(dest.getId());
        List<Guide>       guides      = guideRepo.findByDestinationId(dest.getId());
        Optional<Route>   routeOpt    = routeRepo.findFirstByDestinationId(dest.getId());

        // Filter hotels by travel style
        List<Hotel> filteredHotels = filterHotelsByStyle(allHotels, travelStyle);

        // 5. Build budget summary
        BudgetSummary budgetSummary = buildBudgetSummary(req, filteredHotels, travelStyle);

        // 6. Build day-wise itinerary
        List<ItineraryDayGroup> itinerary = buildItinerary(places, req.getNumberOfDays(), budgetSummary);

        log.info("Travel plan {} generated for destination '{}', user id={}", plan.getId(), dest.getName(), userId);

        // 7. Assemble response
        return TravelPlanResponse.builder()
                .id(plan.getId())
                .destination(plan.getDestination())
                .budget(plan.getBudget())
                .numberOfDays(plan.getNumberOfDays())
                .numberOfTravelers(plan.getNumberOfTravelers())
                .travelType(plan.getTravelType())
                .travelStyle(plan.getTravelStyle())
                .status(plan.getStatus().name())
                .createdAt(plan.getCreatedAt())
                .destinationInfo(mapDestination(dest))
                .touristPlaces(places.stream().map(this::mapPlace).collect(Collectors.toList()))
                .hiddenPlaces(hidden.stream().map(this::mapHidden).collect(Collectors.toList()))
                .hotels(filteredHotels.stream().map(this::mapHotel).collect(Collectors.toList()))
                .guides(guides.stream().map(this::mapGuide).collect(Collectors.toList()))
                .route(routeOpt.map(this::mapRoute).orElse(null))
                .itinerary(itinerary)
                .budgetSummary(budgetSummary)
                .build();
    }

    /** Get all travel plans for a specific user. */
    @Transactional(readOnly = true)
    public List<TravelPlanResponse> getMyPlans(Long userId) {
        return travelPlanRepo.findByUserId(userId).stream()
                .map(plan -> TravelPlanResponse.builder()
                        .id(plan.getId())
                        .destination(plan.getDestination())
                        .budget(plan.getBudget())
                        .numberOfDays(plan.getNumberOfDays())
                        .travelStyle(plan.getTravelStyle())
                        .status(plan.getStatus().name())
                        .createdAt(plan.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    /** Get a specific plan by ID for the authenticated user. */
    @Transactional(readOnly = true)
    public TravelPlanResponse getPlanById(Long planId, Long userId) {
        TravelPlan plan = travelPlanRepo.findByIdAndUserId(planId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Travel plan not found"));

        Destination dest = destinationRepo.findByNameIgnoreCase(plan.getDestination())
                .orElseThrow(() -> new ResourceNotFoundException("Destination data not found"));

        List<Place>       places    = placeRepo.findByDestinationId(dest.getId());
        List<HiddenPlace> hidden    = hiddenPlaceRepo.findByDestinationId(dest.getId());
        List<Hotel>       allHotels = hotelRepo.findByDestinationId(dest.getId());
        List<Guide>       guides    = guideRepo.findByDestinationId(dest.getId());
        Optional<Route>   routeOpt  = routeRepo.findFirstByDestinationId(dest.getId());

        List<Hotel> filteredHotels = filterHotelsByStyle(allHotels, plan.getTravelStyle());
        BudgetSummary budgetSummary = buildBudgetSummary(
                buildRequest(plan), filteredHotels, plan.getTravelStyle());
        List<ItineraryDayGroup> itinerary = buildItinerary(places, plan.getNumberOfDays(), budgetSummary);

        return TravelPlanResponse.builder()
                .id(plan.getId())
                .destination(plan.getDestination())
                .budget(plan.getBudget())
                .numberOfDays(plan.getNumberOfDays())
                .numberOfTravelers(plan.getNumberOfTravelers())
                .travelType(plan.getTravelType())
                .travelStyle(plan.getTravelStyle())
                .status(plan.getStatus().name())
                .createdAt(plan.getCreatedAt())
                .destinationInfo(mapDestination(dest))
                .touristPlaces(places.stream().map(this::mapPlace).collect(Collectors.toList()))
                .hiddenPlaces(hidden.stream().map(this::mapHidden).collect(Collectors.toList()))
                .hotels(filteredHotels.stream().map(this::mapHotel).collect(Collectors.toList()))
                .guides(guides.stream().map(this::mapGuide).collect(Collectors.toList()))
                .route(routeOpt.map(this::mapRoute).orElse(null))
                .itinerary(itinerary)
                .budgetSummary(budgetSummary)
                .build();
    }

    // =====================================================================
    // BUDGET CALCULATION
    // =====================================================================

    private BudgetSummary buildBudgetSummary(TravelPlanRequest req, List<Hotel> hotels, String style) {
        int days      = req.getNumberOfDays();
        int travelers = Math.max(1, req.getNumberOfTravelers() == null ? 1 : req.getNumberOfTravelers());
        int budget    = req.getBudget();

        // Pick the cheapest suitable hotel
        int hotelRate = hotels.isEmpty() ? 1500 :
                hotels.stream().mapToInt(Hotel::getPricePerNight).min().orElse(1500);
        int accommodation = hotelRate * days;

        // Food estimate: ₹300/person/day (budget), ₹600 (standard), ₹1200 (luxury)
        int foodRate = switch (style) {
            case "LUXURY"   -> 1200;
            case "STANDARD" -> 600;
            default         -> 300;
        };
        int food = foodRate * travelers * days;

        // Transport: 20% of remaining budget
        int activitiesAndTransport = (int)((budget - accommodation - food) * 0.5);
        if (activitiesAndTransport < 0) activitiesAndTransport = 0;
        int transport  = activitiesAndTransport / 2;
        int activities = activitiesAndTransport / 2;

        int total  = accommodation + food + transport + activities;
        int buffer = budget - total;

        return BudgetSummary.builder()
                .totalBudget(budget)
                .estimatedAccommodation(accommodation)
                .estimatedFood(food)
                .estimatedTransport(transport)
                .estimatedActivities(activities)
                .estimatedTotal(total)
                .remainingBuffer(buffer)
                .travelStyle(style)
                .build();
    }

    // =====================================================================
    // DAY-WISE ITINERARY BUILDER
    // =====================================================================

    private List<ItineraryDayGroup> buildItinerary(List<Place> places, int days, BudgetSummary budget) {
        List<ItineraryDayGroup> result = new ArrayList<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");

        // Distribute places across days (3-4 places per day max)
        List<Place> sortedPlaces = new ArrayList<>(places);
        sortedPlaces.sort(Comparator.comparingInt(Place::getRecommendedDay));

        int placesPerDay = Math.max(1, Math.min(4, (int) Math.ceil((double) sortedPlaces.size() / days)));
        int placeIdx = 0;

        for (int day = 1; day <= days; day++) {
            List<ActivityInfo> activities = new ArrayList<>();
            LocalTime cursor = LocalTime.of(8, 30);

            // Morning: Breakfast
            activities.add(ActivityInfo.builder()
                    .placeName("Hotel Breakfast")
                    .description("Start your day with a hearty breakfast at the hotel")
                    .startTime(cursor.format(fmt))
                    .endTime(cursor.plusMinutes(60).format(fmt))
                    .activityType("MEAL")
                    .estimatedCost(budget.getEstimatedFood() / (days * 3))
                    .notes("Try local specialties if available")
                    .build());
            cursor = cursor.plusMinutes(75);

            // Tourist places for the day
            for (int p = 0; p < placesPerDay && placeIdx < sortedPlaces.size(); p++, placeIdx++) {
                Place place = sortedPlaces.get(placeIdx);
                int durationMins = (int)(place.getVisitingTimeHours() * 60);
                activities.add(ActivityInfo.builder()
                        .placeName(place.getName())
                        .description(place.getDescription())
                        .startTime(cursor.format(fmt))
                        .endTime(cursor.plusMinutes(durationMins).format(fmt))
                        .activityType("SIGHTSEEING")
                        .estimatedCost(place.getEntryFee() != null ? place.getEntryFee() : 0)
                        .notes("Open: " + place.getOpenTime() + " – " + place.getCloseTime())
                        .build());
                cursor = cursor.plusMinutes(durationMins + 45); // travel buffer

                // Lunch after 2nd attraction
                if (p == 1 && cursor.getHour() >= 12) {
                    activities.add(ActivityInfo.builder()
                            .placeName("Lunch")
                            .description("Enjoy local cuisine at a nearby restaurant")
                            .startTime(cursor.format(fmt))
                            .endTime(cursor.plusMinutes(60).format(fmt))
                            .activityType("MEAL")
                            .estimatedCost(budget.getEstimatedFood() / (days * 3))
                            .notes("Ask your guide for the best local restaurant")
                            .build());
                    cursor = cursor.plusMinutes(75);
                }
            }

            // Evening: Dinner
            LocalTime dinnerTime = LocalTime.of(19, 30);
            if (cursor.isBefore(dinnerTime)) cursor = dinnerTime;
            activities.add(ActivityInfo.builder()
                    .placeName("Dinner")
                    .description("Evening meal — explore local specialties or hotel restaurant")
                    .startTime(cursor.format(fmt))
                    .endTime(cursor.plusMinutes(90).format(fmt))
                    .activityType("MEAL")
                    .estimatedCost(budget.getEstimatedFood() / (days * 3))
                    .notes("Last day? Try the most iconic local dish")
                    .build());

            result.add(ItineraryDayGroup.builder()
                    .dayNumber(day)
                    .activities(activities)
                    .build());
        }

        return result;
    }

    // =====================================================================
    // HOTEL FILTER
    // =====================================================================

    private List<Hotel> filterHotelsByStyle(List<Hotel> hotels, String style) {
        // Primary: match style
        List<Hotel> filtered = hotels.stream()
                .filter(h -> style.equalsIgnoreCase(h.getBudgetType()))
                .collect(Collectors.toList());
        // Fallback: all hotels if no match
        return filtered.isEmpty() ? hotels : filtered;
    }

    // =====================================================================
    // MAPPERS
    // =====================================================================

    private DestinationInfo mapDestination(Destination d) {
        return DestinationInfo.builder()
                .id(d.getId()).name(d.getName()).state(d.getState())
                .description(d.getDescription()).imageUrl(d.getImageUrl())
                .rating(d.getRating()).category(d.getCategory())
                .bestTimeToVisit(d.getBestTimeToVisit()).build();
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

    private RouteInfo mapRoute(Route r) {
        return RouteInfo.builder()
                .id(r.getId()).startLocation(r.getStartLocation()).endLocation(r.getEndLocation())
                .distanceKm(r.getDistanceKm()).estimatedTimeHours(r.getEstimatedTimeHours())
                .roadType(r.getRoadType()).roadCondition(r.getRoadCondition())
                .importantStops(r.getImportantStops()).alternativeRoute(r.getAlternativeRoute())
                .travelMode(r.getTravelMode()).build();
    }

    private TravelPlanRequest buildRequest(TravelPlan plan) {
        TravelPlanRequest req = new TravelPlanRequest();
        req.setDestination(plan.getDestination());
        req.setBudget(plan.getBudget());
        req.setNumberOfDays(plan.getNumberOfDays());
        req.setNumberOfTravelers(plan.getNumberOfTravelers() != null ? plan.getNumberOfTravelers() : 1);
        req.setTravelType(plan.getTravelType());
        return req;
    }
}
