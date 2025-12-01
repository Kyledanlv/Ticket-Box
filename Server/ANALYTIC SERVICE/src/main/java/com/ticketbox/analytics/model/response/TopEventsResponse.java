package com.ticketbox.analytics.model.response;

import java.math.BigDecimal;
import java.util.List;

public class TopEventsResponse {
    private List<TopEvent> events;

    public List<TopEvent> getEvents() {
        return events;
    }

    public void setEvents(List<TopEvent> events) {
        this.events = events;
    }

    public static class TopEvent {
        private String eventId;
        private String eventName;
        private BigDecimal revenue;
        private Integer ticketsSold;
        private Integer totalCapacity;
        private BigDecimal sellThroughRate;
        private String organizerName;

        public String getEventId() {
            return eventId;
        }

        public void setEventId(String eventId) {
            this.eventId = eventId;
        }

        public String getEventName() {
            return eventName;
        }

        public void setEventName(String eventName) {
            this.eventName = eventName;
        }

        public BigDecimal getRevenue() {
            return revenue;
        }

        public void setRevenue(BigDecimal revenue) {
            this.revenue = revenue;
        }

        public Integer getTicketsSold() {
            return ticketsSold;
        }

        public void setTicketsSold(Integer ticketsSold) {
            this.ticketsSold = ticketsSold;
        }

        public Integer getTotalCapacity() {
            return totalCapacity;
        }

        public void setTotalCapacity(Integer totalCapacity) {
            this.totalCapacity = totalCapacity;
        }

        public BigDecimal getSellThroughRate() {
            return sellThroughRate;
        }

        public void setSellThroughRate(BigDecimal sellThroughRate) {
            this.sellThroughRate = sellThroughRate;
        }

        public String getOrganizerName() {
            return organizerName;
        }

        public void setOrganizerName(String organizerName) {
            this.organizerName = organizerName;
        }
    }
}