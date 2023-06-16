package com.example.Analytics.repository;

import com.example.Analytics.models.SendQuiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SendQuizRepository extends JpaRepository<SendQuiz, UUID> {
}
