package com.example.analytics.controller.graphql;

import com.example.analytics.models.QuizzAction;
import com.example.analytics.service.QuizActionService;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@AllArgsConstructor
@Controller
public class QuizActionController {
    private QuizActionService quizActionService;

    @MutationMapping
    void sendQuizKpi(@Argument("quizActionInput") QuizzAction quizzAction) {
        quizActionService.persistQuizz(quizzAction);
    }

    @MutationMapping
    void passQuizKpi(@Argument("quizActionInput") QuizzAction quizzAction) {
        quizActionService.persistQuizz(quizzAction);
    }

    @QueryMapping
    long countQuizzByUser(@Argument String userName) {
        return quizActionService.countQuizzByUser(userName);
    }

    @QueryMapping
    long countQuizzByEvent(@Argument UUID eventId) {
        return quizActionService.countEventQuizzResponses(eventId);
    }
}
