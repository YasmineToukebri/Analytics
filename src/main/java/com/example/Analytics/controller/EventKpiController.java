package com.example.Analytics.controller;

import com.example.Analytics.dto.CountEventViews;
import com.example.Analytics.dto.SessionAction;
import com.example.Analytics.models.EventKpi;
import com.example.Analytics.models.QuizzAction;
import com.example.Analytics.models.Session;
import com.example.Analytics.models.ViewEventAction;
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
    ViewEventAction viewEventKpi(@RequestBody ViewEventAction viewEventAction) {
        return service.handleViewAction(viewEventAction);
    }

    @PostMapping("/join-room")
    Session joinRoomKpi(@RequestBody SessionAction sessionAction) {
        return service.handleSessionAction(sessionAction);
    }

    @PostMapping("/end-meeting")
    Session endRoomKpi( @RequestBody SessionAction sessionAction) {
        return service.handleClosingSession(sessionAction);
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
    long countViewsByUser(@RequestParam(name = "username") String userName) {
        return service.countViewsByUser(userName);
    }

    @GetMapping("/views-by-event")
    void countViewsByEvent(@RequestParam(name = "eventid") UUID viewEvent) {
        service.viewEvent(viewEvent);
    }

    @GetMapping("/session-duration")
    void getSessionDuration(@RequestParam(name = "username") String username, @RequestParam(name = "event_Id") UUID event_Id) {
        service.getSessionDuration(username, event_Id);
    }

    @GetMapping("/participants")
    long countParticipants() {
        return service.getParticipantsNumber();
    }

    @GetMapping("/events-participated")
    long countEventsParticpatedAt(@RequestParam(name = "username") String uername) {
        return service.countEventsParticpatedAt(uername);
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
    Session getMaxDurationByEventId(@RequestParam(name = "event_Id") UUID event_Id) {
        return  service.getMaxDurationByRoomId(event_Id);
    }

    @GetMapping("/session-duration-by-room-min")
    Session getMinDurationByEventId(@RequestParam(name = "event_Id") UUID event_Id) {
       return service.getMinDurationByRoomId(event_Id);
    }


    @GetMapping("/session-duration-max")
    Session getMaxSession() {
        return service.getMaxSession();
    }

    @GetMapping("/session-duration-min")
    Session getMinSession() {
        return service.getMinSession();
    }

    @GetMapping("/session-duration-by-user")
    String getSessionDurationByUser(@RequestParam(name = "username") String username, @RequestParam(name = "event_Id") UUID event_Id) {
    return service.getSessionDuration(username,event_Id);
    }

    @GetMapping("/maximal-participants")
    long getMaximalParticipants() {
        return service.MaximalParticipation();
    }

    @GetMapping("/minimal-participants")
    long getMinimalParticipants() {
        return service.MinimalParticipation();
    }

}
