package com.example.analytics.controller.graphql;

import com.example.analytics.models.EventKpi;
import com.example.analytics.service.EventKpiService;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@Controller
public class EventKpiController {
    private EventKpiService service;

    @QueryMapping
    long countEvents() {
        return service.countALlEvents();
    }

    @QueryMapping
    long countEventsByUserName(@Argument String userName) {
        return service.countAllEventsByUserName(userName);
    }

    @QueryMapping
    String findUsernameWithMostEvents() {
        return service.getUsernameWithMostEvents();
    }

    @QueryMapping
    String findUsernameWithLeastEvents() {
        return service.findUsernameWithLeastEvents();
    }

    @QueryMapping
    double calculateAverageEventsPerUser() {
        return service.calculateAverageEventsPerUser();
    }

    @MutationMapping
    void addEventKpi(@Argument("eventInput") EventKpi eventKpi) {
        service.addKpi(eventKpi);
    }


    @QueryMapping
    long getTotalEventsToday() {
        return service.findTotalByToday();
    }

    @QueryMapping
    long getTotalEventsThisWeek() {
        return service.findTotalByCurrentWeek();
    }

    @QueryMapping
    long getTotalEventsThisMonth() {
        return service.findTotalByCurrentMonth();
    }

    @QueryMapping
    long getTotalEventsTodayByUsername(@Argument String userName) {
        return service.findTotalByTodayAndUserName(userName);
    }

    @QueryMapping
    long getTotalEventsThisWeekByUsername(@Argument String userName) {
        return service.findTotalByCurrentWeekAndUserName(userName);
    }

    @QueryMapping
    long getTotalEventsThisMonthByUsername(@Argument String userName) {
        return service.findTotalByCurrentMonthAndUserName(userName);
    }
}
