package com.example.Analytics.controller;
import com.example.Analytics.models.EventKpi;
import com.example.Analytics.service.EventKpService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.io.IOException;
@RestController
@AllArgsConstructor
public class EventKpiController {
    private EventKpService service;
    @GetMapping("/subscribe")
    SseEmitter subscribe() throws IOException {
        return service.subscribe();
    }

    @PostMapping("/monitoring/create-event")
    void addEventKpi(@RequestBody EventKpi eventKpi) throws IOException {
         service.addKpi(eventKpi);
    }

}
