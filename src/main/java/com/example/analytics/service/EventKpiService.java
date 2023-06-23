package com.example.analytics.service;

import com.example.analytics.models.EventKpi;

public interface EventKpiService {
    void addKpi(EventKpi eventKpi);

    long countALlEvents();

    long countAllEventsByUserName(String userName);

    String getUsernameWithMostEvents();

    String findUsernameWithLeastEvents();

    double calculateAverageEventsPerUser();

    Long findTotalByToday();

    Long findTotalByCurrentWeek();

    Long findTotalByCurrentMonth();

    Long findTotalByTodayAndUserName(String userName);

    Long findTotalByCurrentWeekAndUserName(String userName);

    Long findTotalByCurrentMonthAndUserName(String userName);


}
