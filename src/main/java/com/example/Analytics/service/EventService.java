package com.example.Analytics.service;
import com.example.Analytics.models.EventKpi;
import com.example.Analytics.repository.EventKpiRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;


@Service
public class EventService implements EventKpService {

    private  static List<SseEmitter> emitters = new java.util.ArrayList<>();

    private final EventKpiRepository eventKpiRepository;

    public EventService(EventKpiRepository eventKpiRepository) {
        this.eventKpiRepository = eventKpiRepository;
    }

    @Override
    public SseEmitter subscribe() throws IOException {
        SseEmitter sseEmitter = new SseEmitter(600000L);
        sseEmitter.send(SseEmitter.event()
                .name("message")
                .data("connexion"));
        sseEmitter.onCompletion(() -> this.emitters.remove(sseEmitter));
        this.emitters.add(sseEmitter);
        return sseEmitter;
    }
    @Override
    public void addKpi(EventKpi eventKpi) throws IOException {
        eventKpi.setEventId(UUID.randomUUID());
        EventKpi eventKpi1 = eventKpiRepository.save(eventKpi);
        long countAll = eventKpiRepository.count();
        long countByUsername = eventKpiRepository.countByUserName("ilyes");
        System.out.println(countByUsername);
        for(SseEmitter emitter : this.emitters) {
            emitter.send(SseEmitter.event()
                    .name("message")
                    .data(countAll));
        }

    }

    }



