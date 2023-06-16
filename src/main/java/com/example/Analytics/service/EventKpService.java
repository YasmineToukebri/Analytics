package com.example.Analytics.service;

import com.example.Analytics.models.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;


public interface EventKpService {
    void addKpi(EventKpi eventKpi);

    void viewEvent(ViewEvent viewEvent);

    void joinRoom(JoinRoom joinRoom);

    void endMeeting(EndMeeting endMeeting);

    void sendQuiz(SendQuiz sendQuiz);

    void passQuiz(PassQuiz passQuiz);

    SseEmitter subscribe() throws IOException;

}
