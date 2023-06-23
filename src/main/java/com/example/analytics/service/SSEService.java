package com.example.analytics.service;

import com.example.analytics.dto.DataToEmit;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;

public interface SSEService {

    void emitData(String action, String data);

    SseEmitter subscribe() throws IOException;

    void emitData(DataToEmit data);
    void emitMultipleData(List<DataToEmit> dataToEmits);

}
