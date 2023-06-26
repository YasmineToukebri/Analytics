package com.example.analytics.controller.rest;

import com.example.analytics.models.QuizzAction;
import com.example.analytics.service.QuizActionService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/monitoring")
public class QuizActionRestController {
    private QuizActionService quizActionService;

    @PostMapping("/send-quiz")
    void sendQuizKpi(@RequestBody QuizzAction quizzAction) {
        quizActionService.persistQuizz(quizzAction);
    }

    @PostMapping("/pass-quiz")
    void passQuizKpi(@RequestBody QuizzAction quizzAction) {
        quizActionService.persistQuizz(quizzAction);
    }

    @GetMapping("/quizz-by-user")
    long countQuizzByUser(@RequestParam(name = "username") String userName) {
        return quizActionService.countQuizzByUser(userName);
    }

    @GetMapping("/quizz-by-event")
    long countQuizzByEvent(@RequestParam(name = "eventid") UUID eventId) {
        return quizActionService.countEventQuizzResponses(eventId);
    }
}
