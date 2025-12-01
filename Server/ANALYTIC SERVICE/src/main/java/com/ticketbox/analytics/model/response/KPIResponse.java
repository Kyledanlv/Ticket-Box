package com.ticketbox.analytics.model.response;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class KPIResponse {
    private BigDecimal totalRevenue;
    private Integer ticketsSold;
    private Integer activeEvents;
    private BigDecimal avgOrderValue;
    private BigDecimal revenueChange; // percentage vs previous period
    private BigDecimal ticketsSoldChange; // percentage vs previous period
    private BigDecimal conversionRate;
}