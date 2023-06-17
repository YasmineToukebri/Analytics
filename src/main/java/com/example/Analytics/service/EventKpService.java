package com.example.Analytics.service;

import com.example.Analytics.dto.SessionAction;
import com.example.Analytics.models.EventKpi;
import com.example.Analytics.models.QuizzAction;
import com.example.Analytics.models.ViewEvent;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.UUID;


public interface EventKpService {
    void addKpi(EventKpi eventKpi);

    long viewEvent(UUID viewEvent);
    SseEmitter subscribe() throws IOException;
    void emitData(String action,String data);

    void handleViewAction(ViewEvent viewEvent);

    void handleSessionAction(SessionAction sessionAction);

    void handleClosingSession(SessionAction sessionAction);

    long getSessionDuration(String username);

    long countEventQuizzResponses(UUID eventId);

    long countQuizzByUser(String userName);

    long countViewsByUser(String userName);

    void persistQuizz(QuizzAction quizzAction);

    Long findTotalByToday();

    Long findTotalByCurrentWeek();

    Long findTotalByCurrentMonth();
    Long findTotalByTodayAndUserName(String userName);

    Long findTotalByCurrentWeekAndUserName(String userName);

    Long findTotalByCurrentMonthAndUserName(String userName);
}
