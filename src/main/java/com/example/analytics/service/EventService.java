package com.example.analytics.service;

import com.example.analytics.exception.EmptyListException;
import com.example.analytics.dto.*;
import com.example.analytics.models.EventKpi;
import com.example.analytics.models.QuizzAction;
import com.example.analytics.models.Session;
import com.example.analytics.models.ViewEventAction;
import com.example.analytics.repository.EventKpiRepository;
import com.example.analytics.repository.QuizAction;
import com.example.analytics.repository.SessionRepository;
import com.example.analytics.repository.ViewEventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;


@RequiredArgsConstructor
@Service
public class EventService implements EventKpService {

    private static final List<SseEmitter> emitters = new java.util.ArrayList<>();
    private final EventKpiRepository eventKpiRepository;
    private final ViewEventRepository viewEventRepository;
    private final QuizAction quizzActionRepository;
    private final SessionRepository sessionRepository;

    @Override
    public SseEmitter subscribe() throws IOException {
        SseEmitter sseEmitter = new SseEmitter(600000L);
        sseEmitter.send(SseEmitter.event()
                .name("message")
                .data("connexion"));
        sseEmitter.onCompletion(() -> emitters.remove(sseEmitter));
        emitters.add(sseEmitter);
        return sseEmitter;
    }

    public void emitData(DataToEmit dataToEmit) {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("message")
                        .data(dataToEmit));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void emitMultipleData(List<DataToEmit> dataToEmits) {
        dataToEmits.forEach(this::emitData);
    }

    @Override
    public void addKpi(EventKpi eventKpi) {
        eventKpi.setEventId(UUID.randomUUID());
        eventKpiRepository.save(eventKpi);
        eventKpiRepository.count();
        eventKpiRepository.countByUserName(eventKpi.getUserName());
    }

    @Override
    public long viewEvent(UUID viewEvent) {
        return  viewEventRepository.countAllByEventId(viewEvent);
    }

    @Override
    public ViewEventAction handleViewAction(ViewEventAction viewEventAction) throws JsonProcessingException, EmptyListException {
        viewEventAction.setSeenAt(LocalDateTime.now());
        ViewEventAction action = viewEventRepository.save(viewEventAction);
        JsonMapper jsonMapper = new JsonMapper();
        jsonMapper.registerModule(new JavaTimeModule());
        ObjectWriter ow = new ObjectMapper().registerModule(new JavaTimeModule()).writer().withDefaultPrettyPrinter();
        String maxViews = ow.writeValueAsString(this.getMaxViews());
        String minViews = ow.writeValueAsString(this.getMinViews());
        String userTotalViews = ow.writeValueAsString(this.countViewsByUser(viewEventAction.getUserName()));

        List<DataToEmit> dataToEmits = List.of(
                DataToEmit.builder().action(EAction.viewsPerUser.toString()).data(userTotalViews).build(),
                DataToEmit.builder().action(EAction.maxViewsNumber.toString()).data(maxViews).build(),
                DataToEmit.builder().action(EAction.minViewsNumber.toString()).data(minViews).build()
        );
        this.emitMultipleData(dataToEmits);
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
        existingSession.setEnterActionAt(LocalDateTime.now());
        sessionRepository.save(existingSession);
        return existingSession;

    }

    @Override
    public Session handleClosingSession(SessionAction sessionAction) throws JsonProcessingException, EmptyListException, NullPointerException {
        Session session = sessionRepository.findAllByUserNameAndRoomId(sessionAction.getUserName(),sessionAction.getRoomId());
        if(session==null){
            throw new EmptyListException("No session found for the user " + sessionAction.getUserName() + " in the room " + sessionAction.getRoomId());
        }
        session.setLeaveActionAt(LocalDateTime.now());
        session.setDuration(Duration.between(session.getEnterActionAt(),session.getLeaveActionAt()));
        sessionRepository.save(session);
        JsonMapper jsonMapper = new JsonMapper();
        jsonMapper.registerModule(new JavaTimeModule());
        ObjectWriter ow = new ObjectMapper().registerModule(new JavaTimeModule()).writer().withDefaultPrettyPrinter();

        //TODO : create query sql to retrieve all data
        String totalParticipation = String.valueOf(this.getParticipantsNumber());
        String userParticipation = String.valueOf(this.countEventsParticpatedAt(sessionAction.getUserName()));

        String sessionDuration = ow.writeValueAsString(this.getLastSessionDuration(sessionAction.getUserName()));
        String maximalSessionDuration = ow.writeValueAsString(this.getMaxSession());
        String minimalSessionDuration = ow.writeValueAsString(this.getMinSession());
        String maximalParticipation = ow.writeValueAsString(this.maximalParticipation());
        String minimalParticipation = ow.writeValueAsString(this.minimalParticipation());

        List<DataToEmit> dataToEmits = List.of(
                DataToEmit.builder().action(EAction.sessionDuration.toString()).data(sessionDuration).build(),
                DataToEmit.builder().action(EAction.maximalSessionDuration.toString()).data(maximalSessionDuration).build(),
                DataToEmit.builder().action(EAction.minimalSessionDuration.toString()).data(minimalSessionDuration).build(),
                DataToEmit.builder().action(EAction.totalParticipation.toString()).data(totalParticipation).build(),
                DataToEmit.builder().action(EAction.currentUserParticipation.toString()).data(userParticipation).build(),
                DataToEmit.builder().action(EAction.maximalParticipation.toString()).data(maximalParticipation).build(),
                DataToEmit.builder().action(EAction.minimalParticipation.toString()).data(minimalParticipation).build()
        );
        this.emitMultipleData(dataToEmits);
        return session;
    }

    @Override
    public MaxMinSession getSessionDuration(String username, UUID roomId) {
        Session userSession = sessionRepository.findAllByUserNameAndRoomId(username,roomId);
        if(userSession==null){
            throw new EmptyListException("No session found for the user " + username + " in the room " + roomId);
        }
        return MaxMinSession.builder()
                .roomId(userSession.getRoomId())
                .duration(userSession.getDuration().toHours()+"hours " + userSession.getDuration().toMinutes()+"minutes " + userSession.getDuration().toSeconds()+"seconds ")
                .build();
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
        List<DataToEmit> dataToEmits = List.of(
                DataToEmit.builder().action(EAction.countQuizzPerEvent.toString()).data(this.countEventQuizzResponses(quizzAction.getEventId())+" " + "event :" + quizzAction.getEventId()).build(),
                DataToEmit.builder().action(EAction.countQuizzPerUser.toString()).data(this.countQuizzByUser(quizzAction.getUserName())+" " + "user :" + quizzAction.getUserName()).build()
        );
        this.emitMultipleData(dataToEmits);
    }

    @Override
    public long countEventsParticpatedAt(String uerName) {
       return sessionRepository.countAllByUserName(uerName);
    }

    @Override
    public long getParticipantsNumber(){
        return sessionRepository.count();
    }

    @Override
    public long countParticipantsByRoomId(UUID roomId) {
        return sessionRepository.countAllByRoomId(roomId);
    }


    @Override
    public CountEventViews getMaxViews() {
        List<CountEventViews> views = viewEventRepository.countMaxViews();
        checkNullList(e -> e == null || e.isEmpty(), "No Max views found", views);

        return views.get(0);
    }

    @Override
    public CountEventViews getMinViews() {
        List<CountEventViews> views = viewEventRepository.countMinViews();
        checkNullList(e -> e == null || e.isEmpty(), "No Min views found", views);

        return views.get(0);
    }

    @Override
    public Session getMaxDurationByRoomId(UUID roomId) {
        List<Session> sessions =  sessionRepository.getMaxDurationByRoomId(roomId);
        checkNullList(e -> e == null || e.isEmpty(), "No Maximal session found for the event : " + roomId, sessions);

        return sessions.get(0);
    }

    @Override
    public Session getMinDurationByRoomId(UUID roomId) {
        List<Session> sessions =  sessionRepository.getMinDurationByRoomId(roomId);
        checkNullList(e -> e == null || e.isEmpty(), "No Minimal session found for the event : " + roomId, sessions);
        return sessions.get(0);
    }

    @Override
    public MaxMinSession getMaxSession() {
        List<Session> sessions = sessionRepository.getMaxDuration();
        checkNullList(e -> e == null || e.isEmpty(), "No Maximal session found", sessions);
        Session maxSession = sessions.get(0);
        return MaxMinSession.builder()
                .userName(maxSession.getUserName())
                .roomId(maxSession.getRoomId())
                .duration(maxSession.getDuration().toHours()+"hours" + maxSession.getDuration().toMinutes()+"minutes" + maxSession.getDuration().toSeconds()+"seconds")
                .build();
    }

    @Override
    public MaxMinSession getMinSession() {
        List<Session> sessions =  sessionRepository.getMinDuration();
        checkNullList(e -> e == null || e.isEmpty(), "No Minimal session found", sessions);
        Session minSession = sessions.get(0);
        return MaxMinSession.builder()
                .userName(minSession.getUserName())
                .roomId(minSession.getRoomId())
                .duration(minSession.getDuration().toHours()+"hours" + minSession.getDuration().toMinutes()+"minutes" + minSession.getDuration().toSeconds()+"seconds")
                .build();
    }


    @Override
    public Participation maximalParticipation() {
        List<Participation> participations = sessionRepository.getMaximalParticipation();
        checkNullList(e -> e == null || e.isEmpty(), "No Maximal participation found", participations);
        return participations.get(0);
    }

    @Override
    public Participation minimalParticipation() {
        List<Participation> participations = sessionRepository.getMinimalParticipation();
        checkNullList(e -> e == null || e.isEmpty(), "No Minimal participation found", participations);
        return participations.get(0);
    }


    @Override
    public MaxMinSession getLastSessionDuration(String username) {
        List<Session> userSessions = sessionRepository.getSessionsByUserName(username);
        checkNullList(e -> e == null || e.isEmpty(), "No sessions found for the user : " + username, userSessions);
        Session userLastSession = userSessions.get(0);
        return MaxMinSession.builder()
                .roomId(userLastSession.getRoomId())
                .duration(userLastSession.getDuration().toHours()+"hours " + userLastSession.getDuration().toMinutes()+"minutes " + userLastSession.getDuration().toSeconds()+"seconds ")
                .build();
    }

    private <T> void checkNullList(Predicate<List<T>> predicate, String message, List<T> list) {
        if(predicate.test(list)) {
            throw new EmptyListException(message);
        }
    }

}



