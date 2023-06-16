package com.example.Analytics.repository;

import com.example.Analytics.models.ViewEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ViewEventRepository extends JpaRepository<ViewEvent, UUID> {
    long countAllByEventId(UUID eventId);
}
