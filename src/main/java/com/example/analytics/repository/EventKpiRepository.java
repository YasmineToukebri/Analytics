package com.example.analytics.repository;

import com.example.analytics.models.EventKpi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EventKpiRepository extends JpaRepository<EventKpi, UUID> {
    long countByUserName(String username);
}
