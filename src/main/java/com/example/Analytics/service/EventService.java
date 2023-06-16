package com.example.Analytics.service;

import com.example.Analytics.dto.DataToEmit;
import com.example.Analytics.dto.SessionAction;
import com.example.Analytics.models.*;
import com.example.Analytics.repository.EventKpiRepository;
import com.example.Analytics.repository.SessionRepository;
import com.example.Analytics.repository.ViewEventRepository;
import com.example.Analytics.repository.quizAction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class EventService implements EventKpService {

    private static List<SseEmitter> emitters = new java.util.ArrayList<>();

    private final EventKpiRepository eventKpiRepository;
    private final ViewEventRepository viewEventRepository;
    private final quizAction quizzActionRepository;
    private final SessionRepository sessionRepository;

    @Override
    public SseEmitter subscribe() throws IOException {
        SseEmitter sseEmitter = new SseEmitter(600000L);
        sseEmitter.send(SseEmitter.event()
                .name("message")
                .data("connexion"));
        sseEmitter.onCompletion(() -> this.emitters.remove(sseEmitter));
        this.emitters.add(sseEmitter);
        return sseEmitter;
    }

    public void emitData(String action,String data) {
        DataToEmit dataToEmit = DataToEmit.builder()
                .action(action)
                .data(data)
                .build();
        for (SseEmitter emitter : this.emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("message")
                        .data(dataToEmit));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void addKpi(EventKpi eventKpi) {
        eventKpi.setEventId(UUID.randomUUID());
        EventKpi eventKpi1 = eventKpiRepository.save(eventKpi);
        long countAll = eventKpiRepository.count();
        long countByUsername = eventKpiRepository.countByUserName("ilyes");
        System.out.println(countByUsername);
        this.emitData("addKpi",countAll+"");
    }

    @Override
    public void viewEvent(ViewEvent viewEvent) {
        long countAll = viewEventRepository.countAllByEventId(viewEvent.getEventId());
        this.emitData("viewEvent",countAll+"");
    }

    @Override
    public void handleViewAction(ViewEvent viewEvent){
        viewEventRepository.save(viewEvent);
    }

    @Override
    public void handleSessionAction(SessionAction sessionAction) {
        Session session = Session.builder()
                .id(UUID.randomUUID())
                .userName(sessionAction.getUserName())
                .roomId(sessionAction.getRoomId())
                .enterActionAt(LocalDateTime.now())
                .build();
        sessionRepository.save(session);

    }

    @Override
    public void handleClosingSession(SessionAction sessionAction) {
        Session session = sessionRepository.findAllByUserName(sessionAction.getUserName());
        session.setLeaveActionAt(LocalDateTime.now());
        sessionRepository.save(session);
        long minutes = session.getLeaveActionAt().getMinute() - session.getEnterActionAt().getMinute();
        long hours = session.getLeaveActionAt().getHour() - session.getEnterActionAt().getHour();
        this.emitData("sessionAction",hours+"h"+minutes+"m");
    }

    @Override
    public void countEventQuizzResponses(QuizzAction quizzAction) {
        long countAll = quizzActionRepository.countAllByEventId(quizzAction.getEventId());
        this.emitData("quizzAction",countAll+"");
    }

    @Override
    public void countQuizzByUser(String userName) {
        long countAll = quizzActionRepository.countAllByUserName(userName);
        this.emitData("quizzAction",countAll+"");
    }

    @Override
    public void countViewsByUser(String userName) {
        long countAll = viewEventRepository.countAllByUserName(userName);
        this.emitData("count By User",countAll+"");
    }

    @Override
    public void persistQuizz(QuizzAction quizzAction) {
        quizzActionRepository.save(quizzAction);
    }
}



