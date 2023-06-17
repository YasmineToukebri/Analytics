package com.example.Analytics.service;

import com.example.Analytics.dto.CountEventViews;
import com.example.Analytics.dto.SessionAction;
import com.example.Analytics.models.EventKpi;
import com.example.Analytics.models.QuizzAction;
import com.example.Analytics.models.Session;
import com.example.Analytics.models.ViewEventAction;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.UUID;


public interface EventKpService {
    void addKpi(EventKpi eventKpi);

    long viewEvent(UUID viewEvent);
    SseEmitter subscribe() throws IOException;
    void emitData(String action,String data);

    ViewEventAction handleViewAction(ViewEventAction viewEventAction);

    Session handleSessionAction(SessionAction sessionAction);

    Session handleClosingSession(SessionAction sessionAction);

    String getSessionDuration(String username, UUID roomId);

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

    Session getMaxSession();

    Session getMinSession();

    long getParticipantsNumber();

    long MaximalParticipation();

    long MinimalParticipation();
}
