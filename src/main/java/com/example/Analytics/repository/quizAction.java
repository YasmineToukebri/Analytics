package com.example.Analytics.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.Analytics.models.QuizzAction;
import java.util.UUID;

@Repository
public interface quizAction extends JpaRepository<QuizzAction, UUID> {
     long countAllByEventId(UUID eventId);
}
