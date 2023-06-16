package com.example.Analytics.repository;

import com.example.Analytics.models.PassQuiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PassQuizRepository extends JpaRepository<PassQuiz, UUID> {
}
