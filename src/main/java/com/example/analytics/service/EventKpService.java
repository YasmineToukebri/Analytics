package com.example.analytics.service;

import com.example.analytics.dto.CountEventViews;
import com.example.analytics.dto.MaxMinSession;
import com.example.analytics.dto.SessionAction;
import com.example.analytics.models.EventKpi;
import com.example.analytics.models.QuizzAction;
import com.example.analytics.models.Session;
import com.example.analytics.models.ViewEventAction;
import com.example.analytics.dto.Participation;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.UUID;


public interface EventKpService {
    void addKpi(EventKpi eventKpi);

    long viewEvent(UUID viewEvent);
    SseEmitter subscribe() throws IOException;
    void emitData(String action,String data);

    ViewEventAction handleViewAction(ViewEventAction viewEventAction) throws JsonProcessingException;

    Session handleSessionAction(SessionAction sessionAction);

    Session handleClosingSession(SessionAction sessionAction) throws JsonProcessingException;

    MaxMinSession getSessionDuration(String username, UUID roomId);

    long countEventQuizzResponses(UUID eventId);

    long countQuizzByUser(String userName);

    long countViewsByUser(String userName);

    void persistQuizz(QuizzAction quizzAction);


    long countEventsParticpatedAt(String uername);

    long countParticipantsByRoomId(UUID roomId);


    CountEventViews getMaxViews();

    CountEventViews getMinViews();

    Session getMaxDurationByRoomId(UUID roomId);

    Session getMinDurationByRoomId(UUID roomId);

    MaxMinSession getMaxSession();

    MaxMinSession getMinSession();

    long getParticipantsNumber();

    Participation maximalParticipation();

    Participation minimalParticipation();

    MaxMinSession getLastSessionDuration(String username);
}
