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
    public long viewEvent(UUID viewEvent) {
        long countAll = viewEventRepository.countAllByEventId(viewEvent);
        return countAll;
    }

    @Override
    public void handleViewAction(ViewEvent viewEvent){
        viewEvent.setSeenAt(LocalDateTime.now());
        viewEventRepository.save(viewEvent);
        this.emitData("Views per user",this.countViewsByUser(viewEvent.getUserName()) + "");
        this.emitData("Views per event",this.viewEvent(viewEvent.getEventId()) + "");
    }

    @Override
    public void handleSessionAction(SessionAction sessionAction) {
        Session session = Session.builder()
                .id(UUID.randomUUID())
                .userName(sessionAction.getUserName())
                .roomId(UUID.randomUUID())
                .enterActionAt(LocalDateTime.now())
                .build();
        if(sessionRepository.findAllByUserNameAndRoomId(sessionAction.getUserName(),sessionAction.getRoomId()).equals(null)){
            sessionRepository.save(session);
        }
        else {
            Session session1 = sessionRepository.findAllByUserNameAndRoomId(sessionAction.getUserName(),sessionAction.getRoomId());
            session1.setEnterActionAt(LocalDateTime.now());
            sessionRepository.save(session1);
        }

    }

    @Override
    public void handleClosingSession(SessionAction sessionAction) {
        UUID sessionId = sessionRepository.findAllByUserNameAndRoomId(sessionAction.getUserName(),sessionAction.getRoomId()).getId();
        Session session = sessionRepository.findById(sessionId).get();
        session.setLeaveActionAt(LocalDateTime.now());
        sessionRepository.save(session);

    }

    @Override
    public void getSessionDuration(String username){
        Session session = sessionRepository.findAllByUserName(username);
        long minutes = session.getLeaveActionAt().getMinute() - session.getEnterActionAt().getMinute();
        long hours = session.getLeaveActionAt().getHour() - session.getEnterActionAt().getHour();
        this.emitData("sessionAction",hours+"h"+minutes+"m");
    }

    @Override
    public long countEventQuizzResponses(UUID eventId) {
        long countAll = quizzActionRepository.countAllByEventId(eventId);
        return countAll;
    }

    @Override
    public long countQuizzByUser(String userName) {
        long countAll = quizzActionRepository.countAllByUserName(userName);
        return countAll;
    }

    @Override
    public long countViewsByUser(String userName) {
        long countAll = viewEventRepository.countAllByUserName(userName);
        return countAll;
    }

    @Override
    public void persistQuizz(QuizzAction quizzAction) {
        quizzAction.setPassedAt(LocalDateTime.now());
        quizzActionRepository.save(quizzAction);
        this.emitData("quizz count per event",this.countEventQuizzResponses(quizzAction.getEventId())+"");
        this.emitData(  "quizz count per user",this.countQuizzByUser(quizzAction.getUserName())+"");
    }

    @Override
    public void countParticipants(SessionAction sessionAction) {
        long countAll = sessionRepository.countAllByUsername(sessionAction.getUserName());
        this.emitData("participants count",countAll+"");
    }

    @Override
    public void countParticipantsByRoomId(SessionAction sessionAction) {
        long countAll = sessionRepository.countAllByRoomId(sessionAction.getRoomId());
        this.emitData("participants count",countAll+"");
    }
}



