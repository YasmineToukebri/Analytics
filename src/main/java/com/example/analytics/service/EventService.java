package com.example.analytics.service;


import com.example.analytics.exception.EmptyListException;
import com.example.analytics.dto.*;
import com.example.analytics.models.*;
import com.example.analytics.models.ViewEventAction;
import com.example.analytics.repository.AbortEventRepository;
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
    private final AbortEventRepository abortEventRepository;
    private final SessionRepository sessionRepository;

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
        abortEvent.setAbortedAt(LocalDateTime.now());
        abortEventRepository.save(abortEvent);

        String findUserWithLeastAbortedEvents= findUserWithLeastAbortedEvents();
        this.emitData("findUserWithLeastAbortedEvents",findUserWithLeastAbortedEvents);

        String findUserWithMostAbortedEvents= findUserWithMostAbortedEvents();
        this.emitData("findUserWithMostAbortedEvents",findUserWithMostAbortedEvents);

        long countEventsAborted = abortEventRepository.count();
        this.emitData("abortEvent",countEventsAborted+"");


        long countTotalAbortedEventToday = abortEventRepository.findTotalAbortedEventToday();
        this.emitData("findTotalAbortedEventToday",countTotalAbortedEventToday+"");


        long countTotalAbortedEventByCurrentWeek = abortEventRepository.findTotalAbortedEventByCurrentWeek();
        this.emitData("findTotalAbortedEventByCurrentWeek",countTotalAbortedEventByCurrentWeek+"");

        long countTotalAbortedEventByCurrentMonth = abortEventRepository.findTotalAbortedEventByCurrentMonth();
        this.emitData("findTotalAbortedEventByCurrentMonth",countTotalAbortedEventByCurrentMonth+"");

        double calculateAverageAbortedEventsPerUser = abortEventRepository.calculateAverageAbortedEventsPerUser();
        this.emitData("calculateAverageAbortedEventsPerUser",calculateAverageAbortedEventsPerUser+"");
    }

    @Override
    public String findUserWithLeastAbortedEvents() {
        List<String> findUserWithLeastAbortedEvents=abortEventRepository.findUserWithLeastAbortedEvents();
        return findUserWithLeastAbortedEvents.get(0);
    }

    @Override
    public String findUserWithMostAbortedEvents() {
        List<String> findUserWithMostAbortedEvents=abortEventRepository.findUserWithMostAbortedEvents();
        return findUserWithMostAbortedEvents.get(0);
    }

    @Override
    public Long findTotalAbortedEventToday() {
        return abortEventRepository.findTotalAbortedEventToday();
    }

    @Override
    public Long findTotalAbortedEventByCurrentWeek() {
        return abortEventRepository.findTotalAbortedEventByCurrentWeek();
    }

    @Override
    public Long findTotalAbortedEventByCurrentMonth() {
        return abortEventRepository.findTotalAbortedEventByCurrentMonth();
    }

    @Override
    public double calculateAverageAbortedEventsPerUser() {
        return abortEventRepository.calculateAverageAbortedEventsPerUser();
    }

    @Override
    public void addKpi(EventKpi eventKpi) {
        eventKpi.setEventId(UUID.randomUUID());
        eventKpi.setActionAt(LocalDateTime.now());
        EventKpi eventKpi1 = eventKpiRepository.save(eventKpi);

        long totalEventsByUsername = countAllEventsByUserName(eventKpi.getUserName());
        this.emitData("totalEventsByUsername",totalEventsByUsername+"");

        String usernameWithMostEvents= getUsernameWithMostEvents();
        this.emitData("findUsernameWithMostEvents",usernameWithMostEvents);


        String usernameWithLeastEvents= findUsernameWithLeastEvents();
        this.emitData("findUsernameWithLeastEvents",usernameWithLeastEvents);


        double calculateAverageEventsPerUser= calculateAverageEventsPerUser();
        this.emitData("calculateAverageEventsPerUser",calculateAverageEventsPerUser+"");

        long totalEventsThisDay= findTotalByToday();
        this.emitData("totalEventsThisDay",totalEventsThisDay+"");

        long totalEventsThisWeek = findTotalByCurrentWeek();
        this.emitData("totalEventsThisWeek",totalEventsThisWeek+"");

        long totalEventsThisMonth = findTotalByCurrentMonth();
        this.emitData("totalEventsThisMonth",totalEventsThisMonth+"");



        long findTotalByTodayAndUserName=findTotalByTodayAndUserName(eventKpi.getUserName());
        this.emitData("findTotalByTodayAndUserName",findTotalByTodayAndUserName+"");

        long findTotalByCurrentWeekAndUserName=findTotalByCurrentWeekAndUserName(eventKpi.getUserName());
        this.emitData("findTotalByCurrentWeekAndUserName",findTotalByCurrentWeekAndUserName+"");

        long findTotalByCurrentMonthAndUserName=findTotalByCurrentMonthAndUserName(eventKpi.getUserName());
        this.emitData("findTotalByCurrentMonthAndUserName",findTotalByCurrentMonthAndUserName+"");



        long countAll = eventKpiRepository.count();
        this.emitData("addKpi",countAll+"");

    }

    @Override
    public long countALlEvents() {
        return eventKpiRepository.count();
    }

    @Override
    public long countAllEventsByUserName(String userName) {
        return eventKpiRepository.countByUserName(userName);
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
    public void persistQuizz(QuizzAction quizzAction) {
        quizzAction.setPassedAt(LocalDateTime.now());
        quizzActionRepository.save(quizzAction);
        this.countEventQuizzResponses(quizzAction.getEventId());
        this.emitData(  "quizzAction",this.countQuizzByUser(quizzAction.getUserName())+"");

    }

    @Override
    public Long findTotalByToday() {
        return eventKpiRepository.findTotalByToday();
    }

    @Override
    public Long findTotalByCurrentWeek() {
        return eventKpiRepository.findTotalByCurrentWeek();
    }

    @Override
    public Long findTotalByCurrentMonth() {
        return eventKpiRepository.findTotalByCurrentMonth();
    }

    @Override
    public Long findTotalByTodayAndUserName(String userName) {
        return eventKpiRepository.findTotalByUsernameToday(userName);
    }

    @Override
    public Long findTotalByCurrentWeekAndUserName(String userName) {
        return eventKpiRepository.findTotalByUserNameAndCurrentWeek(userName);
    }

    @Override
    public Long findTotalByCurrentMonthAndUserName(String userName) {
        return eventKpiRepository.findTotalByUserNameAndCurrentMonth(userName);
    }



    private <T> void checkNullList(Predicate<List<T>> predicate, String message, List<T> list) {
        if(predicate.test(list)) {
            throw new EmptyListException(message);
        }
    }

}



