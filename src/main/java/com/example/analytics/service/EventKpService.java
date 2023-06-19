package com.example.analytics.service;

import com.example.analytics.Exception.emptyListException;
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

    ViewEventAction handleViewAction(ViewEventAction viewEventAction) throws JsonProcessingException, emptyListException;

    Session handleSessionAction(SessionAction sessionAction);

    Session handleClosingSession(SessionAction sessionAction) throws JsonProcessingException, emptyListException;

    MaxMinSession getSessionDuration(String username, UUID roomId) throws emptyListException;

    long countEventQuizzResponses(UUID eventId);

    long countQuizzByUser(String userName);

    long countViewsByUser(String userName);

    void persistQuizz(QuizzAction quizzAction);


    long countEventsParticpatedAt(String uername);

    long countParticipantsByRoomId(UUID roomId);


    CountEventViews getMaxViews() throws emptyListException;

    CountEventViews getMinViews() throws emptyListException;

    Session getMaxDurationByRoomId(UUID roomId) throws emptyListException;

    Session getMinDurationByRoomId(UUID roomId) throws emptyListException;

    MaxMinSession getMaxSession() throws emptyListException;

    MaxMinSession getMinSession() throws emptyListException;

    long getParticipantsNumber();

    Participation maximalParticipation() throws emptyListException;

    Participation minimalParticipation() throws emptyListException;

    MaxMinSession getLastSessionDuration(String username) throws emptyListException;
}
