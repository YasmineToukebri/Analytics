package com.example.Analytics.repository;

import com.example.Analytics.models.AbortEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface AbortEventRepository extends JpaRepository<AbortEvent, UUID> {

    @Query("SELECT a.userName, COUNT(a) FROM AbortEvent a GROUP BY a.userName ORDER BY COUNT(a) ASC")
    List<String> findUserWithLeastAbortedEvents();

    @Query("SELECT a.userName, COUNT(a) FROM AbortEvent a GROUP BY a.userName ORDER BY COUNT(a) DESC")
    List<String> findUserWithMostAbortedEvents();


    @Query("SELECT COUNT(e) FROM AbortEvent e WHERE FUNCTION('DATE', e.abortedAt) = FUNCTION('CURRENT_DATE')")
    Long findTotalAbortedEventToday();

    @Query("SELECT COUNT(e) FROM AbortEvent e WHERE EXTRACT('year' FROM e.abortedAt) = EXTRACT('year' FROM CURRENT_DATE) AND TO_CHAR(e.abortedAt, 'IW') = TO_CHAR(CURRENT_DATE, 'IW')")
    Long findTotalAbortedEventByCurrentWeek();

    @Query("SELECT COUNT(e) FROM AbortEvent e WHERE FUNCTION('YEAR', e.abortedAt) = FUNCTION('YEAR', FUNCTION('CURRENT_DATE')) AND FUNCTION('MONTH', e.abortedAt) = FUNCTION('MONTH', FUNCTION('CURRENT_DATE'))")
    Long findTotalAbortedEventByCurrentMonth();

    @Query("SELECT COUNT(e) / COUNT(DISTINCT e.userName) FROM AbortEvent e")
    double calculateAverageAbortedEventsPerUser();


}
