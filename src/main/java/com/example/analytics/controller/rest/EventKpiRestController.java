package com.example.analytics.controller.rest;

import com.example.analytics.models.EventKpi;
import com.example.analytics.service.EventKpiService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@AllArgsConstructor
@RequestMapping("/monitoring")
public class EventKpiRestController {
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
