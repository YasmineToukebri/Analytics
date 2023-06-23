package com.example.analytics.service;

import com.example.analytics.models.QuizzAction;

import java.util.UUID;

public interface QuizActionService {
    long countEventQuizzResponses(UUID eventId);

    long countQuizzByUser(String userName);


    void persistQuizz(QuizzAction quizzAction);
}
