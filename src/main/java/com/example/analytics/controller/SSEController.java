package com.example.analytics.controller;

import com.example.analytics.service.SSEService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@RestController
@AllArgsConstructor
@RequestMapping("/monitoring")
public class SSEController {
    private SSEService sseService;

    @GetMapping("/subscribe")
    SseEmitter subscribe() throws IOException {
        return sseService.subscribe();
    }
}
