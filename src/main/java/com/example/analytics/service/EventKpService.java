package com.example.analytics.service;

import com.example.analytics.dto.*;
import com.example.analytics.models.*;
import com.example.analytics.models.ViewEventAction;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.UUID;


public interface EventKpService {
    void addKpi(EventKpi eventKpi);

    long countALlEvents();

    long countAllEventsByUserName(String userName);

    void emitData(String action, String data);

    void handleViewAction(ViewEvent viewEvent);

    long getSessionDuration(String username);

    String getUsernameWithMostEvents();

    String findUsernameWithLeastEvents();

    double calculateAverageEventsPerUser();

    void abortEvent(AbortEvent abortEvent);

    Long findTotalByToday();

    Long findTotalByCurrentWeek();

    Long findTotalByCurrentMonth();

    Long findTotalByTodayAndUserName(String userName);

    String findUserWithLeastAbortedEvents();

    String findUserWithMostAbortedEvents();

    Long findTotalAbortedEventToday();

    Long findTotalAbortedEventByCurrentWeek();

    Long findTotalAbortedEventByCurrentMonth();

    double calculateAverageAbortedEventsPerUser();


    Long findTotalByCurrentWeekAndUserName(String userName);

    Long findTotalByCurrentMonthAndUserName(String userName);

    long viewEvent(UUID viewEvent);

    SseEmitter subscribe() throws IOException;

    void emitData(DataToEmit data);

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
