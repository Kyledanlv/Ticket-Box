package com.ticketbox.analytics.service;

import com.ticketbox.analytics.model.response.RevenueResponse;
import com.ticketbox.analytics.model.response.TopEventsResponse;
import com.ticketbox.analytics.model.response.KPIResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public interface AnalyticsService {
    RevenueResponse getSystemRevenue(String timeRange);
    RevenueResponse getOrganizerRevenue(String organizerId, String timeRange);
    RevenueResponse getEventRevenue(String eventId, String timeRange);
    TopEventsResponse getTopEvents(int limit);
    TopEventsResponse getOrganizerTopEvents(String organizerId, int limit);
    KPIResponse getSystemKPI(String timeRange);
    KPIResponse getOrganizerKPI(String organizerId, String timeRange);
}