package com.smarttourism.controller;

import com.smarttourism.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Weather endpoints — public (no auth required).
 */
@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;

    /**
     * GET /api/weather?city=Ooty
     * Returns current weather for the given city.
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getCurrentWeather(
            @RequestParam String city) {

        return ResponseEntity.ok(weatherService.getWeather(city));
    }

    /**
     * GET /api/weather/forecast?city=Ooty
     * Returns 5-day forecast for the given city.
     */
    @GetMapping("/forecast")
    public ResponseEntity<Map<String, Object>> getForecast(
            @RequestParam String city) {

        return ResponseEntity.ok(weatherService.getForecast(city));
    }
}
