package com.ticketbox.analytics.model.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class RevenueResponse {
    private List<RevenueDataPoint> chartData;
    private RevenueSummary summary;

    @Data
    public static class RevenueDataPoint {
        private LocalDateTime date;
        private BigDecimal revenue;
        private Integer ticketsSold;
        private Integer orderCount;
    }

    @Data
    public static class RevenueSummary {
        private BigDecimal totalRevenue;
        private Integer totalTickets;
        private Integer totalOrders;
        private BigDecimal averageTicketPrice;
        private BigDecimal revenueChange; // percentage
    }
}