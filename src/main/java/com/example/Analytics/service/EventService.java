package com.example.Analytics.service;

import com.example.Analytics.dto.DataToEmit;
import com.example.Analytics.dto.MinMaxViewedEventDto;
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
    private final JoinRoomRepository joinRoomRepository;
    private final EndMeetingRepository endMeetingRepository;
    private final SendQuizRepository sendQuizRepository;
    private final PassQuizRepository passQuizRepository;


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

    public void emitData(String action, String data) {
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
        for (SseEmitter emitter : this.emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("message")
                        .data(countAll));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void viewEvent(ViewEvent viewEvent) {
        viewEvent = viewEventRepository.save(viewEvent);
        getMinEventViews();
    }

    @Override
    public void joinRoom(JoinRoom joinRoom) {
        joinRoom = joinRoomRepository.save(joinRoom);

    }

    @Override
    public void endMeeting(EndMeeting endMeeting) {
        endMeeting = endMeetingRepository.save(endMeeting);

    }

    @Override
    public void sendQuiz(SendQuiz sendQuiz) {
        sendQuiz = sendQuizRepository.save(sendQuiz);

    }

    @Override
    public void passQuiz(PassQuiz passQuiz) {
        passQuiz = passQuizRepository.save(passQuiz);

    }

    public MinMaxViewedEventDto getMinEventViews() {
//        System.out.println(viewEventRepository.findMinEventIdOccurrence());
        System.out.println(viewEventRepository.findViewEventWithMinOccurrence().getEventId());

        return MinMaxViewedEventDto.builder()
//                .eventId(viewEventRepository.findViewEventWithMinOccurrence().getEventId())
//                .numberOfViews(viewEventRepository.findMinEventIdOccurrence())
                .build();
    }

}



