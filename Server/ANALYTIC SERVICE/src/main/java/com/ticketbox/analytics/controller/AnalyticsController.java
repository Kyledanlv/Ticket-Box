package com.ticketbox.analytics.controller;

import com.ticketbox.analytics.model.response.KPIResponse;
import com.ticketbox.analytics.model.response.RevenueResponse;
import com.ticketbox.analytics.model.response.TopEventsResponse;
import com.ticketbox.analytics.service.AnalyticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private static final Logger log = LoggerFactory.getLogger(AnalyticsController.class);
    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        log.info("Health check endpoint called");
        return ResponseEntity.ok("Analytics Service is running");
    }

    // ✅ ĐÚNG - Khớp với interface
    @GetMapping("/revenue/system")
    public ResponseEntity<RevenueResponse> getSystemRevenue(
            @RequestParam(defaultValue = "30d") String timeRange) {
        log.info("Fetching system revenue with timeRange: {}", timeRange);
        return ResponseEntity.ok(analyticsService.getSystemRevenue(timeRange));
    }

    // ✅ ĐÚNG - Khớp với interface
    @GetMapping("/revenue/organizer/{organizerId}")
    public ResponseEntity<RevenueResponse> getOrganizerRevenue(
            @PathVariable String organizerId,
            @RequestParam(defaultValue = "30d") String timeRange) {
        log.info("Fetching organizer revenue for: {} with timeRange: {}", organizerId, timeRange);
        return ResponseEntity.ok(analyticsService.getOrganizerRevenue(organizerId, timeRange));
    }

    // ✅ ĐÚNG - Khớp với interface
    @GetMapping("/revenue/event/{eventId}")
    public ResponseEntity<RevenueResponse> getEventRevenue(
            @PathVariable String eventId,
            @RequestParam(defaultValue = "30d") String timeRange) {
        log.info("Fetching event revenue for: {} with timeRange: {}", eventId, timeRange);
        return ResponseEntity.ok(analyticsService.getEventRevenue(eventId, timeRange));
    }

    // ✅ ĐÚNG - Khớp với interface (KHÔNG có timeRange)
    @GetMapping("/events/top")
    public ResponseEntity<TopEventsResponse> getTopEvents(
            @RequestParam(defaultValue = "10") int limit) {
        log.info("Fetching top {} events system-wide", limit);
        return ResponseEntity.ok(analyticsService.getTopEvents(limit));
    }

    // ✅ ĐÚNG - Khớp với interface (KHÔNG có timeRange)
    @GetMapping("/events/organizer/{organizerId}")
    public ResponseEntity<TopEventsResponse> getOrganizerTopEvents(
            @PathVariable String organizerId,
            @RequestParam(defaultValue = "10") int limit) {
        log.info("Fetching top {} events for organizer: {}", limit, organizerId);
        return ResponseEntity.ok(analyticsService.getOrganizerTopEvents(organizerId, limit));
    }

    // ✅ ĐÚNG - Khớp với interface
    @GetMapping("/kpi/system")
    public ResponseEntity<KPIResponse> getSystemKPI(
            @RequestParam(defaultValue = "30d") String timeRange) {
        log.info("Fetching system KPI with timeRange: {}", timeRange);
        return ResponseEntity.ok(analyticsService.getSystemKPI(timeRange));
    }

    // ✅ ĐÚNG - Khớp với interface
    @GetMapping("/kpi/organizer/{organizerId}")
    public ResponseEntity<KPIResponse> getOrganizerKPI(
            @PathVariable String organizerId,
            @RequestParam(defaultValue = "30d") String timeRange) {
        log.info("Fetching organizer KPI for: {} with timeRange: {}", organizerId, timeRange);
        return ResponseEntity.ok(analyticsService.getOrganizerKPI(organizerId, timeRange));
    }

    // ✅ ĐÚNG - Khớp với interface
    @GetMapping("/events/{eventId}/ticket-comparison")
    public ResponseEntity<TopEventsResponse> getEventTicketComparison(
            @PathVariable String eventId) {
        log.info("Fetching ticket comparison for event: {}", eventId);
        return ResponseEntity.ok(analyticsService.getEventTicketComparison(eventId));
    }
}