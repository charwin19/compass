package com.smarttourism.controller;

import com.smarttourism.dto.TravelPlanRequest;
import com.smarttourism.dto.TravelPlanResponse;
import com.smarttourism.service.TravelPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Travel plan endpoints — requires authentication.
 */
@RestController
@RequestMapping("/api/plans")
@RequiredArgsConstructor
public class TravelPlanController {

    private final TravelPlanService travelPlanService;

    /**
     * POST /api/plans
     * Generate a new personalised travel plan.
     */
    @PostMapping
    public ResponseEntity<TravelPlanResponse> generatePlan(
            @Valid @RequestBody TravelPlanRequest req,
            @AuthenticationPrincipal Long userId) {

        TravelPlanResponse response = travelPlanService.generatePlan(req, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/plans
     * List all travel plans for the authenticated user.
     */
    @GetMapping
    public ResponseEntity<List<TravelPlanResponse>> getMyPlans(
            @AuthenticationPrincipal Long userId) {

        return ResponseEntity.ok(travelPlanService.getMyPlans(userId));
    }

    /**
     * GET /api/plans/{id}
     * Get a specific travel plan with full details.
     * Only the owner can access their own plans.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TravelPlanResponse> getPlanById(
            @PathVariable Long id,
            @AuthenticationPrincipal Long userId) {

        return ResponseEntity.ok(travelPlanService.getPlanById(id, userId));
    }
}
