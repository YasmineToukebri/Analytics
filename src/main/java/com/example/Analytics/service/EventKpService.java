package com.example.Analytics.service;

import com.example.Analytics.dto.SessionAction;
import com.example.Analytics.models.EventKpi;
import com.example.Analytics.models.QuizzAction;
import com.example.Analytics.models.ViewEvent;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;


public interface EventKpService {
    void addKpi(EventKpi eventKpi);

    void viewEvent(ViewEvent viewEvent);
    SseEmitter subscribe() throws IOException;
    void emitData(String action,String data);

    void handleViewAction(ViewEvent viewEvent);

    void handleSessionAction(SessionAction sessionAction);

    void handleClosingSession(SessionAction sessionAction);

    void countEventQuizzResponses(QuizzAction quizzAction);

    void countQuizzByUser(String userName);

    void countViewsByUser(String userName);

    void persistQuizz(QuizzAction quizzAction);
}
