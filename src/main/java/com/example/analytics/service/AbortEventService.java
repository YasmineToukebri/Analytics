package com.example.analytics.service;

import com.example.analytics.models.AbortEvent;

public interface AbortEventService {
    void abortEvent(AbortEvent abortEvent);

    String findUserWithLeastAbortedEvents();

    String findUserWithMostAbortedEvents();

    Long findTotalAbortedEvents();
    Long findTotalAbortedEventToday();

    Long findTotalAbortedEventByCurrentWeek();

    Long findTotalAbortedEventByCurrentMonth();

    double calculateAverageAbortedEventsPerUser();
}
