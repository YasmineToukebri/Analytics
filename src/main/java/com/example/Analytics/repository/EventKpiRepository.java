package com.example.Analytics.repository;

import com.example.Analytics.models.EventKpi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EventKpiRepository extends JpaRepository<EventKpi, UUID> {

    @Query("SELECT e.userName FROM EventKpi e GROUP BY e.userName ORDER BY COUNT(e) DESC ")
    List<String> findUsernameWithMostEvents();

    @Query("SELECT e.userName FROM EventKpi e GROUP BY e.userName ORDER BY COUNT(e) ASC")
    List<String> findUsernameWithLeastEvents();

    @Query("SELECT COUNT(e) / COUNT(DISTINCT e.userName) FROM EventKpi e")
    double calculateAverageEventsPerUser();


}
