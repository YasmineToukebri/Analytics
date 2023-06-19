package com.example.analytics.dto;

public enum EAction {
    viewsPerUser,
    maxViewsNumber,
    minViewsNumber,
    sessionDuration,
    maximalSessionDuration,
    minimalSessionDuration,
    totalParticipation,
    currentUserParticipation,
    maximalParticipation,
    minimalParticipation,
    countQuizzPerEvent,
    countQuizzPerUser,
    findUserWithLeastAbortedEvents,
    findUserWithMostAbortedEvents,
    abortEvent,
    findTotalAbortedEventToday,
    findTotalAbortedEventByCurrentWeek,
    findTotalAbortedEventByCurrentMonth,
    calculateAverageAbortedEventsPerUser,
    totalEventsByUsername,
    findUsernameWithMostEvents,
    findUsernameWithLeastEvents,
    calculateAverageEventsPerUser,
    totalEventsThisDay,
    totalEventsThisWeek,
    totalEventsThisMonth,
    findTotalByTodayAndUserName,
    findTotalByCurrentWeekAndUserName,
    findTotalByCurrentMonthAndUserName,

    COUNT_ALL_EVENTS


}
