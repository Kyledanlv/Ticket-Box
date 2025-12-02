package com.ticketbox.analytics.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Revenue analytics response")
public class RevenueResponse {
    @Schema(description = "Chart data points for revenue visualization")
    private List<RevenueDataPoint> chartData;

    @Schema(description = "Summary of revenue analytics")
    private RevenueSummary summary;

    public List<RevenueDataPoint> getChartData() { return chartData; }
    public void setChartData(List<RevenueDataPoint> chartData) { this.chartData = chartData; }

    public RevenueSummary getSummary() { return summary; }
    public void setSummary(RevenueSummary summary) { this.summary = summary; }

    @Schema(description = "Revenue data point for a specific period")
    public static class RevenueDataPoint {
        @Schema(description = "Date/time of the data point", example = "2024-12-01T00:00:00")
        private LocalDateTime date;

        @Schema(description = "Revenue amount for the period", example = "15000.50")
        private BigDecimal revenue;

        @Schema(description = "Number of tickets sold", example = "50")
        private Integer ticketsSold;

        @Schema(description = "Number of orders", example = "15")
        private Integer orderCount;

        public LocalDateTime getDate() { return date; }
        public void setDate(LocalDateTime date) { this.date = date; }

        public BigDecimal getRevenue() { return revenue; }
        public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }

        public Integer getTicketsSold() { return ticketsSold; }
        public void setTicketsSold(Integer ticketsSold) { this.ticketsSold = ticketsSold; }

        public Integer getOrderCount() { return orderCount; }
        public void setOrderCount(Integer orderCount) { this.orderCount = orderCount; }
    }

    public static class RevenueSummary {
        @Schema(description = "Total revenue", example = "125000.75")
        private BigDecimal totalRevenue;

        @Schema(description = "Total tickets sold", example = "450")
        private Integer totalTickets;

        @Schema(description = "Total number of orders", example = "120")
        private Integer totalOrders;

        @Schema(description = "Average ticket price", example = "277.78")
        private BigDecimal averageTicketPrice;

        @Schema(description = "Revenue change percentage (vs previous period)", example = "12.5")
        private BigDecimal revenueChange;

        public BigDecimal getTotalRevenue() { return totalRevenue; }
        public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }

        public Integer getTotalTickets() { return totalTickets; }
        public void setTotalTickets(Integer totalTickets) { this.totalTickets = totalTickets; }

        public Integer getTotalOrders() { return totalOrders; }
        public void setTotalOrders(Integer totalOrders) { this.totalOrders = totalOrders; }

        public BigDecimal getAverageTicketPrice() { return averageTicketPrice; }
        public void setAverageTicketPrice(BigDecimal averageTicketPrice) { this.averageTicketPrice = averageTicketPrice; }

        public BigDecimal getRevenueChange() { return revenueChange; }
        public void setRevenueChange(BigDecimal revenueChange) { this.revenueChange = revenueChange; }
    }
}