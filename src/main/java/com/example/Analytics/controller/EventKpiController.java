package com.example.Analytics.controller;

import com.example.Analytics.dto.SessionAction;
import com.example.Analytics.models.EventKpi;
import com.example.Analytics.models.QuizzAction;
import com.example.Analytics.models.ViewEvent;
import com.example.Analytics.service.EventKpService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@RestController
@AllArgsConstructor
@RequestMapping("/monitoring")
public class EventKpiController {
    private EventKpService service;

    @GetMapping("/subscribe")
    SseEmitter subscribe() throws IOException {
        return service.subscribe();
    }

    @PostMapping("/create-event")
    void addEventKpi(@RequestBody EventKpi eventKpi) {
        service.addKpi(eventKpi);
    }

    @PostMapping("/view-event")
    void viewEventKpi(@RequestBody ViewEvent viewEvent) {
        service.handleViewAction(viewEvent);
    }

    @PostMapping("/join-room")
    void joinRoomKpi(@RequestBody SessionAction sessionAction) {
        service.handleSessionAction(sessionAction);
    }

    @PostMapping("/end-meeting")
    void endRoomKpi( @RequestBody SessionAction sessionAction) {
        service.handleClosingSession(sessionAction);
    }

    @PostMapping("/persist-quiz")
    void passQuizKpi(QuizzAction quizzAction) {
        service.persistQuizz(quizzAction);
    }


    @GetMapping("/quizz-by-user")
    void countQuizzByUser(String userName) {
        service.countQuizzByUser(userName);
    }

    @GetMapping("/views-by-user")
    void countViewsByUser(String userName) {
        service.countViewsByUser(userName);
    }

    @GetMapping("/views-by-event")
    void countViewsByEvent(ViewEvent viewEvent) {
        service.viewEvent(viewEvent);
    }
    @GetMapping("/Quizz-by-event")
    void countQuizzByEvent(QuizzAction quizzAction) {
        service.countEventQuizzResponses(quizzAction);
    }


}
