package com.example.Analytics.repository;

import com.example.Analytics.models.EventKpi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EventKpiRepository extends JpaRepository<EventKpi, UUID> {
    public long countByUserName(String Username);
}
