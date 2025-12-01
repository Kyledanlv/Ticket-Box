package com.ticketbox.analytics.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "revenue_metrics")
@Data
public class RevenueMetric {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "organizer_id")
    private String organizerId;

    @Column(name = "event_id")
    private String eventId;

    @Column(name = "date_recorded")
    private LocalDateTime dateRecorded;

    @Column(name = "revenue_amount", precision = 15, scale = 2)
    private BigDecimal revenueAmount;

    @Column(name = "tickets_sold")
    private Integer ticketsSold;

    @Column(name = "order_count")
    private Integer orderCount;

    @Column(name = "metric_type") // DAILY, WEEKLY, MONTHLY
    private String metricType;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}