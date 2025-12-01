package com.ticketbox.analytics.repository;

import com.ticketbox.analytics.model.entity.EventStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EventStatsRepository extends JpaRepository<EventStat, Long> {

    // Top events system-wide
    @Query("SELECT es FROM EventStat es ORDER BY es.totalRevenue DESC LIMIT :limit")
    List<EventStat> findTopEventsByRevenue(@Param("limit") int limit);

    // Top events for organizer
    @Query("SELECT es FROM EventStat es WHERE es.organizerId = :organizerId " +
            "ORDER BY es.totalRevenue DESC LIMIT :limit")
    List<EventStat> findTopEventsByOrganizer(@Param("organizerId") String organizerId,
                                             @Param("limit") int limit);

    // Event by ID
    @Query("SELECT es FROM EventStat es WHERE es.eventId = :eventId")
    EventStat findByEventId(@Param("eventId") String eventId);
}