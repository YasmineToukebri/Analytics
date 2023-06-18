package com.example.analytics.service;

import com.example.analytics.dto.CountEventViews;
import com.example.analytics.dto.DataToEmit;
import com.example.analytics.dto.SessionAction;
import com.example.analytics.models.EventKpi;
import com.example.analytics.models.Session;
import com.example.analytics.models.ViewEventAction;
import com.example.analytics.repository.EventKpiRepository;
import com.example.analytics.repository.SessionRepository;
import com.example.analytics.repository.ViewEventRepository;
import com.example.analytics.repository.QuizAction;
import com.example.analytics.models.QuizzAction;
import com.example.analytics.dto.Participation;
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
    private static final String PARTICIPANT_COUNT = "participants count";
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
    public ViewEventAction handleViewAction(ViewEventAction viewEventAction){
        viewEventAction.setSeenAt(LocalDateTime.now());
        ViewEventAction action = viewEventRepository.save(viewEventAction);
        this.emitData("Views per user",this.countViewsByUser(viewEventAction.getUserName()) + "");
        this.emitData("Views per event",this.viewEvent(viewEventAction.getEventId()) + "");
        this.emitData("Max Views number",this.getMaxViews()+"");
        this.emitData("Min Views number",this.getMinViews()+"");
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
            this.emitData("Maximal participation" , this.MaximalParticipation() + "");
            this.emitData("Minimal participation" , this.MinimalParticipation() + "");
            return session;
        }
        existingSession.setEnterActionAt(LocalDateTime.now());
        sessionRepository.save(existingSession);
        return existingSession;

    }

    @Override
    public Session handleClosingSession(SessionAction sessionAction) {
        Session session = sessionRepository.findAllByUserNameAndRoomId(sessionAction.getUserName(),sessionAction.getRoomId());
        session.setLeaveActionAt(LocalDateTime.now());
        session.setDuration(Duration.between(session.getEnterActionAt(),session.getLeaveActionAt()));
        sessionRepository.save(session);
        this.emitData("session duration",session.getDuration().toHours() + ""
                + session.getDuration().toMinutes()
                + "" + session.getDuration().toSeconds() + "");
        return session;
    }

    @Override
    public String getSessionDuration(String username, UUID roomId){
        Session session = sessionRepository.findAllByUserNameAndRoomId(username,roomId);
        return "user " + username + session.getDuration().toHours() +  "hours"  +  session.getDuration().toMinutes() + "Minutes" + session.getDuration().toSeconds() + "s";
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
        this.emitData(PARTICIPANT_COUNT,countAll+"");
        return countAll;
    }

    @Override
    public long getParticipantsNumber(){
        long totalParticipants = sessionRepository.count();
        this.emitData(PARTICIPANT_COUNT,totalParticipants+"");
        return totalParticipants;
    }

    @Override
    public long countParticipantsByRoomId(UUID roomId) {
        long countAll = sessionRepository.countAllByRoomId(roomId);
        this.emitData(PARTICIPANT_COUNT,countAll+"");
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
    public Participation maximalParticipation(){
        List<Participation> participations = sessionRepository.getMaximalParticipation();
        return participations.get(0);
    }

    @Override
    public Participation minimalParticipation(){
        List<Participation> participations = sessionRepository.getMinimalParticipation();
        return participations.get(0);
    }


}



