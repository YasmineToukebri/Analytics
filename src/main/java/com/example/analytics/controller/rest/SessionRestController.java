package com.example.analytics.controller.rest;

import com.example.analytics.dto.MaxMinSession;
import com.example.analytics.dto.Participation;
import com.example.analytics.dto.SessionAction;
import com.example.analytics.models.Session;
import com.example.analytics.service.SessionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/monitoring")
public class SessionRestController {
    private SessionService sessionService;

    @PostMapping("/join-room")
    Session joinRoomKpi(@RequestBody SessionAction sessionAction) {
        return sessionService.handleSessionAction(sessionAction);
    }

    @PostMapping("/end-meeting")
    Session endRoomKpi( @RequestBody SessionAction sessionAction) throws JsonProcessingException {
        return sessionService.handleClosingSession(sessionAction);
    }

    @GetMapping("/session-duration")
    MaxMinSession getSessionDuration(@RequestParam(name = "username") String username, @RequestParam(name = "event_Id") UUID eventId) {
        return sessionService.getSessionDuration(username, eventId);
    }

    @GetMapping("/participants")
    long countParticipants() {
        return sessionService.getParticipantsNumber();
    }

    @GetMapping("/events-participated")
    long countEventsParticipantAt(@RequestParam(name = "username") String username) {
        return sessionService.countEventsParticpatedAt(username);
    }

    @GetMapping("/participants-by-room")
    long countParticipantsByRoomId(@RequestParam(name = "roomId") UUID roomId) {
        return sessionService.countParticipantsByRoomId(roomId);
    }

    @GetMapping("/session-duration-by-room-max")
    Session getMaxDurationByEventId(@RequestParam(name = "event_Id") UUID eventId) {
        return  sessionService.getMaxDurationByRoomId(eventId);
    }

    @GetMapping("/session-duration-by-room-min")
    Session getMinDurationByEventId(@RequestParam(name = "event_Id") UUID eventId) {
        return sessionService.getMinDurationByRoomId(eventId);
    }

    @GetMapping("/session-duration-max")
    MaxMinSession getMaxSession() {
        return sessionService.getMaxSession();
    }

    @GetMapping("/session-duration-min")
    MaxMinSession getMinSession() {
        return sessionService.getMinSession();
    }

    @GetMapping("/maximal-participants")
    Participation getMaximalParticipants() {
        return sessionService.maximalParticipation();
    }

    @GetMapping("/minimal-participants")
    Participation getMinimalParticipants() {
        return sessionService.minimalParticipation();
    }

    @GetMapping("/user-last-session-duration")
    MaxMinSession getLastSessionDurationByUser(@RequestParam(name = "username") String username) {
        return sessionService.getLastSessionDuration(username);
    }

}
