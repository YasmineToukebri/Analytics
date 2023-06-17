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



}
