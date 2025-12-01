package com.ticketbox.analytics.repository;

import com.ticketbox.analytics.model.entity.RevenueMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RevenueMetricsRepository extends JpaRepository<RevenueMetric, Long> {

    // For system revenue (all organizers)
    @Query("SELECT rm FROM RevenueMetric rm WHERE rm.dateRecorded BETWEEN :startDate AND :endDate " +
            "AND rm.metricType = 'DAILY' ORDER BY rm.dateRecorded")
    List<RevenueMetric> findSystemRevenueByDateRange(@Param("startDate") LocalDateTime startDate,
                                                     @Param("endDate") LocalDateTime endDate);

    // For organizer revenue
    @Query("SELECT rm FROM RevenueMetric rm WHERE rm.organizerId = :organizerId " +
            "AND rm.dateRecorded BETWEEN :startDate AND :endDate " +
            "AND rm.metricType = 'DAILY' ORDER BY rm.dateRecorded")
    List<RevenueMetric> findOrganizerRevenueByDateRange(@Param("organizerId") String organizerId,
                                                        @Param("startDate") LocalDateTime startDate,
                                                        @Param("endDate") LocalDateTime endDate);

    // For event revenue
    @Query("SELECT rm FROM RevenueMetric rm WHERE rm.eventId = :eventId " +
            "AND rm.dateRecorded BETWEEN :startDate AND :endDate " +
            "AND rm.metricType = 'DAILY' ORDER BY rm.dateRecorded")
    List<RevenueMetric> findEventRevenueByDateRange(@Param("eventId") String eventId,
                                                    @Param("startDate") LocalDateTime startDate,
                                                    @Param("endDate") LocalDateTime endDate);
}