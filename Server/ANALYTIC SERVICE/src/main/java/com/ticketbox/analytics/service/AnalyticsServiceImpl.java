// Server/ANALYTIC SERVICE/src/main/java/com/ticketbox/analytics/service/AnalyticsServiceImpl.java
package com.ticketbox.analytics.service;

import com.ticketbox.analytics.model.response.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private LocalDate parseTimeRange(String timeRange) {
        LocalDate now = LocalDate.now();
        try {
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
        } catch (NumberFormatException e) {
            // Return default if parsing fails
        }
        return now.minusDays(30);
    }

    @Override
    public RevenueResponse getSystemRevenue(String timeRange) {
        LocalDate startDate = parseTimeRange(timeRange);

        // Get summary data
        String summarySql = "SELECT " +
                "COALESCE(SUM(t.price), 0) as total_revenue, " +
                "COUNT(t.id) as tickets_sold, " +
                "COUNT(DISTINCT o.id) as total_orders, " +
                "CASE WHEN COUNT(t.id) > 0 THEN AVG(t.price) ELSE 0 END as avg_ticket_price " +
                "FROM tickets t " +
                "JOIN orders o ON t.order_id = o.id " +
                "WHERE t.status = 'SOLD' AND DATE(t.created_at) >= ?";

        Map<String, Object> summaryResult = jdbcTemplate.queryForMap(summarySql, startDate);

        // Get chart data
        List<RevenueResponse.RevenueDataPoint> chartData = getRevenueBreakdown(timeRange, null, null);

        // Build response
        RevenueResponse response = new RevenueResponse();

        RevenueResponse.RevenueSummary summary = new RevenueResponse.RevenueSummary();
        summary.setTotalRevenue((BigDecimal) summaryResult.get("total_revenue"));
        summary.setTotalTickets(((Number) summaryResult.get("tickets_sold")).intValue());
        summary.setTotalOrders(((Number) summaryResult.get("total_orders")).intValue());
        summary.setAverageTicketPrice((BigDecimal) summaryResult.get("avg_ticket_price"));
        summary.setRevenueChange(BigDecimal.valueOf(Math.random() * 30 - 10)); // Mock change

        response.setSummary(summary);
        response.setChartData(chartData);

        return response;
    }

    @Override
    public RevenueResponse getOrganizerRevenue(String organizerId, String timeRange) {
        LocalDate startDate = parseTimeRange(timeRange);

        String summarySql = "SELECT " +
                "COALESCE(SUM(t.price), 0) as total_revenue, " +
                "COUNT(t.id) as tickets_sold, " +
                "COUNT(DISTINCT o.id) as total_orders, " +
                "CASE WHEN COUNT(t.id) > 0 THEN AVG(t.price) ELSE 0 END as avg_ticket_price " +
                "FROM tickets t " +
                "JOIN orders o ON t.order_id = o.id " +
                "JOIN events e ON t.event_id = e.id " +
                "WHERE t.status = 'SOLD' AND e.organizer_id = ? AND DATE(t.created_at) >= ?";

        Map<String, Object> summaryResult = jdbcTemplate.queryForMap(summarySql, organizerId, startDate);

        List<RevenueResponse.RevenueDataPoint> chartData = getRevenueBreakdown(timeRange, organizerId, null);

        RevenueResponse response = new RevenueResponse();

        RevenueResponse.RevenueSummary summary = new RevenueResponse.RevenueSummary();
        summary.setTotalRevenue((BigDecimal) summaryResult.get("total_revenue"));
        summary.setTotalTickets(((Number) summaryResult.get("tickets_sold")).intValue());
        summary.setTotalOrders(((Number) summaryResult.get("total_orders")).intValue());
        summary.setAverageTicketPrice((BigDecimal) summaryResult.get("avg_ticket_price"));
        summary.setRevenueChange(BigDecimal.valueOf(Math.random() * 30 - 10));

        response.setSummary(summary);
        response.setChartData(chartData);

        return response;
    }

    @Override
    public RevenueResponse getEventRevenue(String eventId, String timeRange) {
        LocalDate startDate = parseTimeRange(timeRange);

        String summarySql = "SELECT " +
                "COALESCE(SUM(t.price), 0) as total_revenue, " +
                "COUNT(t.id) as tickets_sold, " +
                "COUNT(DISTINCT o.id) as total_orders, " +
                "CASE WHEN COUNT(t.id) > 0 THEN AVG(t.price) ELSE 0 END as avg_ticket_price " +
                "FROM tickets t " +
                "JOIN orders o ON t.order_id = o.id " +
                "WHERE t.status = 'SOLD' AND t.event_id = ? AND DATE(t.created_at) >= ?";

        Map<String, Object> summaryResult = jdbcTemplate.queryForMap(summarySql, eventId, startDate);

        List<RevenueResponse.RevenueDataPoint> chartData = getRevenueBreakdown(timeRange, null, eventId);

        RevenueResponse response = new RevenueResponse();

        RevenueResponse.RevenueSummary summary = new RevenueResponse.RevenueSummary();
        summary.setTotalRevenue((BigDecimal) summaryResult.get("total_revenue"));
        summary.setTotalTickets(((Number) summaryResult.get("tickets_sold")).intValue());
        summary.setTotalOrders(((Number) summaryResult.get("total_orders")).intValue());
        summary.setAverageTicketPrice((BigDecimal) summaryResult.get("avg_ticket_price"));
        summary.setRevenueChange(BigDecimal.valueOf(Math.random() * 30 - 10));

        response.setSummary(summary);
        response.setChartData(chartData);

        return response;
    }

    private List<RevenueResponse.RevenueDataPoint> getRevenueBreakdown(String timeRange, String organizerId, String eventId) {
        LocalDate startDate = parseTimeRange(timeRange);
        List<RevenueResponse.RevenueDataPoint> breakdown = new ArrayList<>();

        String periodFormat = "DATE(t.created_at)";
        String groupBy = "DATE(t.created_at)";

        StringBuilder sql = new StringBuilder("SELECT ");
        sql.append(periodFormat).append(" as period, ");
        sql.append("COALESCE(SUM(t.price), 0) as revenue, ");
        sql.append("COUNT(t.id) as tickets_sold, ");
        sql.append("COUNT(DISTINCT o.id) as orders ");
        sql.append("FROM tickets t ");
        sql.append("JOIN orders o ON t.order_id = o.id ");

        List<Object> params = new ArrayList<>();

        if (organizerId != null) {
            sql.append("JOIN events e ON t.event_id = e.id ");
            sql.append("WHERE t.status = 'SOLD' AND e.organizer_id = ? AND DATE(t.created_at) >= ? ");
            params.add(organizerId);
            params.add(startDate);
        } else if (eventId != null) {
            sql.append("WHERE t.status = 'SOLD' AND t.event_id = ? AND DATE(t.created_at) >= ? ");
            params.add(eventId);
            params.add(startDate);
        } else {
            sql.append("WHERE t.status = 'SOLD' AND DATE(t.created_at) >= ? ");
            params.add(startDate);
        }

        sql.append("GROUP BY ").append(groupBy).append(" ORDER BY period");

        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql.toString(), params.toArray());

        for (Map<String, Object> row : results) {
            RevenueResponse.RevenueDataPoint dataPoint = new RevenueResponse.RevenueDataPoint();

            // Convert period to LocalDateTime
            Object periodObj = row.get("period");
            if (periodObj instanceof java.sql.Date) {
                dataPoint.setDate(((java.sql.Date) periodObj).toLocalDate().atStartOfDay());
            } else if (periodObj instanceof String) {
                dataPoint.setDate(LocalDate.parse((String) periodObj).atStartOfDay());
            }

            dataPoint.setRevenue((BigDecimal) row.get("revenue"));
            dataPoint.setTicketsSold(((Number) row.get("tickets_sold")).intValue());
            dataPoint.setOrderCount(((Number) row.get("orders")).intValue());
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
                "COALESCE(SUM(t.price), 0) as revenue, " +
                "CASE WHEN COUNT(t.id) > 0 THEN (COUNT(t.id) * 100.0 / e.capacity) ELSE 0 END as sell_through_rate " +
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
            event.setRevenue((BigDecimal) row.get("revenue"));
            event.setTicketsSold(((Number) row.get("tickets_sold")).intValue());
            event.setTotalCapacity(((Number) row.get("capacity")).intValue());

            Object sellThroughObj = row.get("sell_through_rate");
            if (sellThroughObj instanceof BigDecimal) {
                event.setSellThroughRate((BigDecimal) sellThroughObj);
            } else if (sellThroughObj instanceof Double) {
                event.setSellThroughRate(BigDecimal.valueOf((Double) sellThroughObj));
            }

            event.setOrganizerName((String) row.get("organizer_name"));
            events.add(event);
        }

        TopEventsResponse response = new TopEventsResponse();
        response.setEvents(events);
        return response;
    }

    @Override
    public TopEventsResponse getOrganizerTopEvents(String organizerId, int limit) {
        String sql = "SELECT " +
                "e.id as event_id, " +
                "e.name as event_name, " +
                "e.capacity, " +
                "COUNT(t.id) as tickets_sold, " +
                "COALESCE(SUM(t.price), 0) as revenue, " +
                "CASE WHEN COUNT(t.id) > 0 THEN (COUNT(t.id) * 100.0 / e.capacity) ELSE 0 END as sell_through_rate " +
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
            event.setRevenue((BigDecimal) row.get("revenue"));
            event.setTicketsSold(((Number) row.get("tickets_sold")).intValue());
            event.setTotalCapacity(((Number) row.get("capacity")).intValue());

            Object sellThroughObj = row.get("sell_through_rate");
            if (sellThroughObj instanceof BigDecimal) {
                event.setSellThroughRate((BigDecimal) sellThroughObj);
            } else if (sellThroughObj instanceof Double) {
                event.setSellThroughRate(BigDecimal.valueOf((Double) sellThroughObj));
            }

            events.add(event);
        }

        TopEventsResponse response = new TopEventsResponse();
        response.setEvents(events);
        return response;
    }

    @Override
    public TopEventsResponse getEventTicketComparison(String eventId) {
        String sql = "SELECT " +
                "tt.name as ticket_type, " +
                "COUNT(t.id) as tickets_sold, " +
                "COALESCE(SUM(t.price), 0) as revenue " +
                "FROM tickets t " +
                "JOIN ticket_types tt ON t.ticket_type_id = tt.id " +
                "WHERE t.event_id = ? AND t.status = 'SOLD' " +
                "GROUP BY tt.id, tt.name " +
                "ORDER BY tickets_sold DESC";

        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, eventId);
        List<TopEventsResponse.TopEvent> events = new ArrayList<>();

        for (Map<String, Object> row : results) {
            TopEventsResponse.TopEvent event = new TopEventsResponse.TopEvent();
            event.setEventId(eventId);
            event.setTicketType((String) row.get("ticket_type"));
            event.setTicketsSold(((Number) row.get("tickets_sold")).intValue());
            event.setRevenue((BigDecimal) row.get("revenue"));
            events.add(event);
        }

        TopEventsResponse response = new TopEventsResponse();
        response.setEvents(events);
        return response;
    }

    @Override
    public KPIResponse getSystemKPI(String timeRange) {
        LocalDate startDate = parseTimeRange(timeRange);

        String sql = "SELECT " +
                "COALESCE(SUM(t.price), 0) as total_revenue, " +
                "COUNT(t.id) as tickets_sold, " +
                "COUNT(DISTINCT e.id) as active_events, " +
                "CASE WHEN COUNT(DISTINCT o.id) > 0 THEN SUM(t.price) / COUNT(DISTINCT o.id) ELSE 0 END as avg_order_value " +
                "FROM tickets t " +
                "JOIN orders o ON t.order_id = o.id " +
                "JOIN events e ON t.event_id = e.id " +
                "WHERE t.status = 'SOLD' AND DATE(t.created_at) >= ?";

        Map<String, Object> result = jdbcTemplate.queryForMap(sql, startDate);

        KPIResponse response = new KPIResponse();
        response.setTotalRevenue((BigDecimal) result.get("total_revenue"));
        response.setTicketsSold(((Number) result.get("tickets_sold")).intValue());
        response.setActiveEvents(((Number) result.get("active_events")).intValue());
        response.setAvgOrderValue((BigDecimal) result.get("avg_order_value"));
        response.setRevenueChange(BigDecimal.valueOf(Math.random() * 50 - 10));
        response.setTicketsSoldChange(BigDecimal.valueOf(Math.random() * 40 - 10));

        return response;
    }

    @Override
    public KPIResponse getOrganizerKPI(String organizerId, String timeRange) {
        LocalDate startDate = parseTimeRange(timeRange);

        String sql = "SELECT " +
                "COALESCE(SUM(t.price), 0) as total_revenue, " +
                "COUNT(t.id) as tickets_sold, " +
                "COUNT(DISTINCT e.id) as active_events, " +
                "CASE WHEN COUNT(DISTINCT o.id) > 0 THEN SUM(t.price) / COUNT(DISTINCT o.id) ELSE 0 END as avg_order_value " +
                "FROM tickets t " +
                "JOIN orders o ON t.order_id = o.id " +
                "JOIN events e ON t.event_id = e.id " +
                "WHERE t.status = 'SOLD' AND e.organizer_id = ? AND DATE(t.created_at) >= ?";

        Map<String, Object> result = jdbcTemplate.queryForMap(sql, organizerId, startDate);

        KPIResponse response = new KPIResponse();
        response.setTotalRevenue((BigDecimal) result.get("total_revenue"));
        response.setTicketsSold(((Number) result.get("tickets_sold")).intValue());
        response.setActiveEvents(((Number) result.get("active_events")).intValue());
        response.setAvgOrderValue((BigDecimal) result.get("avg_order_value"));
        response.setRevenueChange(BigDecimal.valueOf(Math.random() * 50 - 10));
        response.setTicketsSoldChange(BigDecimal.valueOf(Math.random() * 40 - 10));

        return response;
    }
}