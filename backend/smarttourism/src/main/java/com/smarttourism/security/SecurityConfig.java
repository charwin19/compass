package com.smarttourism.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * Spring Security configuration.
 *
 * Public routes: auth endpoints, all GETs on destinations/weather
 * Protected routes: POST travel-plan, emergency (requires USER or ADMIN)
 * Admin routes: GET/PATCH /api/admin/** (requires ADMIN)
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Use our CORS config from WebConfig
            .cors(cors -> cors.configurationSource(corsConfigurationSource))

            // Disable CSRF — we use stateless JWT
            .csrf(csrf -> csrf.disable())

            // Stateless session — no HttpSession
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Authorisation rules
            .authorizeHttpRequests(auth -> auth
                // ---- PUBLIC ----
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()            // CORS preflight
                .requestMatchers("/api/auth/**").permitAll()                       // login/register
                .requestMatchers(HttpMethod.GET, "/api/destinations/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/destinations").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/weather/**").permitAll()

                // ---- ADMIN ONLY ----
                .requestMatchers("/api/admin/**").hasRole("ADMIN")

                // ---- AUTHENTICATED (any role) ----
                .anyRequest().authenticated()
            )

            // JWT filter runs before the standard username/password filter
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * BCrypt password encoder — strength 12 for production-grade hashing.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
