package com.example.analytics.service;

import com.example.analytics.dto.SessionAction;
import com.example.analytics.models.AbortEvent;
import com.example.analytics.models.EventKpi;
import com.example.analytics.models.QuizzAction;
import com.example.analytics.models.ViewEvent;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.UUID;


public interface EventKpService {
    void addKpi(EventKpi eventKpi);

    long countALlEvents();

    long countAllEventsByUserName(String userName);

    long viewEvent(UUID viewEvent);

    SseEmitter subscribe() throws IOException;

    void emitData(String action, String data);

    void handleViewAction(ViewEvent viewEvent);

    void handleSessionAction(SessionAction sessionAction);

    void handleClosingSession(SessionAction sessionAction);

    long getSessionDuration(String username);

    long countEventQuizzResponses(UUID eventId);

    long countQuizzByUser(String userName);

    long countViewsByUser(String userName);

    void persistQuizz(QuizzAction quizzAction);

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
}
