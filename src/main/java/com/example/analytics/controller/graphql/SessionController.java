package com.example.analytics.controller.graphql;

import com.example.analytics.dto.MaxMinSession;
import com.example.analytics.dto.Participation;
import com.example.analytics.dto.SessionAction;
import com.example.analytics.models.Session;
import com.example.analytics.service.SessionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@AllArgsConstructor
@Controller
public class SessionController {
    private SessionService sessionService;

    @MutationMapping
    Session joinRoomKpi(@Argument("sessionActionInput") SessionAction sessionAction) {
        return sessionService.handleSessionAction(sessionAction);
    }

    @MutationMapping
    Session endRoomKpi(@Argument("sessionActionInput") SessionAction sessionAction) throws JsonProcessingException {
        return sessionService.handleClosingSession(sessionAction);
    }

    @QueryMapping
    MaxMinSession getSessionDuration(@Argument String userName, @Argument UUID eventId) {
        return sessionService.getSessionDuration(userName, eventId);
    }

    @QueryMapping
    long countParticipants() {
        return sessionService.getParticipantsNumber();
    }

    @QueryMapping
    long countEventsParticipantAt(@Argument("userName") String username) {
        return sessionService.countEventsParticpatedAt(username);
    }

    @QueryMapping
    long countParticipantsByRoomId(@Argument("roomId") UUID roomId) {
        return sessionService.countParticipantsByRoomId(roomId);
    }

    @QueryMapping
    Session getMaxDurationByEventId(@Argument(name = "eventId") UUID eventId) {
        return  sessionService.getMaxDurationByRoomId(eventId);
    }

    @QueryMapping
    Session getMinDurationByEventId(@Argument(name = "eventId") UUID eventId) {
        return sessionService.getMinDurationByRoomId(eventId);
    }

    @QueryMapping
    MaxMinSession getMaxSession() {
        return sessionService.getMaxSession();
    }

    @QueryMapping
    MaxMinSession getMinSession() {
        return sessionService.getMinSession();
    }

    @QueryMapping
    Participation getMaximalParticipants() {
        return sessionService.maximalParticipation();
    }

    @QueryMapping
    Participation getMinimalParticipants() {
        return sessionService.minimalParticipation();
    }

    @QueryMapping
    MaxMinSession getLastSessionDurationByUser(@Argument String userName) {
        System.out.println(sessionService.getLastSessionDuration(userName));
        return sessionService.getLastSessionDuration(userName);
    }
}
