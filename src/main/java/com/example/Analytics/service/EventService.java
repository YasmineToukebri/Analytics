package com.example.Analytics.service;

import com.example.Analytics.dto.DataToEmit;
import com.example.Analytics.dto.SessionAction;
import com.example.Analytics.models.*;
import com.example.Analytics.repository.*;
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

    private final AbortEventRepository abortEventRepository;
    private final ViewEventRepository viewEventRepository;
    private final quizAction quizzActionRepository;
    private final SessionRepository sessionRepository;



    @Override
    public String getUsernameWithMostEvents() {
        List<String> findUsernameWithMostEvents= eventKpiRepository.findUsernameWithMostEvents();
        return findUsernameWithMostEvents.get(0);
    }

    @Override
    public String  findUsernameWithLeastEvents() {
        List<String> usernameWithLeastEvents=eventKpiRepository.findUsernameWithLeastEvents();
        return usernameWithLeastEvents.get(0);
    }

    @Override
    public double calculateAverageEventsPerUser() {
        return eventKpiRepository.calculateAverageEventsPerUser();
    }



    @Override
    public long countEventQuizzResponses(UUID eventId) {
        long countAll = quizzActionRepository.countAllByEventId(eventId);
        this.emitData("quizzAction",countAll+"");
        return countAll;
    }


    @Override
    public void abortEvent(AbortEvent abortEvent) {
         abortEventRepository.save(abortEvent);
    }

    @Override
    public void addKpi(EventKpi eventKpi) {
        eventKpi.setEventId(UUID.randomUUID());
        EventKpi eventKpi1 = eventKpiRepository.save(eventKpi);

        String usernameWithMostEvents= getUsernameWithMostEvents();
        this.emitData("findUsernameWithMostEvents",usernameWithMostEvents);


        String usernameWithLeastEvents= findUsernameWithLeastEvents();
        this.emitData("findUsernameWithLeastEvents",usernameWithLeastEvents);


        double calculateAverageEventsPerUser= calculateAverageEventsPerUser();
        this.emitData("findUsernameWithLeastEvents",usernameWithLeastEvents+"");





        long countAll = eventKpiRepository.count();
        this.emitData("addKpi",countAll+"");


        long countEventAborted = abortEventRepository.count();
        this.emitData("addKpi",countEventAborted+"");


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
    public SseEmitter subscribe() throws IOException {
        SseEmitter sseEmitter = new SseEmitter(600000L);
        sseEmitter.send(SseEmitter.event()
                .name("message")
                .data("connexion"));
        sseEmitter.onCompletion(() -> this.emitters.remove(sseEmitter));
        this.emitters.add(sseEmitter);
        return sseEmitter;
    }













    @Override
    public long viewEvent(UUID viewEvent) {
        long countAll = viewEventRepository.countAllByEventId(viewEvent);
        this.emitData("viewEvent",countAll+"");
        return countAll;
    }

    @Override
    public void handleViewAction(ViewEvent viewEvent){
        viewEvent.setSeenAt(LocalDateTime.now());
        viewEventRepository.save(viewEvent);
        this.emitData("viewEvent",this.countViewsByUser(viewEvent.getUserName()) + "");
        this.emitData("viewEvent",this.viewEvent(viewEvent.getEventId()) + "");
    }

    @Override
    public void handleSessionAction(SessionAction sessionAction) {
        Session session = Session.builder()
                .id(UUID.randomUUID())
                .userName(sessionAction.getUserName())
                .roomId(UUID.randomUUID())
                .enterActionAt(LocalDateTime.now())
                .build();
        if(sessionRepository.findAllByUserName(sessionAction.getUserName()) == null && sessionRepository.findAllByRoomId(sessionAction.getRoomId()) == null){
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
    public long getSessionDuration(String username){
        Session session = sessionRepository.findAllByUserName(username);
        long minutes = session.getLeaveActionAt().getMinute() - session.getEnterActionAt().getMinute();
        long hours = session.getLeaveActionAt().getHour() - session.getEnterActionAt().getHour();
        this.emitData("sessionAction",hours+"h"+minutes+"m");
        return minutes;
    }


    @Override
    public long countQuizzByUser(String userName) {
        long countAll = quizzActionRepository.countAllByUserName(userName);
        this.emitData("quizzAction",countAll+"");
        return countAll;
    }

    @Override
    public long countViewsByUser(String userName) {
        long countAll = viewEventRepository.countAllByUserName(userName);
        this.emitData("count By User",countAll+"");
        return countAll;
    }

    @Override
    public void persistQuizz(QuizzAction quizzAction) {
        quizzAction.setPassedAt(LocalDateTime.now());
        quizzActionRepository.save(quizzAction);
        this.countEventQuizzResponses(quizzAction.getEventId());
        this.emitData(  "quizzAction",this.countQuizzByUser(quizzAction.getUserName())+"");

    }
}



