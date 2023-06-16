package com.example.Analytics.service;

import com.example.Analytics.dto.DataToEmit;
import com.example.Analytics.models.*;
import com.example.Analytics.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class EventService implements EventKpService {

    private static List<SseEmitter> emitters = new java.util.ArrayList<>();

    private final EventKpiRepository eventKpiRepository;
    private final ViewEventRepository viewEventRepository;
    private final sessionAction sessionActionRepository;
    private final quizAction quizzActionRepository;


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

    public void emitData(String action,String data) {
        DataToEmit dataToEmit = DataToEmit.builder()
                .action(action)
                .data(data)
                .build();
        for (SseEmitter emitter : this.emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("message")
                        .data(dataToEmit));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void addKpi(EventKpi eventKpi) {
        eventKpi.setEventId(UUID.randomUUID());
        EventKpi eventKpi1 = eventKpiRepository.save(eventKpi);
        long countAll = eventKpiRepository.count();
        long countByUsername = eventKpiRepository.countByUserName("ilyes");
        System.out.println(countByUsername);
        this.emitData("addKpi",countAll+"");
    }

    @Override
    public void viewEvent(ViewEvent viewEvent) {
        viewEventRepository.save(viewEvent);
        long countAll = viewEventRepository.countAllByEventId(viewEvent.getEventId());
        this.emitData("viewEvent",countAll+"");
    }

    @Override
    public void handleSessionAction(SessionAction sessionAction) {
        sessionActionRepository.save(sessionAction);

    }

    @Override
    public void handleQuizzAction(QuizzAction quizzAction) {
        quizzActionRepository.save(quizzAction);
        long countAll = quizzActionRepository.countAllByEventId(quizzAction.getEventId());
        this.emitData("quizzAction",countAll+"");
    }
}



