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
import java.util.UUID;

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

    @PostMapping("/send-quiz")
    void passQuizKpi(@RequestBody  QuizzAction quizzAction) {
        service.persistQuizz(quizzAction);
    }


    @GetMapping("/quizz-by-user")
    void countQuizzByUser(@RequestParam(name = "username") String userName) {
        service.countQuizzByUser(userName);
    }

    @GetMapping("/quizz-by-event")
    void countQuizzByEvent(@RequestParam(name = "eventid") UUID eventId) {
        service.countEventQuizzResponses(eventId);
    }

    @GetMapping("/views-by-user")
    void countViewsByUser(@RequestParam(name = "username") String userName) {
        service.countViewsByUser(userName);
    }

    @GetMapping("/views-by-event")
    void countViewsByEvent(@RequestParam(name = "eventid") UUID viewEvent) {
        service.viewEvent(viewEvent);
    }

    @GetMapping("/session-duration")
    void getSessionDuration(@RequestParam(name = "username") String username) {
        service.getSessionDuration(username);
    }

    @GetMapping("/participants")
    void countParticipants(@RequestBody SessionAction sessionAction) {
        service.countParticipants(sessionAction);
    }

    @GetMapping("/participants-by-room")
    void countParticipantsByRoomId(@RequestBody SessionAction sessionAction) {
        service.countParticipantsByRoomId(sessionAction);
    }
}
