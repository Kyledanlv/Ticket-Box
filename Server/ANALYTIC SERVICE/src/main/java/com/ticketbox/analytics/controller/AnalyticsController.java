package com.ticketbox.analytics.controller;

import com.ticketbox.analytics.model.response.KPIResponse;
import com.ticketbox.analytics.model.response.RevenueResponse;
import com.ticketbox.analytics.model.response.TopEventsResponse;
import com.ticketbox.analytics.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analytics")
@Tag(name = "Analytics API", description = "📊 Ticket sales analytics and reporting endpoints")
public class AnalyticsController {

    private static final Logger log = LoggerFactory.getLogger(AnalyticsController.class);
    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Check if the analytics service is running")
    @ApiResponse(responseCode = "200", description = "Service is healthy")
    public ResponseEntity<String> healthCheck() {
        log.info("Health check endpoint called");
        return ResponseEntity.ok("Analytics Service is running");
    }

    @GetMapping("/revenue/system")
    @Operation(summary = "Get system revenue",
            description = "Get revenue analytics for the entire system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Revenue data retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    })
    public ResponseEntity<RevenueResponse> getSystemRevenue(
            @Parameter(description = "Time range for analytics (e.g., 7d, 30d, 90d, 365d)",
                    example = "30d",
                    required = false)
            @RequestParam(defaultValue = "30d") String timeRange) {
        log.info("Fetching system revenue with timeRange: {}", timeRange);
        return ResponseEntity.ok(analyticsService.getSystemRevenue(timeRange));
    }

    @GetMapping("/revenue/organizer/{organizerId}")
    @Operation(summary = "Get organizer revenue",
            description = "Get revenue analytics for a specific organizer")
    public ResponseEntity<RevenueResponse> getOrganizerRevenue(
            @Parameter(description = "Organizer ID", example = "ORG001", required = true)
            @PathVariable String organizerId,

            @Parameter(description = "Time range for analytics", example = "30d", required = false)
            @RequestParam(defaultValue = "30d") String timeRange) {
        log.info("Fetching organizer revenue for: {} with timeRange: {}", organizerId, timeRange);
        return ResponseEntity.ok(analyticsService.getOrganizerRevenue(organizerId, timeRange));
    }

    @GetMapping("/revenue/event/{eventId}")
    @Operation(summary = "Get event revenue",
            description = "Get revenue analytics for a specific event")
    public ResponseEntity<RevenueResponse> getEventRevenue(
            @Parameter(description = "Event ID", example = "EV1001", required = true)
            @PathVariable String eventId,

            @Parameter(description = "Time range for analytics", example = "30d", required = false)
            @RequestParam(defaultValue = "30d") String timeRange) {
        log.info("Fetching event revenue for: {} with timeRange: {}", eventId, timeRange);
        return ResponseEntity.ok(analyticsService.getEventRevenue(eventId, timeRange));
    }

    @GetMapping("/events/top")
    @Operation(summary = "Get top events",
            description = "Get ranking of top events by revenue")
    public ResponseEntity<TopEventsResponse> getTopEvents(
            @Parameter(description = "Number of top events to return", example = "10", required = false)
            @RequestParam(defaultValue = "10") int limit) {
        log.info("Fetching top {} events system-wide", limit);
        return ResponseEntity.ok(analyticsService.getTopEvents(limit));
    }

    @GetMapping("/events/organizer/{organizerId}")
    @Operation(summary = "Get organizer's top events",
            description = "Get top events for a specific organizer")
    public ResponseEntity<TopEventsResponse> getOrganizerTopEvents(
            @Parameter(description = "Organizer ID", example = "ORG001", required = true)
            @PathVariable String organizerId,

            @Parameter(description = "Number of top events to return", example = "10", required = false)
            @RequestParam(defaultValue = "10") int limit) {
        log.info("Fetching top {} events for organizer: {}", limit, organizerId);
        return ResponseEntity.ok(analyticsService.getOrganizerTopEvents(organizerId, limit));
    }

    @GetMapping("/kpi/system")
    @Operation(summary = "Get system KPI",
            description = "Get key performance indicators for the entire system")
    public ResponseEntity<KPIResponse> getSystemKPI(
            @Parameter(description = "Time range for analytics", example = "30d", required = false)
            @RequestParam(defaultValue = "30d") String timeRange) {
        log.info("Fetching system KPI with timeRange: {}", timeRange);
        return ResponseEntity.ok(analyticsService.getSystemKPI(timeRange));
    }

    @GetMapping("/kpi/organizer/{organizerId}")
    @Operation(summary = "Get organizer KPI",
            description = "Get key performance indicators for a specific organizer")
    public ResponseEntity<KPIResponse> getOrganizerKPI(
            @Parameter(description = "Organizer ID", example = "ORG001", required = true)
            @PathVariable String organizerId,

            @Parameter(description = "Time range for analytics", example = "30d", required = false)
            @RequestParam(defaultValue = "30d") String timeRange) {
        log.info("Fetching organizer KPI for: {} with timeRange: {}", organizerId, timeRange);
        return ResponseEntity.ok(analyticsService.getOrganizerKPI(organizerId, timeRange));
    }

    @GetMapping("/events/{eventId}/ticket-comparison")
    @Operation(summary = "Get ticket type comparison",
            description = "Compare sales across different ticket types for an event")
    public ResponseEntity<TopEventsResponse> getEventTicketComparison(
            @Parameter(description = "Event ID", example = "EV1001", required = true)
            @PathVariable String eventId) {
        log.info("Fetching ticket comparison for event: {}", eventId);
        return ResponseEntity.ok(analyticsService.getEventTicketComparison(eventId));
    }
}