package com.example.analytics.controller;

import com.example.analytics.dto.CountEventViews;
import com.example.analytics.dto.MaxMinSession;
import com.example.analytics.dto.Participation;
import com.example.analytics.dto.SessionAction;
import com.example.analytics.models.*;
import com.example.analytics.service.EventKpService;
import com.example.analytics.service.EventKpiService;
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
    private EventKpiService service;

    @GetMapping("/count-events")
    long countEvents(){
        return service.countALlEvents();
    }
    @GetMapping("count-events/{userName}")
    long countEventsByUserName(@PathVariable String userName){
        return service.countAllEventsByUserName(userName);
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
