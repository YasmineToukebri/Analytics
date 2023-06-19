package com.example.analytics.controller;

import com.example.analytics.exception.EmptyListException;
import com.example.analytics.dto.CountEventViews;
import com.example.analytics.dto.MaxMinSession;
import com.example.analytics.dto.SessionAction;
import com.example.analytics.models.EventKpi;
import com.example.analytics.models.QuizzAction;
import com.example.analytics.models.Session;
import com.example.analytics.models.ViewEventAction;
import com.example.analytics.service.EventKpService;
import com.example.analytics.dto.Participation;
import com.fasterxml.jackson.core.JsonProcessingException;
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
    ViewEventAction viewEventKpi(@RequestBody ViewEventAction viewEventAction) throws JsonProcessingException, EmptyListException {
        return service.handleViewAction(viewEventAction);
    }

    @PostMapping("/join-room")
    Session joinRoomKpi(@RequestBody SessionAction sessionAction) {
        return service.handleSessionAction(sessionAction);
    }

    @PostMapping("/end-meeting")
    Session endRoomKpi( @RequestBody SessionAction sessionAction) throws JsonProcessingException, EmptyListException {
        return service.handleClosingSession(sessionAction);
    }

    @PostMapping("/send-quiz")
    void passQuizKpi(@RequestBody QuizzAction quizzAction) {
        service.persistQuizz(quizzAction);
    }

    @GetMapping("/views-by-user")
    long countViewsByUser(@RequestParam(name = "username") String userName) {
        return service.countViewsByUser(userName);
    }

    @GetMapping("/views-by-event")
    void countViewsByEvent(@RequestParam(name = "eventid") UUID viewEvent) {
        service.viewEvent(viewEvent);
    }

    @GetMapping("/session-duration")
    void getSessionDuration(@RequestParam(name = "username") String username, @RequestParam(name = "event_Id") UUID eventId) {
        service.getSessionDuration(username, eventId);
    }

    @GetMapping("/participants")
    long countParticipants() {
        return service.getParticipantsNumber();
    }

    @GetMapping("/events-participated")
    long countEventsParticipantAt(@RequestParam(name = "username") String username) {
        return service.countEventsParticpatedAt(username);
    }

    @GetMapping("/participants-by-room")
    long countParticipantsByRoomId(@RequestParam(name = "roomId") UUID roomId) {
       return service.countParticipantsByRoomId(roomId);
    }

    @GetMapping("/max-views")
    CountEventViews getMaxViews() {
        return  service.getMaxViews();
    }

    @GetMapping("/min-views")
    CountEventViews getMinViews() {
       return service.getMinViews();
    }

    @GetMapping("/session-duration-by-room-max")
    Session getMaxDurationByEventId(@RequestParam(name = "event_Id") UUID eventId) {
        return  service.getMaxDurationByRoomId(eventId);
    }

    @GetMapping("/session-duration-by-room-min")
    Session getMinDurationByEventId(@RequestParam(name = "event_Id") UUID eventId) {
       return service.getMinDurationByRoomId(eventId);
    }


    @GetMapping("/session-duration-max")
    MaxMinSession getMaxSession() {
        return service.getMaxSession();
    }

    @GetMapping("/session-duration-min")
    MaxMinSession getMinSession() {
        return service.getMinSession();
    }

    @GetMapping("/session-duration-by-user")
    MaxMinSession getSessionDurationByUser(@RequestParam(name = "username") String username, @RequestParam(name = "event_Id") UUID eventId) {
    return service.getSessionDuration(username, eventId);
    }

    @GetMapping("/maximal-participants")
    Participation getMaximalParticipants() {
        return service.maximalParticipation();
    }

    @GetMapping("/minimal-participants")
    Participation getMinimalParticipants() {
        return service.minimalParticipation();
    }

    @GetMapping("/quizz-by-user")
    void countQuizzByUser(@RequestParam(name = "username") String userName) {
        service.countQuizzByUser(userName);
    }

    @GetMapping("/quizz-by-event")
    void countQuizzByEvent(@RequestParam(name = "eventid") UUID eventId) {
        service.countEventQuizzResponses(eventId);
    }


    @GetMapping("/user-last-session-duration")
    MaxMinSession getLastSessionDurationByUser(@RequestParam(name = "username") String username) {
        return service.getLastSessionDuration(username);
    }
}
