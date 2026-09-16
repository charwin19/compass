package com.smarttourism.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;

/**
 * Fetches live weather data from OpenWeatherMap API.
 * Falls back to a mock response when the API key is not configured.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherService {

    @Value("${app.weather.api-key:}")
    private String apiKey;

    @Value("${app.weather.base-url}")
    private String baseUrl;

    private final WebClient.Builder webClientBuilder;

    /**
     * Fetch current weather + 5-day forecast for the given city.
     *
     * @param city city name (e.g. "Ooty", "Munnar")
     * @return raw JSON map from OpenWeatherMap, or mock data if key is not set
     */
    public Map<String, Object> getWeather(String city) {
        if (city == null || city.isBlank()) {
            throw new IllegalArgumentException("City name is required");
        }

        if (apiKey == null || apiKey.isBlank()) {
            log.warn("Weather API key not configured — returning mock weather data for '{}'", city);
            return buildMockWeather(city);
        }

        try {
            WebClient client = webClientBuilder.baseUrl(baseUrl).build();

            @SuppressWarnings("unchecked")
            Map<String, Object> current = client.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/weather")
                            .queryParam("q", city)
                            .queryParam("appid", apiKey)
                            .queryParam("units", "metric")
                            .queryParam("lang", "en")
                            .build())
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            return current;

        } catch (WebClientResponseException.NotFound ex) {
            throw new IllegalArgumentException("City '" + city + "' not found. Please check the spelling.");
        } catch (Exception ex) {
            log.error("Weather API error for city '{}': {}", city, ex.getMessage());
            log.warn("Falling back to mock weather data");
            return buildMockWeather(city);
        }
    }

    /**
     * Fetch 5-day / 3-hour forecast for a city.
     */
    public Map<String, Object> getForecast(String city) {
        if (city == null || city.isBlank()) {
            throw new IllegalArgumentException("City name is required");
        }

        if (apiKey == null || apiKey.isBlank()) {
            return buildMockForecast(city);
        }

        try {
            WebClient client = webClientBuilder.baseUrl(baseUrl).build();

            @SuppressWarnings("unchecked")
            Map<String, Object> forecast = client.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/forecast")
                            .queryParam("q", city)
                            .queryParam("appid", apiKey)
                            .queryParam("units", "metric")
                            .queryParam("cnt", 40)
                            .build())
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            return forecast;

        } catch (Exception ex) {
            log.error("Forecast API error for '{}': {}", city, ex.getMessage());
            return buildMockForecast(city);
        }
    }

    // =====================================================================
    // MOCK FALLBACK — used when API key is not configured
    // =====================================================================

    private Map<String, Object> buildMockWeather(String city) {
        return Map.of(
            "name", city,
            "mock", true,
            "main", Map.of(
                "temp", 22.5,
                "feels_like", 21.0,
                "humidity", 68,
                "pressure", 1013,
                "temp_min", 18.0,
                "temp_max", 26.0
            ),
            "weather", java.util.List.of(Map.of(
                "main", "Partly Cloudy",
                "description", "partly cloudy skies — pleasant weather for tourism",
                "icon", "02d"
            )),
            "wind", Map.of("speed", 3.5, "deg", 220),
            "clouds", Map.of("all", 40),
            "visibility", 10000,
            "message", "Demo mode — configure WEATHER_API_KEY for live data"
        );
    }

    private Map<String, Object> buildMockForecast(String city) {
        return Map.of(
            "city", Map.of("name", city),
            "mock", true,
            "list", java.util.List.of(
                Map.of("dt_txt", "2024-01-01 09:00:00",
                       "main", Map.of("temp", 20.0, "humidity", 70),
                       "weather", java.util.List.of(Map.of("description", "sunny", "icon", "01d"))),
                Map.of("dt_txt", "2024-01-01 12:00:00",
                       "main", Map.of("temp", 24.0, "humidity", 60),
                       "weather", java.util.List.of(Map.of("description", "partly cloudy", "icon", "02d"))),
                Map.of("dt_txt", "2024-01-02 09:00:00",
                       "main", Map.of("temp", 19.0, "humidity", 75),
                       "weather", java.util.List.of(Map.of("description", "light rain", "icon", "10d")))
            ),
            "message", "Demo mode — configure WEATHER_API_KEY for live data"
        );
    }
}
