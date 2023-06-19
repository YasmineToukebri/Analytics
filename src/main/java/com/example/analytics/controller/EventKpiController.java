package com.example.analytics.controller;

import com.example.analytics.exception.EmptyListException;
import com.example.analytics.dto.CountEventViews;
import com.example.analytics.dto.MaxMinSession;
import com.example.analytics.dto.SessionAction;
import com.example.analytics.models.*;
import com.example.analytics.models.ViewEventAction;
import com.example.analytics.service.EventKpService;
import com.example.analytics.dto.Participation;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/monitoring")
public class EventKpiController {
    private EventKpService service;
    @GetMapping("/subscribe")
    SseEmitter subscribe() throws IOException {
        return service.subscribe();
    }

    @PostMapping("/view-event")
    ViewEventAction viewEventKpi(@RequestBody ViewEventAction viewEventAction) throws JsonProcessingException, EmptyListException {
        return service.handleViewAction(viewEventAction);
    }

    @PostMapping("/join-room")
    Session joinRoomKpi(@RequestBody SessionAction sessionAction) {
        return service.handleSessionAction(sessionAction);
    }

    @PostMapping("/end-meeting")
    Session endRoomKpi( @RequestBody SessionAction sessionAction) throws JsonProcessingException, EmptyListException {
        return service.handleClosingSession(sessionAction);
    }

    @GetMapping("/session-duration")
    void getSessionDuration(@RequestParam(name = "username") String username, @RequestParam(name = "event_Id") UUID eventId) {
        service.getSessionDuration(username, eventId);
    }

    @GetMapping("/participants")
    long countParticipants() {
        return service.getParticipantsNumber();
    }

    @GetMapping("/events-participated")
    long countEventsParticipantAt(@RequestParam(name = "username") String username) {
        return service.countEventsParticpatedAt(username);
    }

    @GetMapping("/participants-by-room")
    long countParticipantsByRoomId(@RequestParam(name = "roomId") UUID roomId) {
        return service.countParticipantsByRoomId(roomId);
    }

    @GetMapping("/max-views")
    CountEventViews getMaxViews() {
        return  service.getMaxViews();
    }

    @GetMapping("/min-views")
    CountEventViews getMinViews() {
        return service.getMinViews();
    }

    @GetMapping("/session-duration-by-room-max")
    Session getMaxDurationByEventId(@RequestParam(name = "event_Id") UUID eventId) {
        return  service.getMaxDurationByRoomId(eventId);
    }

    @GetMapping("/session-duration-by-room-min")
    Session getMinDurationByEventId(@RequestParam(name = "event_Id") UUID eventId) {
        return service.getMinDurationByRoomId(eventId);
    }


    @GetMapping("/session-duration-max")
    MaxMinSession getMaxSession() {
        return service.getMaxSession();
    }

    @GetMapping("/session-duration-min")
    MaxMinSession getMinSession() {
        return service.getMinSession();
    }

    @GetMapping("/session-duration-by-user")
    MaxMinSession getSessionDurationByUser(@RequestParam(name = "username") String username, @RequestParam(name = "event_Id") UUID eventId) {
        return service.getSessionDuration(username, eventId);
    }

    @GetMapping("/maximal-participants")
    Participation getMaximalParticipants() {
        return service.maximalParticipation();
    }

    @GetMapping("/minimal-participants")
    Participation getMinimalParticipants() {
        return service.minimalParticipation();
    }

    @GetMapping("/user-last-session-duration")
    MaxMinSession getLastSessionDurationByUser(@RequestParam(name = "username") String username) {
        return service.getLastSessionDuration(username);
    }



    @PostMapping("/abort-event")
    void abortEvent(@RequestBody AbortEvent abortEvent) {
        service.abortEvent(abortEvent);
    }

    @GetMapping("/count-events")
    long countEvents(){
        return service.countALlEvents();
    }
    @GetMapping("count-events/{userName}")
    long countEventsByUserName(@PathVariable String userName){
        return service.countAllEventsByUserName(userName);
    }

    @GetMapping("/countEventsAborted")
    long countEventsAborted() {
        return abortEventRepository.count();
    }

    @GetMapping("/findUserWithLeastAbortedEvents")
    String findUserWithLeastAbortedEvents() {
        return service.findUserWithLeastAbortedEvents();
    }

    @GetMapping("/findUserWithMostAbortedEvents")
    String findUserWithMostAbortedEvents() {
        return service.findUserWithMostAbortedEvents();
    }

    @GetMapping("/findTotalAbortedEventToday")
    long findTotalAbortedEventToday() {
        return service.findTotalAbortedEventToday();
    }

    @GetMapping("/findTotalAbortedEventByCurrentWeek")
    long findTotalAbortedEventByCurrentWeek() {
        return service.findTotalAbortedEventByCurrentWeek();
    }

    @GetMapping("/findTotalAbortedEventByCurrentMonth")
    long findTotalAbortedEventByCurrentMonth() {
        return service.findTotalAbortedEventByCurrentMonth();
    }

    @GetMapping("/calculateAverageAbortedEventsPerUser")
    double calculateAverageAbortedEventsPerUser() {
        return service.calculateAverageAbortedEventsPerUser();
    }

    @GetMapping("/findUsernameWithMostEvents")
    String findUsernameWithMostEvents() {
        return service.getUsernameWithMostEvents();
    }

    @GetMapping("/findUsernameWithLeastEvents")
    ResponseEntity<String> findUsernameWithLeastEvents() {
        return
                new ResponseEntity<String>(service.findUsernameWithLeastEvents(), HttpStatus.OK);
    }

    @GetMapping("/calculateAverageEventsPerUser")
    double calculateAverageEventsPerUser() {
        return service.calculateAverageEventsPerUser();
    }


    @PostMapping("/create-event")
    void addEventKpi(@RequestBody EventKpi eventKpi) {
        service.addKpi(eventKpi);
    }

    @PostMapping("/view-event")
    void viewEventKpi(@RequestBody ViewEvent viewEvent) {
        service.handleViewAction(viewEvent);
    }

    @PostMapping("/send-quiz")
    void sendQuizKpi(@RequestBody QuizzAction quizzAction) {
        service.persistQuizz(quizzAction);
    }

    @PostMapping("/pass-quiz")
    void passQuizKpi(@RequestBody QuizzAction quizzAction) {
        service.persistQuizz(quizzAction);
    }

    @GetMapping("/quizz-by-user")
    long countQuizzByUser(@RequestParam(name = "username") String userName) {
        return service.countQuizzByUser(userName);
    }

    @GetMapping("/quizz-by-event")
    long countQuizzByEvent(@RequestParam(name = "eventid") UUID eventId) {
        return service.countEventQuizzResponses(eventId);
    }

    @GetMapping("/views-by-user")
    long countViewsByUser(@RequestParam(name = "username") String userName) {
        return service.countViewsByUser(userName);
    }

    @GetMapping("/views-by-event")
    long countViewsByEvent(@RequestParam(name = "eventid") UUID viewEvent) {
        return service.viewEvent(viewEvent);
    }

    @GetMapping("/session-duration")
    long getSessionDuration(@RequestParam(name = "username") String username) {
        return service.getSessionDuration(username);
    }

    @GetMapping("/total-events-today")
    long getTotalEventsToday() {
        return service.findTotalByToday();
    }

    @GetMapping("/total-events-this-week")
    long getTotalEventsThisWeek() {
        return service.findTotalByCurrentWeek();
    }

    @GetMapping("/total-events-this-month")
    long getTotalEventsThisMonth() {
        return service.findTotalByCurrentMonth();
    }

    @GetMapping("/total-events-today/{userName}")
    long getTotalEventsTodayByUsername(@PathVariable String userName) {
        return service.findTotalByTodayAndUserName(userName);
    }

    @GetMapping("/total-events-this-week/{userName}")
    long getTotalEventsThisWeekByUsername(@PathVariable String userName) {
        return service.findTotalByCurrentWeekAndUserName(userName);
    }

    @GetMapping("/total-events-this-month/{userName}")
    long getTotalEventsThisMonthByUsername(@PathVariable String userName) {
        return service.findTotalByCurrentMonthAndUserName(userName);
    }
}
