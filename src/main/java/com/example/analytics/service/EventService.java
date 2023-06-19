package com.example.analytics.service;

import com.example.analytics.Exception.emptyListException;
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


@RequiredArgsConstructor
@Service
public class EventService implements EventKpService {


    enum Action {
        viewsPerUser,
        maxViewsNumber,
        minViewsNumber,
        sessionDuration,
        maximalSessionDuration,
        minimalSessionDuration,
        totalParticipation,
        currentUserParticipation,
        maximalParticipation,
        minimalParticipation,
        countQuizzPerEvent,
        countQuizzPerUser,


    }
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

    public void emitData(String action,String data) {
        DataToEmit dataToEmit = DataToEmit.builder()
                .action(action)
                .data(data)
                .build();
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
    public ViewEventAction handleViewAction(ViewEventAction viewEventAction) throws JsonProcessingException, emptyListException {
        viewEventAction.setSeenAt(LocalDateTime.now());
        ViewEventAction action = viewEventRepository.save(viewEventAction);
        JsonMapper jsonMapper = new JsonMapper();
        jsonMapper.registerModule(new JavaTimeModule());
        ObjectWriter ow = new ObjectMapper().registerModule(new JavaTimeModule()).writer().withDefaultPrettyPrinter();
        String maxViews = ow.writeValueAsString(this.getMaxViews());
        String minViews = ow.writeValueAsString(this.getMinViews());
        String userTotalViews = ow.writeValueAsString(this.countViewsByUser(viewEventAction.getUserName()));
        this.emitData(String.valueOf(Action.viewsPerUser),userTotalViews);
        this.emitData(String.valueOf(Action.maxViewsNumber),maxViews);
        this.emitData(String.valueOf(Action.minViewsNumber),minViews);
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
    public Session handleClosingSession(SessionAction sessionAction) throws JsonProcessingException, emptyListException {
        Session session = sessionRepository.findAllByUserNameAndRoomId(sessionAction.getUserName(),sessionAction.getRoomId());
        session.setLeaveActionAt(LocalDateTime.now());
        session.setDuration(Duration.between(session.getEnterActionAt(),session.getLeaveActionAt()));
        sessionRepository.save(session);
        JsonMapper jsonMapper = new JsonMapper();
        jsonMapper.registerModule(new JavaTimeModule());
        ObjectWriter ow = new ObjectMapper().registerModule(new JavaTimeModule()).writer().withDefaultPrettyPrinter();
        MaxMinSession last_user_session = MaxMinSession.builder().userName(session.getUserName())
                .roomId(session.getRoomId())
                .duration(session.getDuration().toHours() + "hours " + session.getDuration().toMinutes()+"minutes " + session.getDuration().toSeconds() + "seconds")
                .build();
        MaxMinSession maxSession = this.getMaxSession();
        MaxMinSession minSession = this.getMinSession();
        String sessionDuration = ow.writeValueAsString(this.getLastSessionDuration(sessionAction.getUserName()));
        String maximalSessionDuration = ow.writeValueAsString(maxSession);
        String minimalSessionDuration = ow.writeValueAsString(minSession);
        String totalParticipation = ow.writeValueAsString(this.getParticipantsNumber());
        String userParticipation = ow.writeValueAsString(this.countEventsParticpatedAt(sessionAction.getUserName()));
        String maximalParticipation = ow.writeValueAsString(this.maximalParticipation());
        String minimalParticipation = ow.writeValueAsString(this.minimalParticipation());
        this.emitData(String.valueOf(Action.sessionDuration),sessionDuration);
        this.emitData(String.valueOf(Action.maximalSessionDuration) , maximalSessionDuration);
        this.emitData(String.valueOf(Action.minimalSessionDuration) , minimalSessionDuration);
        this.emitData(String.valueOf(Action.totalParticipation), totalParticipation);
        this.emitData(String.valueOf(Action.currentUserParticipation) ,userParticipation);
        this.emitData(String.valueOf(Action.maximalParticipation), maximalParticipation);
        this.emitData(String.valueOf(Action.minimalParticipation), minimalParticipation);
        return session;
    }

    @Override
    public MaxMinSession getSessionDuration(String username, UUID roomId) throws emptyListException {
        Session userSession = sessionRepository.findAllByUserNameAndRoomId(username,roomId);
        if(userSession==null){
            throw new emptyListException("no session found for the user " + username + " in the room " + roomId);
        }
        MaxMinSession useSession = MaxMinSession.builder()
                .duration(userSession.getDuration().toHours()+"hours " + userSession.getDuration().toMinutes()+"minutes " + userSession.getDuration().toSeconds()+"seconds ")
                .build();
        return useSession;
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
        this.emitData(String.valueOf(Action.countQuizzPerEvent),this.countEventQuizzResponses(quizzAction.getEventId())+" " + "event :" + quizzAction.getEventId());
        this.emitData(  String.valueOf(Action.countQuizzPerUser),this.countQuizzByUser(quizzAction.getUserName())+" " + "user :" + quizzAction.getUserName());
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
    public CountEventViews getMaxViews() throws emptyListException {
        List<CountEventViews> views = viewEventRepository.countMaxViews();
        if(views.isEmpty() || views==null){
            throw new emptyListException("No Max views found");
        }
        return views.get(0);
    }

    @Override
    public CountEventViews getMinViews() throws emptyListException {
        List<CountEventViews> views = viewEventRepository.countMinViews();
        if (views.isEmpty() || views==null){
            throw new emptyListException("No Min views found");
        }
        return views.get(0);
    }

    @Override
    public Session getMaxDurationByRoomId(UUID roomId) throws emptyListException {
        List<Session> sessions =  sessionRepository.getMaxDurationByRoomId(roomId);
        if (sessions.isEmpty() || sessions==null){
            throw new emptyListException("No Maximal session found for the event : "+ roomId);
        }
        return sessions.get(0);
    }

    @Override
    public Session getMinDurationByRoomId(UUID roomId) throws emptyListException {
        List<Session> sessions =  sessionRepository.getMinDurationByRoomId(roomId);
        if (sessions.isEmpty() || sessions==null){
            throw new emptyListException("No Minimal session found for the event : " + roomId);
        }
        return sessions.get(0);
    }

    @Override
    public MaxMinSession getMaxSession() throws emptyListException {
        List<Session> sessions = sessionRepository.getMaxDuration();
        if (sessions.isEmpty() || sessions==null){
            throw new emptyListException("No Maximal session found");
        }
        Session maxSession = sessions.get(0);
        MaxMinSession maximalSession = MaxMinSession.builder().userName(maxSession.getUserName())
                        .roomId(maxSession.getRoomId())
                        .duration(maxSession.getDuration().toHours()+"hours" + maxSession.getDuration().toMinutes()+"minutes" + maxSession.getDuration().toSeconds()+"seconds")
                        .build();
        return maximalSession;
    }

    @Override
    public MaxMinSession getMinSession() throws emptyListException {
        List<Session> sessions =  sessionRepository.getMinDuration();
        if (sessions.isEmpty() || sessions==null){
            throw new emptyListException("No Maximal session found");
        }
        Session minSession = sessions.get(0);
        MaxMinSession minimalSession = MaxMinSession.builder()
                .userName(minSession.getUserName())
                .roomId(minSession.getRoomId())
                .duration(minSession.getDuration().toHours()+"hours" + minSession.getDuration().toMinutes()+"minutes" + minSession.getDuration().toSeconds()+"seconds")
                .build();
        return minimalSession;
    }


    @Override
    public Participation maximalParticipation() throws emptyListException {
        List<Participation> participations = sessionRepository.getMaximalParticipation();
        if (participations.isEmpty() || participations==null)
            throw new emptyListException("No Maximal participation found");
        return participations.get(0);
    }

    @Override
    public Participation minimalParticipation() throws emptyListException {
        List<Participation> participations = sessionRepository.getMinimalParticipation();
        if (participations.isEmpty() || participations==null)
            throw new emptyListException("No Minimal participation found");
        return participations.get(0);
    }


    @Override
    public MaxMinSession getLastSessionDuration(String username) throws emptyListException {
        List<Session> userSessions = sessionRepository.getSessionsByUserName(username);
        if (userSessions.isEmpty() || userSessions==null)
            throw new emptyListException("No sessions found for the user : " + username);
        Session userLastSession = userSessions.get(0);
        MaxMinSession userSession = MaxMinSession.builder()
                .duration(userLastSession.getDuration().toHours()+"hours " + userLastSession.getDuration().toMinutes()+"minutes " + userLastSession.getDuration().toSeconds()+"seconds ")
                .build();
        return userSession;
    }

}



