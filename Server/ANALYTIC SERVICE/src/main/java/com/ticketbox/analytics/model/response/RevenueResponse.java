package com.ticketbox.analytics.model.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class RevenueResponse {
    private List<RevenueDataPoint> chartData;
    private RevenueSummary summary;

    public List<RevenueDataPoint> getChartData() { return chartData; }
    public void setChartData(List<RevenueDataPoint> chartData) { this.chartData = chartData; }

    public RevenueSummary getSummary() { return summary; }
    public void setSummary(RevenueSummary summary) { this.summary = summary; }

    public static class RevenueDataPoint {
        private LocalDateTime date;
        private BigDecimal revenue;
        private Integer ticketsSold;
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
        private BigDecimal totalRevenue;
        private Integer totalTickets;
        private Integer totalOrders;
        private BigDecimal averageTicketPrice;
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