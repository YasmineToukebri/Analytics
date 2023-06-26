package com.example.analytics.controller.rest;

import com.example.analytics.models.AbortEvent;
import com.example.analytics.service.AbortEventService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/monitoring")
public class AbortEventRestController {
    private AbortEventService abortEventService;

    @PostMapping("/abort-event")
    void abortEvent(@RequestBody AbortEvent abortEvent) {
        abortEventService.abortEvent(abortEvent);
    }

    @GetMapping("/countEventsAborted")
    long countEventsAborted() {
        return abortEventService.findTotalAbortedEvents();
    }

    @GetMapping("/findUserWithLeastAbortedEvents")
    String findUserWithLeastAbortedEvents() {
        return abortEventService.findUserWithLeastAbortedEvents();
    }

    @GetMapping("/findUserWithMostAbortedEvents")
    String findUserWithMostAbortedEvents() {
        return abortEventService.findUserWithMostAbortedEvents();
    }

    @GetMapping("/findTotalAbortedEventToday")
    long findTotalAbortedEventToday() {
        return abortEventService.findTotalAbortedEventToday();
    }

    @GetMapping("/findTotalAbortedEventByCurrentWeek")
    long findTotalAbortedEventByCurrentWeek() {
        return abortEventService.findTotalAbortedEventByCurrentWeek();
    }

    @GetMapping("/findTotalAbortedEventByCurrentMonth")
    long findTotalAbortedEventByCurrentMonth() {
        return abortEventService.findTotalAbortedEventByCurrentMonth();
    }

    @GetMapping("/calculateAverageAbortedEventsPerUser")
    double calculateAverageAbortedEventsPerUser() {
        return abortEventService.calculateAverageAbortedEventsPerUser();
    }

}
