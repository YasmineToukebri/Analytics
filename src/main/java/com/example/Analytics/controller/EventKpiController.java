package com.example.Analytics.controller;

import com.example.Analytics.dto.SessionAction;
import com.example.Analytics.models.AbortEvent;
import com.example.Analytics.models.EventKpi;
import com.example.Analytics.models.QuizzAction;
import com.example.Analytics.models.ViewEvent;
import com.example.Analytics.service.EventKpService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/monitoring")
public class EventKpiController {
    private EventKpService service;



    @PostMapping("/abort-event")
    void abortEvent(@RequestBody AbortEvent abortEvent) {
        service.abortEvent(abortEvent);
    }

    @GetMapping("/findUserWithLeastAbortedEvents")
    String findUserWithLeastAbortedEvents(){
        return service.findUserWithLeastAbortedEvents();
    }

    @GetMapping("/findUserWithMostAbortedEvents")
    String findUserWithMostAbortedEvents(){
        return service.findUserWithMostAbortedEvents();
    }



    @GetMapping("/findTotalAbortedEventToday")
    long findTotalAbortedEventToday(){
        return service.findTotalAbortedEventToday();
    }

    @GetMapping("/findTotalAbortedEventByCurrentWeek")
    long findTotalAbortedEventByCurrentWeek(){
        return service.findTotalAbortedEventByCurrentWeek();
    }

    @GetMapping("/findTotalAbortedEventByCurrentMonth")
    long findTotalAbortedEventByCurrentMonth(){
        return service.findTotalAbortedEventByCurrentMonth();
    }

    @GetMapping("/calculateAverageAbortedEventsPerUser")
    double calculateAverageAbortedEventsPerUser(){
        return service.calculateAverageAbortedEventsPerUser();
    }






    @GetMapping("/findUsernameWithMostEvents")
    String findUsernameWithMostEvents(){
        return service.getUsernameWithMostEvents();
    }

    @GetMapping("/findUsernameWithLeastEvents")
    String findUsernameWithLeastEvents(){
        return service.findUsernameWithLeastEvents();
    }


    @GetMapping("/calculateAverageEventsPerUser")
    double calculateAverageEventsPerUser(){
        return service.calculateAverageEventsPerUser();
    }






    @GetMapping("/subscribe")
    SseEmitter subscribe() throws IOException {
        return service.subscribe();
    }

    @PostMapping("/create-event")
    void addEventKpi(@RequestBody EventKpi eventKpi) {
        service.addKpi(eventKpi);
    }

    @PostMapping("/view-event")
    void viewEventKpi(@RequestBody ViewEvent viewEvent) {
        service.handleViewAction(viewEvent);
    }

    @PostMapping("/join-room")
    void joinRoomKpi(@RequestBody SessionAction sessionAction) {
        service.handleSessionAction(sessionAction);
    }

    @PostMapping("/end-meeting")
    void endRoomKpi( @RequestBody SessionAction sessionAction) {
        service.handleClosingSession(sessionAction);
    }

    @PostMapping("/send-quiz")
    void sendQuizKpi(@RequestBody  QuizzAction quizzAction) {
        service.persistQuizz(quizzAction);
    }

    @PostMapping("/pass-quiz")
    void passQuizKpi(@RequestBody  QuizzAction quizzAction) {
        service.persistQuizz(quizzAction);
    }

    @GetMapping("/quizz-by-user")
    long countQuizzByUser(@RequestParam(name = "username") String userName) {
        return service.countQuizzByUser(userName);
    }

    @GetMapping("/quizz-by-event")
    long countQuizzByEvent(@RequestParam(name = "eventid") UUID eventId) {
      return  service.countEventQuizzResponses(eventId);
    }

    @GetMapping("/views-by-user")
    long countViewsByUser(@RequestParam(name = "username") String userName) {
      return  service.countViewsByUser(userName);
    }

    @GetMapping("/views-by-event")
    long countViewsByEvent(@RequestParam(name = "eventid") UUID viewEvent) {
      return   service.viewEvent(viewEvent);
    }

    @GetMapping("/session-duration")
    long getSessionDuration(@RequestParam(name = "username") String username) {
      return   service.getSessionDuration(username);
    }

}
