package com.example.analytics.controller.graphql;

import com.example.analytics.models.AbortEvent;
import com.example.analytics.service.AbortEventService;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@Controller
public class AbortEventController {


    private AbortEventService abortEventService;

    @MutationMapping
    void abortEvent(@Argument(name = "abortEventInput") AbortEvent abortEvent) {
        abortEventService.abortEvent(abortEvent);
    }
    @QueryMapping
    long countEventsAborted() {
        return abortEventService.findTotalAbortedEvents();
    }

    @QueryMapping
    String findUserWithLeastAbortedEvents() {
        return abortEventService.findUserWithLeastAbortedEvents();
    }

    @QueryMapping
    String findUserWithMostAbortedEvents() {
        return abortEventService.findUserWithMostAbortedEvents();
    }

    @QueryMapping
    long findTotalAbortedEventToday() {
        return abortEventService.findTotalAbortedEventToday();
    }

    @QueryMapping
    long findTotalAbortedEventByCurrentWeek() {
        return abortEventService.findTotalAbortedEventByCurrentWeek();
    }

    @QueryMapping
    long findTotalAbortedEventByCurrentMonth() {
        return abortEventService.findTotalAbortedEventByCurrentMonth();
    }

    @QueryMapping
    double calculateAverageAbortedEventsPerUser() {
        return abortEventService.calculateAverageAbortedEventsPerUser();
    }

}
