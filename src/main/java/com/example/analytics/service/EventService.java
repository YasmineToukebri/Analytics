package com.example.analytics.service;

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

    private static final List<SseEmitter> emitters = new java.util.ArrayList<>();
//    private static final String PARTICIPANT_COUNT = "participants count";
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
    public ViewEventAction handleViewAction(ViewEventAction viewEventAction) throws JsonProcessingException {
        viewEventAction.setSeenAt(LocalDateTime.now());
        ViewEventAction action = viewEventRepository.save(viewEventAction);
        JsonMapper jsonMapper = new JsonMapper();
        jsonMapper.registerModule(new JavaTimeModule());
        ObjectWriter ow = new ObjectMapper().registerModule(new JavaTimeModule()).writer().withDefaultPrettyPrinter();
        String max_views = ow.writeValueAsString(this.getMaxViews());
        String min_views = ow.writeValueAsString(this.getMinViews());
        String user_total_views = ow.writeValueAsString(this.countViewsByUser(viewEventAction.getUserName()));
        this.emitData("Views per user",user_total_views);
//        this.emitData("Views per event",this.viewEvent(viewEventAction.getEventId()) + ""+ "event :" + viewEventAction.getEventId());
        this.emitData("Max Views number",max_views);
        this.emitData("Min Views number",min_views);
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
    public Session handleClosingSession(SessionAction sessionAction) throws JsonProcessingException {
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
        MaxMinSession max_session = this.getMaxSession();
        MaxMinSession min_session = this.getMinSession();
        String session_duration = ow.writeValueAsString(this.getLastSessionDuration(sessionAction.getUserName()));
        String maximal_session_duration = ow.writeValueAsString(max_session);
        String minimal_session_duration = ow.writeValueAsString(min_session);
        String total_participation = ow.writeValueAsString(this.getParticipantsNumber());
        String user_participation = ow.writeValueAsString(this.countEventsParticpatedAt(sessionAction.getUserName()));
        String maximal_participation = ow.writeValueAsString(this.maximalParticipation());
        String minimal_participation = ow.writeValueAsString(this.minimalParticipation());
        this.emitData("session duration",session_duration);
        this.emitData("Maximal session duration" , maximal_session_duration);
        this.emitData("Minimal session duration" , minimal_session_duration);
        this.emitData("total participation" , total_participation);
//        this.emitData("this event participation" , this.countParticipantsByRoomId(sessionAction.getRoomId()) + "");
        this.emitData("this user participation" ,user_participation);
        this.emitData("maximal participation" , maximal_participation);
        this.emitData("minimal participation" , minimal_participation);
        return session;
    }

    @Override
    public MaxMinSession getSessionDuration(String username, UUID roomId){
        Session user_session = sessionRepository.findAllByUserNameAndRoomId(username,roomId);
        MaxMinSession maxMinSession = MaxMinSession.builder()
                .duration(user_session.getDuration().toHours()+"hours " + user_session.getDuration().toMinutes()+"minutes " + user_session.getDuration().toSeconds()+"seconds ")
                .build();
        return maxMinSession;
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
        this.emitData("quizz count per event: ",this.countEventQuizzResponses(quizzAction.getEventId())+" " + "event :" + quizzAction.getEventId());
        this.emitData(  "quizz count per user:",this.countQuizzByUser(quizzAction.getUserName())+" " + "user :" + quizzAction.getUserName());
    }

    @Override
    public long countEventsParticpatedAt(String uername) {
        long countAll = sessionRepository.countAllByUserName(uername);
        return countAll;
    }

    @Override
    public long getParticipantsNumber(){
        long totalParticipants = sessionRepository.count();
        return totalParticipants;
    }

    @Override
    public long countParticipantsByRoomId(UUID roomId) {
        long countAll = sessionRepository.countAllByRoomId(roomId);
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
    public MaxMinSession getMaxSession() {
        List<Session> sessions = sessionRepository.getMaxDuration();
        Session max_session = sessions.get(0);
        MaxMinSession maxSession = MaxMinSession.builder().userName(max_session.getUserName())
                        .roomId(max_session.getRoomId())
                        .duration(max_session.getDuration().toHours()+"hours" + max_session.getDuration().toMinutes()+"minutes" + max_session.getDuration().toSeconds()+"seconds")
                        .build();
        return maxSession;
    }

    @Override
    public MaxMinSession getMinSession() {
        List<Session> sessions =  sessionRepository.getMinDuration();
        Session min_session = sessions.get(0);
        MaxMinSession minSession = MaxMinSession.builder()
                .userName(min_session.getUserName())
                .duration(min_session.getDuration().toHours()+"hours" + min_session.getDuration().toMinutes()+"minutes" + min_session.getDuration().toSeconds()+"seconds")
                .build();
        return minSession;
    }


    @Override
    public Participation maximalParticipation(){
        List<Participation> participations = sessionRepository.getMaximalParticipation();
        return participations.get(0);
    }

    @Override
    public Participation minimalParticipation(){
        List<Participation> participations = sessionRepository.getMinimalParticipation();
        return participations.get(0);
    }


    @Override
    public MaxMinSession getLastSessionDuration(String username){
        List<Session> user_sessions = sessionRepository.getSessionsByUserName(username);
        Session user_last_session = user_sessions.get(0);
        MaxMinSession maxMinSession = MaxMinSession.builder()
                .duration(user_last_session.getDuration().toHours()+"hours " + user_last_session.getDuration().toMinutes()+"minutes " + user_last_session.getDuration().toSeconds()+"seconds ")
                .build();
        return maxMinSession;
    }

}



