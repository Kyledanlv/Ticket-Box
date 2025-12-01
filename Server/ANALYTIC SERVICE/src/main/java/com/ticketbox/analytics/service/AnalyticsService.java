package com.ticketbox.analytics.service;

import com.ticketbox.analytics.model.response.KPIResponse;
import com.ticketbox.analytics.model.response.RevenueResponse;
import com.ticketbox.analytics.model.response.TopEventsResponse;

public interface AnalyticsService {
    // Revenue APIs
    RevenueResponse getSystemRevenue(String timeRange);
    RevenueResponse getOrganizerRevenue(String organizerId, String timeRange);
    RevenueResponse getEventRevenue(String eventId, String timeRange);

    // Events APIs
    TopEventsResponse getTopEvents(int limit);
    TopEventsResponse getOrganizerTopEvents(String organizerId, int limit);
    TopEventsResponse getEventTicketComparison(String eventId);

    // KPI APIs
    KPIResponse getSystemKPI(String timeRange);
    KPIResponse getOrganizerKPI(String organizerId, String timeRange);
}