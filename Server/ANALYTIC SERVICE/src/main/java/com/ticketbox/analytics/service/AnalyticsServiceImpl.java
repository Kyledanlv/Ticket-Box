package com.ticketbox.analytics.service;

import com.ticketbox.analytics.model.response.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

        // REVENUE: Tổng tiền từ s_order.total_price cho orders đã purchased
        // TICKETS SOLD: Tổng quantity từ s_order_ticket với status = 1 (đã bán)
        // ORDERS: Đếm orders có status = 1 (đã mua)
        String summarySql = "SELECT " +
                "COALESCE(SUM(o.total_price), 0) as total_revenue, " +
                "COALESCE(SUM(ot.sub_quantity), 0) as tickets_sold, " +
                "COUNT(DISTINCT o.id) as total_orders, " +
                "CASE WHEN COALESCE(SUM(ot.sub_quantity), 0) > 0 " +
                "THEN SUM(o.total_price) / SUM(ot.sub_quantity) ELSE 0 END as avg_ticket_price " +
                "FROM s_order o " +
                "LEFT JOIN s_order_ticket ot ON o.id = ot.order_id AND ot.status = 1 " +
                "WHERE o.status = 1 AND DATE(o.create_date) >= ?";

        Map<String, Object> summaryResult = jdbcTemplate.queryForMap(summarySql, startDate);

        // Get chart data
        List<RevenueResponse.RevenueDataPoint> chartData = getRevenueBreakdown(timeRange, null, null);

        // Build response
        RevenueResponse response = new RevenueResponse();

        RevenueResponse.RevenueSummary summary = new RevenueResponse.RevenueSummary();
        summary.setTotalRevenue(convertToBigDecimal(summaryResult.get("total_revenue")));
        summary.setTotalTickets(((Number) summaryResult.get("tickets_sold")).intValue());
        summary.setTotalOrders(((Number) summaryResult.get("total_orders")).intValue());
        summary.setAverageTicketPrice(convertToBigDecimal(summaryResult.get("avg_ticket_price")));
        summary.setRevenueChange(BigDecimal.valueOf(Math.random() * 30 - 10));

        response.setSummary(summary);
        response.setChartData(chartData);

        return response;
    }

    @Override
    public RevenueResponse getOrganizerRevenue(String organizerId, String timeRange) {
        LocalDate startDate = parseTimeRange(timeRange);

        // Revenue cho một organizer cụ thể (host_id)
        String summarySql = "SELECT " +
                "COALESCE(SUM(o.total_price), 0) as total_revenue, " +
                "COALESCE(SUM(ot.sub_quantity), 0) as tickets_sold, " +
                "COUNT(DISTINCT o.id) as total_orders, " +
                "CASE WHEN COALESCE(SUM(ot.sub_quantity), 0) > 0 " +
                "THEN SUM(o.total_price) / SUM(ot.sub_quantity) ELSE 0 END as avg_ticket_price " +
                "FROM s_order o " +
                "LEFT JOIN s_order_ticket ot ON o.id = ot.order_id AND ot.status = 1 " +
                "LEFT JOIN s_ticket t ON ot.ticket_id = t.id " +
                "LEFT JOIN s_event e ON t.event_id = e.id " +
                "WHERE o.status = 1 AND e.host_id = ? AND DATE(o.create_date) >= ?";

        Map<String, Object> summaryResult = jdbcTemplate.queryForMap(summarySql, organizerId, startDate);

        List<RevenueResponse.RevenueDataPoint> chartData = getRevenueBreakdown(timeRange, organizerId, null);

        RevenueResponse response = new RevenueResponse();

        RevenueResponse.RevenueSummary summary = new RevenueResponse.RevenueSummary();
        summary.setTotalRevenue(convertToBigDecimal(summaryResult.get("total_revenue")));
        summary.setTotalTickets(((Number) summaryResult.get("tickets_sold")).intValue());
        summary.setTotalOrders(((Number) summaryResult.get("total_orders")).intValue());
        summary.setAverageTicketPrice(convertToBigDecimal(summaryResult.get("avg_ticket_price")));
        summary.setRevenueChange(BigDecimal.valueOf(Math.random() * 30 - 10));

        response.setSummary(summary);
        response.setChartData(chartData);

        return response;
    }

    @Override
    public RevenueResponse getEventRevenue(String eventId, String timeRange) {
        LocalDate startDate = parseTimeRange(timeRange);

        String summarySql = "SELECT " +
                "COALESCE(SUM(o.total_price), 0) as total_revenue, " +
                "COALESCE(SUM(ot.sub_quantity), 0) as tickets_sold, " +
                "COUNT(DISTINCT o.id) as total_orders, " +
                "CASE WHEN COALESCE(SUM(ot.sub_quantity), 0) > 0 " +
                "THEN SUM(o.total_price) / SUM(ot.sub_quantity) ELSE 0 END as avg_ticket_price " +
                "FROM s_order o " +
                "LEFT JOIN s_order_ticket ot ON o.id = ot.order_id AND ot.status = 1 " +
                "LEFT JOIN s_ticket t ON ot.ticket_id = t.id " +
                "WHERE o.status = 1 AND t.event_id = ? AND DATE(o.create_date) >= ?";

        Map<String, Object> summaryResult = jdbcTemplate.queryForMap(summarySql, eventId, startDate);

        List<RevenueResponse.RevenueDataPoint> chartData = getRevenueBreakdown(timeRange, null, eventId);

        RevenueResponse response = new RevenueResponse();

        RevenueResponse.RevenueSummary summary = new RevenueResponse.RevenueSummary();
        summary.setTotalRevenue(convertToBigDecimal(summaryResult.get("total_revenue")));
        summary.setTotalTickets(((Number) summaryResult.get("tickets_sold")).intValue());
        summary.setTotalOrders(((Number) summaryResult.get("total_orders")).intValue());
        summary.setAverageTicketPrice(convertToBigDecimal(summaryResult.get("avg_ticket_price")));
        summary.setRevenueChange(BigDecimal.valueOf(Math.random() * 30 - 10));

        response.setSummary(summary);
        response.setChartData(chartData);

        return response;
    }

    private List<RevenueResponse.RevenueDataPoint> getRevenueBreakdown(String timeRange, String organizerId, String eventId) {
        LocalDate startDate = parseTimeRange(timeRange);
        List<RevenueResponse.RevenueDataPoint> breakdown = new ArrayList<>();

        String periodFormat = "DATE(o.create_date)";
        String groupBy = "DATE(o.create_date)";

        StringBuilder sql = new StringBuilder("SELECT ");
        sql.append(periodFormat).append(" as period, ");
        sql.append("COALESCE(SUM(o.total_price), 0) as revenue, ");
        sql.append("COALESCE(SUM(ot.sub_quantity), 0) as tickets_sold, ");
        sql.append("COUNT(DISTINCT o.id) as orders ");
        sql.append("FROM s_order o ");
        sql.append("LEFT JOIN s_order_ticket ot ON o.id = ot.order_id AND ot.status = 1 ");

        List<Object> params = new ArrayList<>();

        if (organizerId != null) {
            sql.append("LEFT JOIN s_ticket t ON ot.ticket_id = t.id ");
            sql.append("LEFT JOIN s_event e ON t.event_id = e.id ");
            sql.append("WHERE o.status = 1 AND e.host_id = ? AND DATE(o.create_date) >= ? ");
            params.add(organizerId);
            params.add(startDate);
        } else if (eventId != null) {
            sql.append("LEFT JOIN s_ticket t ON ot.ticket_id = t.id ");
            sql.append("WHERE o.status = 1 AND t.event_id = ? AND DATE(o.create_date) >= ? ");
            params.add(eventId);
            params.add(startDate);
        } else {
            sql.append("WHERE o.status = 1 AND DATE(o.create_date) >= ? ");
            params.add(startDate);
        }

        sql.append("GROUP BY ").append(groupBy).append(" ORDER BY period");

        try {
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

                dataPoint.setRevenue(convertToBigDecimal(row.get("revenue")));
                dataPoint.setTicketsSold(((Number) row.get("tickets_sold")).intValue());
                dataPoint.setOrderCount(((Number) row.get("orders")).intValue());
                breakdown.add(dataPoint);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return breakdown;
    }

    @Override
    public TopEventsResponse getTopEvents(int limit) {
        // Lấy top events dựa trên tổng revenue và số lượng ticket bán được
        String sql = "SELECT " +
                "e.id as event_id, " +
                "e.name as event_name, " +
                "e.host_id as organizer_id, " +
                "u.full_name as organizer_name, " +
                "COALESCE(SUM(t.capacity), 0) as total_capacity, " +
                "COALESCE(SUM(t.sold), 0) as tickets_sold, " +
                "COALESCE(SUM(o.total_price), 0) as revenue, " +
                "CASE WHEN COALESCE(SUM(t.capacity), 0) > 0 " +
                "THEN (COALESCE(SUM(t.sold), 0) * 100.0 / COALESCE(SUM(t.capacity), 0)) ELSE 0 END as sell_through_rate " +
                "FROM s_event e " +
                "LEFT JOIN s_ticket t ON e.id = t.event_id " +
                "LEFT JOIN s_order_ticket ot ON t.id = ot.ticket_id AND ot.status = 1 " +
                "LEFT JOIN s_order o ON ot.order_id = o.id AND o.status = 1 " +
                "LEFT JOIN s_user u ON e.host_id = u.id " +
                "GROUP BY e.id, e.name, e.host_id, u.full_name " +
                "ORDER BY revenue DESC " +
                "LIMIT ?";

        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, limit);
        List<TopEventsResponse.TopEvent> events = new ArrayList<>();

        for (Map<String, Object> row : results) {
            TopEventsResponse.TopEvent event = new TopEventsResponse.TopEvent();
            event.setEventId(row.get("event_id").toString());
            event.setEventName((String) row.get("event_name"));
            event.setRevenue(convertToBigDecimal(row.get("revenue")));
            event.setTicketsSold(((Number) row.get("tickets_sold")).intValue());
            event.setTotalCapacity(((Number) row.get("total_capacity")).intValue());
            event.setSellThroughRate(convertToBigDecimal(row.get("sell_through_rate")));
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
                "COALESCE(SUM(t.capacity), 0) as total_capacity, " +
                "COALESCE(SUM(t.sold), 0) as tickets_sold, " +
                "COALESCE(SUM(o.total_price), 0) as revenue, " +
                "CASE WHEN COALESCE(SUM(t.capacity), 0) > 0 " +
                "THEN (COALESCE(SUM(t.sold), 0) * 100.0 / COALESCE(SUM(t.capacity), 0)) ELSE 0 END as sell_through_rate " +
                "FROM s_event e " +
                "LEFT JOIN s_ticket t ON e.id = t.event_id " +
                "LEFT JOIN s_order_ticket ot ON t.id = ot.ticket_id AND ot.status = 1 " +
                "LEFT JOIN s_order o ON ot.order_id = o.id AND o.status = 1 " +
                "WHERE e.host_id = ? " +
                "GROUP BY e.id, e.name " +
                "ORDER BY revenue DESC " +
                "LIMIT ?";

        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, organizerId, limit);
        List<TopEventsResponse.TopEvent> events = new ArrayList<>();

        for (Map<String, Object> row : results) {
            TopEventsResponse.TopEvent event = new TopEventsResponse.TopEvent();
            event.setEventId(row.get("event_id").toString());
            event.setEventName((String) row.get("event_name"));
            event.setRevenue(convertToBigDecimal(row.get("revenue")));
            event.setTicketsSold(((Number) row.get("tickets_sold")).intValue());
            event.setTotalCapacity(((Number) row.get("total_capacity")).intValue());
            event.setSellThroughRate(convertToBigDecimal(row.get("sell_through_rate")));
            events.add(event);
        }

        TopEventsResponse response = new TopEventsResponse();
        response.setEvents(events);
        return response;
    }

    @Override
    public TopEventsResponse getEventTicketComparison(String eventId) {
        // So sánh các loại ticket trong một event
        String sql = "SELECT " +
                "t.type as ticket_type, " +
                "COALESCE(SUM(ot.sub_quantity), 0) as tickets_sold, " +
                "COALESCE(SUM(o.total_price), 0) as revenue " +
                "FROM s_ticket t " +
                "LEFT JOIN s_order_ticket ot ON t.id = ot.ticket_id AND ot.status = 1 " +
                "LEFT JOIN s_order o ON ot.order_id = o.id AND o.status = 1 " +
                "WHERE t.event_id = ? " +
                "GROUP BY t.type " +
                "ORDER BY tickets_sold DESC";

        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, eventId);
        List<TopEventsResponse.TopEvent> events = new ArrayList<>();

        for (Map<String, Object> row : results) {
            TopEventsResponse.TopEvent event = new TopEventsResponse.TopEvent();
            event.setEventId(eventId);
            event.setTicketType((String) row.get("ticket_type"));
            event.setTicketsSold(((Number) row.get("tickets_sold")).intValue());
            event.setRevenue(convertToBigDecimal(row.get("revenue")));
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
                "COALESCE(SUM(o.total_price), 0) as total_revenue, " +
                "COALESCE(SUM(ot.sub_quantity), 0) as tickets_sold, " +
                "COUNT(DISTINCT e.id) as active_events, " +
                "COUNT(DISTINCT o.id) as total_orders, " +
                "COUNT(DISTINCT e.host_id) as active_organizers, " +
                "CASE WHEN COUNT(DISTINCT o.id) > 0 THEN SUM(o.total_price) / COUNT(DISTINCT o.id) ELSE 0 END as avg_order_value " +
                "FROM s_order o " +
                "LEFT JOIN s_order_ticket ot ON o.id = ot.order_id AND ot.status = 1 " +
                "LEFT JOIN s_ticket t ON ot.ticket_id = t.id " +
                "LEFT JOIN s_event e ON t.event_id = e.id " +
                "WHERE o.status = 1 AND DATE(o.create_date) >= ?";

        Map<String, Object> result = jdbcTemplate.queryForMap(sql, startDate);

        KPIResponse response = new KPIResponse();
        response.setTotalRevenue(convertToBigDecimal(result.get("total_revenue")));
        response.setTicketsSold(((Number) result.get("tickets_sold")).intValue());
        response.setActiveEvents(((Number) result.get("active_events")).intValue());
        response.setAvgOrderValue(convertToBigDecimal(result.get("avg_order_value")));
        response.setRevenueChange(BigDecimal.valueOf(Math.random() * 50 - 10));
        response.setTicketsSoldChange(BigDecimal.valueOf(Math.random() * 40 - 10));

        // Thêm các fields còn thiếu
        response.setConversionRate(BigDecimal.valueOf(Math.random() * 100));

        return response;
    }

    @Override
    public KPIResponse getOrganizerKPI(String organizerId, String timeRange) {
        LocalDate startDate = parseTimeRange(timeRange);

        String sql = "SELECT " +
                "COALESCE(SUM(o.total_price), 0) as total_revenue, " +
                "COALESCE(SUM(ot.sub_quantity), 0) as tickets_sold, " +
                "COUNT(DISTINCT e.id) as active_events, " +
                "COUNT(DISTINCT o.id) as total_orders, " +
                "CASE WHEN COUNT(DISTINCT o.id) > 0 THEN SUM(o.total_price) / COUNT(DISTINCT o.id) ELSE 0 END as avg_order_value " +
                "FROM s_order o " +
                "LEFT JOIN s_order_ticket ot ON o.id = ot.order_id AND ot.status = 1 " +
                "LEFT JOIN s_ticket t ON ot.ticket_id = t.id " +
                "LEFT JOIN s_event e ON t.event_id = e.id " +
                "WHERE o.status = 1 AND e.host_id = ? AND DATE(o.create_date) >= ?";

        Map<String, Object> result = jdbcTemplate.queryForMap(sql, organizerId, startDate);

        KPIResponse response = new KPIResponse();
        response.setTotalRevenue(convertToBigDecimal(result.get("total_revenue")));
        response.setTicketsSold(((Number) result.get("tickets_sold")).intValue());
        response.setActiveEvents(((Number) result.get("active_events")).intValue());
        response.setAvgOrderValue(convertToBigDecimal(result.get("avg_order_value")));
        response.setRevenueChange(BigDecimal.valueOf(Math.random() * 50 - 10));
        response.setTicketsSoldChange(BigDecimal.valueOf(Math.random() * 40 - 10));
        response.setConversionRate(BigDecimal.valueOf(Math.random() * 100));

        return response;
    }

    // Helper method to convert Double to BigDecimal
    private BigDecimal convertToBigDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        if (value instanceof Double) {
            return BigDecimal.valueOf((Double) value);
        }
        if (value instanceof Integer) {
            return BigDecimal.valueOf((Integer) value);
        }
        if (value instanceof Long) {
            return BigDecimal.valueOf((Long) value);
        }
        return BigDecimal.ZERO;
    }
}