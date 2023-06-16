package com.example.Analytics.controller;

import com.example.Analytics.models.EventKpi;
import com.example.Analytics.models.ViewEvent;
import com.example.Analytics.service.EventKpService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@RestController
@AllArgsConstructor
@RequestMapping("/monitoring")
public class EventKpiController {
    private EventKpService service;

    @GetMapping("/subscribe")
    SseEmitter subscribe() throws IOException {
        return service.subscribe();
    }

    @PostMapping("/create-event")
    void addEventKpi(@RequestBody EventKpi eventKpi) {
        service.addKpi(eventKpi);
    }

    @PostMapping("/view-event")
    void viewEventKpi(@RequestBody ViewEvent viewEvent) {
        service.viewEvent(viewEvent);
    }

    @PostMapping("/join-room")
    void joinRoomKpi( ) {
    }

    @PostMapping("/end-meeting")
    void endRoomKpi( ) {
    }

    @PostMapping("/send-quiz")
    void sendQuizKpi(  ) {
    }

    @PostMapping("/pass-quiz")
    void passQuizKpi(  ) {
    }

}
