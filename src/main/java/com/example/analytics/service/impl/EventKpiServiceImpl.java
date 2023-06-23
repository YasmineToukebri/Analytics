package com.example.analytics.service.impl;

import com.example.analytics.dto.EAction;
import com.example.analytics.models.EventKpi;
import com.example.analytics.repository.EventKpiRepository;
import com.example.analytics.service.EventKpiService;
import com.example.analytics.service.SSEService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.example.analytics.dto.EAction.COUNT_ALL_EVENTS;

@RequiredArgsConstructor
@Service
public class EventKpiServiceImpl implements EventKpiService {

    private final EventKpiRepository eventKpiRepository;
    private final SSEService sseService;

    @Override
    public void addKpi(EventKpi eventKpi) {
        eventKpi.setEventId(UUID.randomUUID());
        eventKpi.setActionAt(LocalDateTime.now());
        eventKpiRepository.save(eventKpi);

        long totalEventsByUsername = countAllEventsByUserName(eventKpi.getUserName());
        sseService.emitData(EAction.totalEventsByUsername.toString(), totalEventsByUsername + "");

        String usernameWithMostEvents = getUsernameWithMostEvents();
        sseService.emitData(EAction.findUsernameWithMostEvents.toString(), usernameWithMostEvents);

        String usernameWithLeastEvents = findUsernameWithLeastEvents();
        sseService.emitData(EAction.findUsernameWithLeastEvents.toString(), usernameWithLeastEvents);

        double calculateAverageEventsPerUser = calculateAverageEventsPerUser();
        sseService.emitData(EAction.calculateAverageEventsPerUser.toString(), calculateAverageEventsPerUser + "");

        long totalEventsThisDay = findTotalByToday();
        sseService.emitData(EAction.totalEventsThisDay.toString(), totalEventsThisDay + "");

        long totalEventsThisWeek = findTotalByCurrentWeek();
        sseService.emitData(EAction.totalEventsThisWeek.toString(), totalEventsThisWeek + "");

        long totalEventsThisMonth = findTotalByCurrentMonth();
        sseService.emitData(EAction.totalEventsThisMonth.toString(), totalEventsThisMonth + "");

        long findTotalByTodayAndUserName = findTotalByTodayAndUserName(eventKpi.getUserName());
        sseService.emitData(EAction.findTotalByTodayAndUserName.toString(), findTotalByTodayAndUserName + "");

        long findTotalByCurrentWeekAndUserName = findTotalByCurrentWeekAndUserName(eventKpi.getUserName());
        sseService.emitData(EAction.findTotalByCurrentWeekAndUserName.toString(), findTotalByCurrentWeekAndUserName + "");

        long findTotalByCurrentMonthAndUserName = findTotalByCurrentMonthAndUserName(eventKpi.getUserName());
        sseService.emitData(EAction.findTotalByCurrentMonthAndUserName.toString(), findTotalByCurrentMonthAndUserName + "");

        long countAll = eventKpiRepository.count();
        sseService.emitData(COUNT_ALL_EVENTS.toString(), countAll + "");

    }

    @Override
    public long countALlEvents() {
        return eventKpiRepository.count();
    }

    @Override
    public long countAllEventsByUserName(String userName) {
        return eventKpiRepository.countByUserName(userName);
    }

    @Override
    public String getUsernameWithMostEvents() {
        List<String> findUsernameWithMostEvents = eventKpiRepository.findUsernameWithMostEvents();
        return findUsernameWithMostEvents.get(0);
    }

    @Override
    public String findUsernameWithLeastEvents() {
        List<String> usernameWithLeastEvents = eventKpiRepository.findUsernameWithLeastEvents();
        return usernameWithLeastEvents.get(0);
    }

    @Override
    public Long findTotalByToday() {
        return eventKpiRepository.findTotalByToday();
    }

    @Override
    public Long findTotalByCurrentWeek() {
        return eventKpiRepository.findTotalByCurrentWeek();
    }

    @Override
    public Long findTotalByCurrentMonth() {
        return eventKpiRepository.findTotalByCurrentMonth();
    }

    @Override
    public Long findTotalByTodayAndUserName(String userName) {
        return eventKpiRepository.findTotalByUsernameToday(userName);
    }

    @Override
    public Long findTotalByCurrentWeekAndUserName(String userName) {
        return eventKpiRepository.findTotalByUserNameAndCurrentWeek(userName);
    }

    @Override
    public Long findTotalByCurrentMonthAndUserName(String userName) {
        return eventKpiRepository.findTotalByUserNameAndCurrentMonth(userName);
    }

    @Override
    public double calculateAverageEventsPerUser() {
        return eventKpiRepository.calculateAverageEventsPerUser();
    }

}
