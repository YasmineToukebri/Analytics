package com.example.Analytics.service;

import com.example.Analytics.models.EventKpi;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;


public interface EventKpService {
     void addKpi(EventKpi eventKpi) throws IOException;
     SseEmitter subscribe() throws IOException;

}
