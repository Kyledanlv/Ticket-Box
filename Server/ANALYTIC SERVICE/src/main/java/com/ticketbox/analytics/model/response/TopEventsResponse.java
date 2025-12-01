package com.ticketbox.analytics.model.response;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class TopEventsResponse {
    private List<TopEvent> events;

    @Data
    public static class TopEvent {
        private String eventId;
        private String eventName;
        private BigDecimal revenue;
        private Integer ticketsSold;
        private Integer totalCapacity;
        private BigDecimal sellThroughRate;
        private String organizerName;
    }
}