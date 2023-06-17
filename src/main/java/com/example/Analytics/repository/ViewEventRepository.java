package com.example.Analytics.repository;

import com.example.Analytics.models.ViewEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ViewEventRepository extends JpaRepository<ViewEvent, UUID> {
//    @Query("SELECT v.eventId, COUNT(v.eventId) as occurrence FROM ViewEvent v GROUP BY v.eventId ORDER BY occurrence ASC")
//    Object[] findMinEventIdOccurrence();

    @Query("SELECT v FROM ViewEvent v WHERE v.eventId = (SELECT v2.eventId FROM ViewEvent v2 GROUP BY v2.eventId ORDER BY COUNT(v2.eventId) ASC )")
    ViewEvent findViewEventWithMinOccurrence();

    @Query("SELECT COUNT(v.eventId) FROM ViewEvent v GROUP BY v.eventId HAVING COUNT(v.eventId) = (SELECT MIN(COUNT(v2.eventId)) FROM ViewEvent v2 GROUP BY v2.eventId)")
    int findMinEventIdOccurrence();

    @Query("SELECT COUNT(v.eventId) FROM ViewEvent v GROUP BY v.eventId HAVING COUNT(v.eventId) = (SELECT MAX(COUNT(v2.eventId)) FROM ViewEvent v2 GROUP BY v2.eventId)")
    int findMaxEventIdOccurrence();



}
