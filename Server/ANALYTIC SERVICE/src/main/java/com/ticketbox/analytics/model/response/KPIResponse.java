package com.ticketbox.analytics.model.response;

import java.math.BigDecimal;

public class KPIResponse {
    private BigDecimal totalRevenue;
    private Integer ticketsSold;
    private Integer activeEvents;
    private BigDecimal avgOrderValue;
    private BigDecimal revenueChange;
    private BigDecimal ticketsSoldChange;
    private BigDecimal conversionRate;

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public Integer getTicketsSold() {
        return ticketsSold;
    }

    public void setTicketsSold(Integer ticketsSold) {
        this.ticketsSold = ticketsSold;
    }

    public Integer getActiveEvents() {
        return activeEvents;
    }

    public void setActiveEvents(Integer activeEvents) {
        this.activeEvents = activeEvents;
    }

    public BigDecimal getAvgOrderValue() {
        return avgOrderValue;
    }

    public void setAvgOrderValue(BigDecimal avgOrderValue) {
        this.avgOrderValue = avgOrderValue;
    }

    public BigDecimal getRevenueChange() {
        return revenueChange;
    }

    public void setRevenueChange(BigDecimal revenueChange) {
        this.revenueChange = revenueChange;
    }

    public BigDecimal getTicketsSoldChange() {
        return ticketsSoldChange;
    }

    public void setTicketsSoldChange(BigDecimal ticketsSoldChange) {
        this.ticketsSoldChange = ticketsSoldChange;
    }

    public BigDecimal getConversionRate() {
        return conversionRate;
    }

    public void setConversionRate(BigDecimal conversionRate) {
        this.conversionRate = conversionRate;
    }
}