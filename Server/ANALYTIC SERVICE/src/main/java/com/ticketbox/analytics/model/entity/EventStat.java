package com.ticketbox.analytics.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "event_stats")
@Data
public class EventStat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id")
    private String eventId;

    @Column(name = "event_name")
    private String eventName;

    @Column(name = "organizer_id")
    private String organizerId;

    @Column(name = "total_revenue", precision = 15, scale = 2)
    private BigDecimal totalRevenue;

    @Column(name = "tickets_sold")
    private Integer ticketsSold;

    @Column(name = "total_capacity")
    private Integer totalCapacity;

    @Column(name = "sell_through_rate", precision = 5, scale = 2)
    private BigDecimal sellThroughRate;

    @Column(name = "avg_ticket_price", precision = 10, scale = 2)
    private BigDecimal avgTicketPrice;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;
}