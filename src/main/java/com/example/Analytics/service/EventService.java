package com.example.Analytics.service;

import com.example.Analytics.models.EventKpi;
import com.example.Analytics.repository.EventKpiRepository;
import org.springframework.stereotype.Service;

@Service
public class EventService {
    private final EventKpiRepository eventKpiRepository;

    public EventService(EventKpiRepository eventKpiRepository) {
        this.eventKpiRepository = eventKpiRepository;
    }
}

