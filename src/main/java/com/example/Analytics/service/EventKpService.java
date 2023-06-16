package com.example.Analytics.service;

import com.example.Analytics.models.EventKpi;
import com.example.Analytics.models.QuizzAction;
import com.example.Analytics.models.SessionAction;
import com.example.Analytics.models.ViewEvent;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;


public interface EventKpService {
    void addKpi(EventKpi eventKpi);

    void viewEvent(ViewEvent viewEvent);
    SseEmitter subscribe() throws IOException;

    void handleSessionAction(SessionAction sessionAction);

    void handleQuizzAction(QuizzAction quizzAction);

}
