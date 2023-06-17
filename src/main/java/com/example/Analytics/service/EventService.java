package com.example.Analytics.service;

import com.example.Analytics.dto.CountEventViews;
import com.example.Analytics.dto.DataToEmit;
import com.example.Analytics.dto.SessionAction;
import com.example.Analytics.models.ViewEventAction;
import com.example.Analytics.models.*;
import com.example.Analytics.repository.EventKpiRepository;
import com.example.Analytics.repository.SessionRepository;
import com.example.Analytics.repository.ViewEventRepository;
import com.example.Analytics.repository.quizAction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Duration;
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
        eventKpiRepository.save(eventKpi);
        long countAll = eventKpiRepository.count();
        long countByUsername = eventKpiRepository.countByUserName(eventKpi.getUserName());
        this.emitData("addKpi",countAll+"");
        this.emitData("addKpiByUser",countByUsername+"");
    }

    @Override
    public long viewEvent(UUID viewEvent) {
        return  viewEventRepository.countAllByEventId(viewEvent);
    }

    @Override
    public ViewEventAction handleViewAction(ViewEventAction viewEventAction){
        viewEventAction.setSeenAt(LocalDateTime.now());
        ViewEventAction action = viewEventRepository.save(viewEventAction);
        this.emitData("Views per user",this.countViewsByUser(viewEventAction.getUserName()) + "");
        this.emitData("Views per event",this.viewEvent(viewEventAction.getEventId()) + "");
        return action;
    }

    @Override
    public Session handleSessionAction(SessionAction sessionAction) {
        Session existingSession = sessionRepository.findAllByUserNameAndRoomId(sessionAction.getUserName(),sessionAction.getRoomId());
        if(existingSession==null){
            Session session = Session.builder()
                    .id(UUID.randomUUID())
                    .userName(sessionAction.getUserName())
                    .roomId(sessionAction.getRoomId())
                    .enterActionAt(LocalDateTime.now())
                    .build();
            session.setDuration(Duration.ZERO);
            sessionRepository.save(session);
            return session;
        }
        else {
            existingSession.setEnterActionAt(LocalDateTime.now());
            sessionRepository.save(existingSession);
            return existingSession;
        }

    }

    @Override
    public Session handleClosingSession(SessionAction sessionAction) {
        Session session = sessionRepository.findAllByUserNameAndRoomId(sessionAction.getUserName(),sessionAction.getRoomId());
        session.setLeaveActionAt(LocalDateTime.now());
        session.setDuration(Duration.between(session.getEnterActionAt(),session.getLeaveActionAt()));
        sessionRepository.save(session);
        this.emitData("session duration",session.getDuration().toHours() + "" + session.getDuration().toMinutes() + "" + session.getDuration().toSeconds() + "");
        return session;
    }

    @Override
    public String getSessionDuration(String username, UUID roomId){
        Session session = sessionRepository.findAllByUserNameAndRoomId(username,roomId);
        return session.getDuration().toHours() +  "h"  +  session.getDuration().toMinutes() + "M" + session.getDuration().toSeconds() + "s";
    }

    @Override
    public long countEventQuizzResponses(UUID eventId) {
        return quizzActionRepository.countAllByEventId(eventId);
    }

    @Override
    public long countQuizzByUser(String userName) {
        return quizzActionRepository.countAllByUserName(userName);
    }

    @Override
    public long countViewsByUser(String userName) {
          return  viewEventRepository.countAllByUserName(userName);
    }

    @Override
    public void persistQuizz(QuizzAction quizzAction) {
        quizzAction.setPassedAt(LocalDateTime.now());
        quizzActionRepository.save(quizzAction);
        this.emitData("quizz count per event",this.countEventQuizzResponses(quizzAction.getEventId())+"");
        this.emitData(  "quizz count per user",this.countQuizzByUser(quizzAction.getUserName())+"");
    }

    @Override
    public long countEventsParticpatedAt(String uername) {
        long countAll = sessionRepository.countAllByUserName(uername);
        this.emitData("participants count",countAll+"");
        return countAll;
    }

    @Override
    public long getParticipantsNumber(){
        long totalParticipants = sessionRepository.count();
        this.emitData("participants count",totalParticipants+"");
        return totalParticipants;
    }

    @Override
    public long countParticipantsByRoomId(UUID roomId) {
        long countAll = sessionRepository.countAllByRoomId(roomId);
        this.emitData("participants count",countAll+"");
        return countAll;
    }


    @Override
    public CountEventViews getMaxViews() {
        List<CountEventViews> views = viewEventRepository.countMaxViews();
        return views.get(0);
    }

    @Override
    public CountEventViews getMinViews() {
        List<CountEventViews> views = viewEventRepository.countMinViews();
        return views.get(0);
    }

    @Override
    public Session getMaxDurationByRoomId(UUID roomId) {
        List<Session> sessions =  sessionRepository.getMaxDurationByRoomId(roomId);
        return sessions.get(0);
    }

    @Override
    public Session getMinDurationByRoomId(UUID roomId) {
        List<Session> sessions =  sessionRepository.getMinDurationByRoomId(roomId);
        return sessions.get(0);
    }

    @Override
    public Session getMaxSession() {
        List<Session> sessions = sessionRepository.getMaxDuration();
        return sessions.get(0);
    }

    @Override
    public Session getMinSession() {
        List<Session> sessions =  sessionRepository.getMinDuration();
        return sessions.get(0);
    }


    @Override
    public long MaximalParticipation(){
        List<Long> participations = sessionRepository.getMaximalParticipation();
        return participations.get(0);
    }

    @Override
    public long MinimalParticipation(){
        List<Long> participations = sessionRepository.getMinimalParticipation();
        return participations.get(0);
    }


}



