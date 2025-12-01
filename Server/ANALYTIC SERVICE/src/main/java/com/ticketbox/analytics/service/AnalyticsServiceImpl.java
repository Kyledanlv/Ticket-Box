package com.ticketbox.analytics.service.impl;

import com.ticketbox.analytics.model.response.*;
import com.ticketbox.analytics.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private LocalDate parseTimeRange(String timeRange) {
        LocalDate now = LocalDate.now();
        if (timeRange.endsWith("d")) {
            int days = Integer.parseInt(timeRange.substring(0, timeRange.length() - 1));
            return now.minusDays(days);
        } else if (timeRange.endsWith("w")) {
            int weeks = Integer.parseInt(timeRange.substring(0, timeRange.length() - 1));
            return now.minusWeeks(weeks);
        } else if (timeRange.endsWith("m")) {
            int months = Integer.parseInt(timeRange.substring(0, timeRange.length() - 1));
            return now.minusMonths(months);
        } else if (timeRange.endsWith("y")) {
            int years = Integer.parseInt(timeRange.substring(0, timeRange.length() - 1));
            return now.minusYears(years);
        }
        return now.minusDays(30);
    }

    @Override
    public RevenueResponse getSystemRevenue(String timeRange) {
        LocalDate startDate = parseTimeRange(timeRange);

        String sql = "SELECT " +
                "SUM(t.price) as total_revenue, " +
                "COUNT(t.id) as tickets_sold, " +
                "COUNT(DISTINCT o.id) as total_orders, " +
                "AVG(t.price) as avg_ticket_price, " +
                "SUM(t.price) / COUNT(DISTINCT o.id) as avg_order_value " +
                "FROM tickets t " +
                "JOIN orders o ON t.order_id = o.id " +
                "WHERE t.status = 'SOLD' AND t.created_at >= ?";

        Map<String, Object> result = jdbcTemplate.queryForMap(sql, startDate);

        RevenueResponse response = new RevenueResponse();
        response.setTimeRange(timeRange);
        response.setTotalRevenue((BigDecimal) result.get("total_revenue"));
        response.setTotalTicketsSold((Long) result.get("tickets_sold"));
        response.setTotalOrders((Long) result.get("total_orders"));
        response.setAvgTicketPrice((BigDecimal) result.get("avg_ticket_price"));
        response.setAvgOrderValue((BigDecimal) result.get("avg_order_value"));
        response.setBreakdown(getRevenueBreakdown(timeRange, null, null));

        return response;
    }

    @Override
    public RevenueResponse getOrganizerRevenue(String organizerId, String timeRange) {
        LocalDate startDate = parseTimeRange(timeRange);

        String sql = "SELECT " +
                "SUM(t.price) as total_revenue, " +
                "COUNT(t.id) as tickets_sold, " +
                "COUNT(DISTINCT o.id) as total_orders, " +
                "AVG(t.price) as avg_ticket_price, " +
                "SUM(t.price) / COUNT(DISTINCT o.id) as avg_order_value " +
                "FROM tickets t " +
                "JOIN orders o ON t.order_id = o.id " +
                "JOIN events e ON t.event_id = e.id " +
                "WHERE t.status = 'SOLD' AND e.organizer_id = ? AND t.created_at >= ?";

        Map<String, Object> result = jdbcTemplate.queryForMap(sql, organizerId, startDate);

        RevenueResponse response = new RevenueResponse();
        response.setTimeRange(timeRange);
        response.setTotalRevenue((BigDecimal) result.get("total_revenue"));
        response.setTotalTicketsSold((Long) result.get("tickets_sold"));
        response.setTotalOrders((Long) result.get("total_orders"));
        response.setAvgTicketPrice((BigDecimal) result.get("avg_ticket_price"));
        response.setAvgOrderValue((BigDecimal) result.get("avg_order_value"));
        response.setBreakdown(getRevenueBreakdown(timeRange, organizerId, null));

        return response;
    }

    @Override
    public RevenueResponse getEventRevenue(String eventId, String timeRange) {
        LocalDate startDate = parseTimeRange(timeRange);

        String sql = "SELECT " +
                "SUM(t.price) as total_revenue, " +
                "COUNT(t.id) as tickets_sold, " +
                "COUNT(DISTINCT o.id) as total_orders, " +
                "AVG(t.price) as avg_ticket_price, " +
                "SUM(t.price) / COUNT(DISTINCT o.id) as avg_order_value " +
                "FROM tickets t " +
                "JOIN orders o ON t.order_id = o.id " +
                "WHERE t.status = 'SOLD' AND t.event_id = ? AND t.created_at >= ?";

        Map<String, Object> result = jdbcTemplate.queryForMap(sql, eventId, startDate);

        RevenueResponse response = new RevenueResponse();
        response.setTimeRange(timeRange);
        response.setTotalRevenue((BigDecimal) result.get("total_revenue"));
        response.setTotalTicketsSold((Long) result.get("tickets_sold"));
        response.setTotalOrders((Long) result.get("total_orders"));
        response.setAvgTicketPrice((BigDecimal) result.get("avg_ticket_price"));
        response.setAvgOrderValue((BigDecimal) result.get("avg_order_value"));
        response.setBreakdown(getRevenueBreakdown(timeRange, null, eventId));

        return response;
    }

    private List<RevenueResponse.RevenueDataPoint> getRevenueBreakdown(String timeRange, String organizerId, String eventId) {
        List<RevenueResponse.RevenueDataPoint> breakdown = new ArrayList<>();

        String periodFormat = "DATE(t.created_at)";
        String groupBy = "DATE(t.created_at)";

        if (timeRange.endsWith("m") || timeRange.endsWith("y")) {
            periodFormat = "DATE_FORMAT(t.created_at, '%Y-%m')";
            groupBy = "DATE_FORMAT(t.created_at, '%Y-%m')";
        }

        StringBuilder sql = new StringBuilder("SELECT ");
        sql.append(periodFormat).append(" as period, ");
        sql.append("SUM(t.price) as revenue, ");
        sql.append("COUNT(t.id) as tickets_sold, ");
        sql.append("COUNT(DISTINCT o.id) as orders ");
        sql.append("FROM tickets t ");
        sql.append("JOIN orders o ON t.order_id = o.id ");
        sql.append("WHERE t.status = 'SOLD' ");

        List<Object> params = new ArrayList<>();

        if (organizerId != null) {
            sql.append("AND EXISTS (SELECT 1 FROM events e WHERE e.id = t.event_id AND e.organizer_id = ?) ");
            params.add(organizerId);
        }

        if (eventId != null) {
            sql.append("AND t.event_id = ? ");
            params.add(eventId);
        }

        sql.append("GROUP BY ").append(groupBy).append(" ORDER BY period");

        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql.toString(), params.toArray());

        for (Map<String, Object> row : results) {
            RevenueResponse.RevenueDataPoint dataPoint = new RevenueResponse.RevenueDataPoint();
            dataPoint.setPeriod(row.get("period").toString());
            dataPoint.setRevenue((BigDecimal) row.get("revenue"));
            dataPoint.setTicketsSold((Long) row.get("tickets_sold"));
            dataPoint.setOrders((Long) row.get("orders"));
            breakdown.add(dataPoint);
        }

        return breakdown;
    }

    @Override
    public TopEventsResponse getTopEvents(int limit) {
        String sql = "SELECT " +
                "e.id as event_id, " +
                "e.name as event_name, " +
                "e.organizer_id, " +
                "u.name as organizer_name, " +
                "e.capacity, " +
                "COUNT(t.id) as tickets_sold, " +
                "SUM(t.price) as revenue, " +
                "AVG(t.price) as avg_ticket_price, " +
                "(COUNT(t.id) * 100.0 / e.capacity) as sell_through_rate " +
                "FROM events e " +
                "LEFT JOIN tickets t ON e.id = t.event_id AND t.status = 'SOLD' " +
                "LEFT JOIN users u ON e.organizer_id = u.id " +
                "GROUP BY e.id, e.name, e.organizer_id, u.name, e.capacity " +
                "ORDER BY revenue DESC " +
                "LIMIT ?";

        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, limit);
        List<TopEventsResponse.TopEvent> events = new ArrayList<>();

        for (Map<String, Object> row : results) {
            TopEventsResponse.TopEvent event = new TopEventsResponse.TopEvent();
            event.setEventId(row.get("event_id").toString());
            event.setEventName((String) row.get("event_name"));
            event.setOrganizerId((String) row.get("organizer_id"));
            event.setOrganizerName((String) row.get("organizer_name"));
            event.setCapacity((Integer) row.get("capacity"));
            event.setTicketsSold((Long) row.get("tickets_sold"));
            event.setRevenue((BigDecimal) row.get("revenue"));
            event.setAvgTicketPrice((BigDecimal) row.get("avg_ticket_price"));
            event.setSellThroughRate((BigDecimal) row.get("sell_through_rate"));
            events.add(event);
        }

        TopEventsResponse response = new TopEventsResponse();
        response.setEvents(events);
        response.setTimeRange("all-time");
        return response;
    }

    @Override
    public TopEventsResponse getOrganizerTopEvents(String organizerId, int limit) {
        String sql = "SELECT " +
                "e.id as event_id, " +
                "e.name as event_name, " +
                "e.capacity, " +
                "COUNT(t.id) as tickets_sold, " +
                "SUM(t.price) as revenue, " +
                "AVG(t.price) as avg_ticket_price, " +
                "(COUNT(t.id) * 100.0 / e.capacity) as sell_through_rate " +
                "FROM events e " +
                "LEFT JOIN tickets t ON e.id = t.event_id AND t.status = 'SOLD' " +
                "WHERE e.organizer_id = ? " +
                "GROUP BY e.id, e.name, e.capacity " +
                "ORDER BY revenue DESC " +
                "LIMIT ?";

        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, organizerId, limit);
        List<TopEventsResponse.TopEvent> events = new ArrayList<>();

        for (Map<String, Object> row : results) {
            TopEventsResponse.TopEvent event = new TopEventsResponse.TopEvent();
            event.setEventId(row.get("event_id").toString());
            event.setEventName((String) row.get("event_name"));
            event.setOrganizerId(organizerId);
            event.setCapacity((Integer) row.get("capacity"));
            event.setTicketsSold((Long) row.get("tickets_sold"));
            event.setRevenue((BigDecimal) row.get("revenue"));
            event.setAvgTicketPrice((BigDecimal) row.get("avg_ticket_price"));
            event.setSellThroughRate((BigDecimal) row.get("sell_through_rate"));
            events.add(event);
        }

        TopEventsResponse response = new TopEventsResponse();
        response.setEvents(events);
        response.setTimeRange("all-time");
        return response;
    }

    @Override
    public TopEventsResponse getEventTicketComparison(String eventId) {
        String sql = "SELECT " +
                "tt.name as ticket_type, " +
                "COUNT(t.id) as tickets_sold, " +
                "SUM(t.price) as revenue, " +
                "AVG(t.price) as avg_price, " +
                "tt.price as base_price, " +
                "COUNT(t.id) * 100.0 / SUM(COUNT(t.id)) OVER() as percentage " +
                "FROM tickets t " +
                "JOIN ticket_types tt ON t.ticket_type_id = tt.id " +
                "WHERE t.event_id = ? AND t.status = 'SOLD' " +
                "GROUP BY tt.id, tt.name, tt.price " +
                "ORDER BY tickets_sold DESC";

        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, eventId);
        List<TopEventsResponse.TopEvent> events = new ArrayList<>();

        for (Map<String, Object> row : results) {
            TopEventsResponse.TopEvent event = new TopEventsResponse.TopEvent();
            event.setEventId(eventId);
            event.setEventName((String) row.get("ticket_type") + " Tickets");
            event.setTicketsSold((Long) row.get("tickets_sold"));
            event.setRevenue((BigDecimal) row.get("revenue"));
            event.setAvgTicketPrice((BigDecimal) row.get("avg_price"));
            events.add(event);
        }

        TopEventsResponse response = new TopEventsResponse();
        response.setEvents(events);
        response.setTimeRange("all-time");
        return response;
    }

    @Override
    public KPIResponse getSystemKPI(String timeRange) {
        LocalDate startDate = parseTimeRange(timeRange);

        String eventsSql = "SELECT COUNT(*) as total_events FROM events WHERE created_at >= ?";
        String ticketsSql = "SELECT " +
                "COUNT(t.id) as total_tickets_sold, " +
                "SUM(t.price) as total_revenue, " +
                "AVG(t.price) as avg_ticket_price, " +
                "COUNT(DISTINCT o.id) as total_orders, " +
                "SUM(t.price) / COUNT(DISTINCT o.id) as avg_order_value " +
                "FROM tickets t " +
                "JOIN orders o ON t.order_id = o.id " +
                "WHERE t.status = 'SOLD' AND t.created_at >= ?";
        String organizersSql = "SELECT COUNT(DISTINCT organizer_id) as active_organizers FROM events WHERE created_at >= ?";

        Long totalEvents = jdbcTemplate.queryForObject(eventsSql, Long.class, startDate);
        Map<String, Object> ticketStats = jdbcTemplate.queryForMap(ticketsSql, startDate);
        Long activeOrganizers = jdbcTemplate.queryForObject(organizersSql, Long.class, startDate);

        String sellThroughSql = "SELECT " +
                "COALESCE(SUM(tickets_sold), 0) * 100.0 / NULLIF(SUM(capacity), 0) as sell_through_rate " +
                "FROM ( " +
                "SELECT e.id, e.capacity, COUNT(t.id) as tickets_sold " +
                "FROM events e " +
                "LEFT JOIN tickets t ON e.id = t.event_id AND t.status = 'SOLD' AND t.created_at >= ? " +
                "WHERE e.created_at >= ? " +
                "GROUP BY e.id, e.capacity " +
                ") event_stats";

        BigDecimal sellThroughRate = jdbcTemplate.queryForObject(sellThroughSql, BigDecimal.class, startDate, startDate);

        KPIResponse response = new KPIResponse();
        response.setTimeRange(timeRange);
        response.setTotalEvents(totalEvents);
        response.setTotalTicketsSold((Long) ticketStats.get("total_tickets_sold"));
        response.setTotalRevenue((BigDecimal) ticketStats.get("total_revenue"));
        response.setAvgTicketPrice((BigDecimal) ticketStats.get("avg_ticket_price"));
        response.setSellThroughRate(sellThroughRate != null ? sellThroughRate : BigDecimal.ZERO);
        response.setActiveOrganizers(activeOrganizers);
        response.setTotalOrders((Long) ticketStats.get("total_orders"));
        response.setAvgOrderValue((BigDecimal) ticketStats.get("avg_order_value"));

        return response;
    }

    @Override
    public KPIResponse getOrganizerKPI(String organizerId, String timeRange) {
        LocalDate startDate = parseTimeRange(timeRange);

        String eventsSql = "SELECT COUNT(*) as total_events FROM events WHERE organizer_id = ? AND created_at >= ?";
        String ticketsSql = "SELECT " +
                "COUNT(t.id) as total_tickets_sold, " +
                "SUM(t.price) as total_revenue, " +
                "AVG(t.price) as avg_ticket_price, " +
                "COUNT(DISTINCT o.id) as total_orders, " +
                "SUM(t.price) / COUNT(DISTINCT o.id) as avg_order_value " +
                "FROM tickets t " +
                "JOIN orders o ON t.order_id = o.id " +
                "JOIN events e ON t.event_id = e.id " +
                "WHERE t.status = 'SOLD' AND e.organizer_id = ? AND t.created_at >= ?";

        Long totalEvents = jdbcTemplate.queryForObject(eventsSql, Long.class, organizerId, startDate);
        Map<String, Object> ticketStats = jdbcTemplate.queryForMap(ticketsSql, organizerId, startDate);

        String sellThroughSql = "SELECT " +
                "COALESCE(SUM(tickets_sold), 0) * 100.0 / NULLIF(SUM(capacity), 0) as sell_through_rate " +
                "FROM ( " +
                "SELECT e.id, e.capacity, COUNT(t.id) as tickets_sold " +
                "FROM events e " +
                "LEFT JOIN tickets t ON e.id = t.event_id AND t.status = 'SOLD' AND t.created_at >= ? " +
                "WHERE e.organizer_id = ? AND e.created_at >= ? " +
                "GROUP BY e.id, e.capacity " +
                ") event_stats";

        BigDecimal sellThroughRate = jdbcTemplate.queryForObject(sellThroughSql, BigDecimal.class,
                startDate, organizerId, startDate);

        KPIResponse response = new KPIResponse();
        response.setTimeRange(timeRange);
        response.setTotalEvents(totalEvents);
        response.setTotalTicketsSold((Long) ticketStats.get("total_tickets_sold"));
        response.setTotalRevenue((BigDecimal) ticketStats.get("total_revenue"));
        response.setAvgTicketPrice((BigDecimal) ticketStats.get("avg_ticket_price"));
        response.setSellThroughRate(sellThroughRate != null ? sellThroughRate : BigDecimal.ZERO);
        response.setActiveOrganizers(1L);
        response.setTotalOrders((Long) ticketStats.get("total_orders"));
        response.setAvgOrderValue((BigDecimal) ticketStats.get("avg_order_value"));

        return response;
    }
}