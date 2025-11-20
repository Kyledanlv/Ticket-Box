package com.example.ticketboxanalytics.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class AnalyticsSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Public health check
                        .requestMatchers("/api/analytics/health").permitAll()
                        // Admin-only endpoints
                        .requestMatchers("/api/analytics/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/analytics/revenue").hasRole("ADMIN")
                        .requestMatchers("/api/analytics/top-events").hasRole("ADMIN")
                        // Organizer endpoints - chỉ xem data của họ
                        .requestMatchers("/api/analytics/organizer/**").hasAnyRole("ADMIN", "ORGANIZER")
                        .anyRequest().authenticated()
                )
                // Giả định sử dụng JWT từ Auth Server khác
                .oauth2ResourceServer(oauth2 -> oauth2.jwt());

        return http.build();
    }
}