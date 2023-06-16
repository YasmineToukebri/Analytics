package com.example.Analytics.repository;

import com.example.Analytics.models.QuizzAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface quizAction extends JpaRepository<QuizzAction, UUID> {
     long countAllByEventId(UUID eventId);
     long countAllByUserName(String userName);
}
