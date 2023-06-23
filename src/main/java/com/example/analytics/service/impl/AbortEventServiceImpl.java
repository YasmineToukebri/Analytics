package com.example.analytics.service.impl;

import com.example.analytics.dto.EAction;
import com.example.analytics.exception.EmptyListException;
import com.example.analytics.models.AbortEvent;
import com.example.analytics.repository.AbortEventRepository;
import com.example.analytics.service.AbortEventService;
import com.example.analytics.service.SSEService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Predicate;

@RequiredArgsConstructor
@Service
public class AbortEventServiceImpl implements AbortEventService {

    private SSEService sseService;
    private AbortEventRepository abortEventRepository;

    @Override
    public void abortEvent(AbortEvent abortEvent) {
        abortEvent.setAbortedAt(LocalDateTime.now());
        abortEventRepository.save(abortEvent);

        String findUserWithLeastAbortedEvents= findUserWithLeastAbortedEvents();
        sseService.emitData(EAction.findUserWithLeastAbortedEvents.toString(),findUserWithLeastAbortedEvents);

        String findUserWithMostAbortedEvents= findUserWithMostAbortedEvents();
        sseService.emitData(EAction.findUserWithMostAbortedEvents.toString(),findUserWithMostAbortedEvents);

        long countEventsAborted = abortEventRepository.count();
        sseService.emitData(EAction.abortEvent.toString(),countEventsAborted+"");

        long countTotalAbortedEventToday = abortEventRepository.findTotalAbortedEventToday();
        sseService.emitData(EAction.findTotalAbortedEventToday.toString(),countTotalAbortedEventToday+"");

        long countTotalAbortedEventByCurrentWeek = abortEventRepository.findTotalAbortedEventByCurrentWeek();
        sseService.emitData(EAction.findTotalAbortedEventByCurrentWeek.toString(),countTotalAbortedEventByCurrentWeek+"");

        long countTotalAbortedEventByCurrentMonth = abortEventRepository.findTotalAbortedEventByCurrentMonth();
        sseService.emitData(EAction.findTotalAbortedEventByCurrentMonth.toString(),countTotalAbortedEventByCurrentMonth+"");

        double calculateAverageAbortedEventsPerUser = abortEventRepository.calculateAverageAbortedEventsPerUser();
        sseService.emitData(EAction.calculateAverageAbortedEventsPerUser.toString(),calculateAverageAbortedEventsPerUser+"");
    }

    @Override
    public String findUserWithLeastAbortedEvents() {
        List<String> findUserWithLeastAbortedEvents=abortEventRepository.findUserWithLeastAbortedEvents();
        checkNullList(e -> e == null || e.isEmpty(), "No aborted event found", findUserWithLeastAbortedEvents);
        return findUserWithLeastAbortedEvents.get(0);
    }
    @Override
    public String findUserWithMostAbortedEvents() {
        List<String> findUserWithMostAbortedEvents=abortEventRepository.findUserWithMostAbortedEvents();
        checkNullList(e -> e == null || e.isEmpty(), "No most aborted event found", findUserWithMostAbortedEvents);

        return findUserWithMostAbortedEvents.get(0);
    }
    @Override
    public Long findTotalAbortedEventToday() {
        return abortEventRepository.findTotalAbortedEventToday();
    }

    @Override
    public Long findTotalAbortedEventByCurrentWeek() {
        return abortEventRepository.findTotalAbortedEventByCurrentWeek();
    }
    @Override
    public Long findTotalAbortedEventByCurrentMonth() {
        return abortEventRepository.findTotalAbortedEventByCurrentMonth();
    }

    @Override
    public double calculateAverageAbortedEventsPerUser() {
        return abortEventRepository.calculateAverageAbortedEventsPerUser();
    }

    private <T> void checkNullList(Predicate<List<T>> predicate, String message, List<T> list) {
        if(predicate.test(list)) {
            throw new EmptyListException(message);
        }
    }
}
