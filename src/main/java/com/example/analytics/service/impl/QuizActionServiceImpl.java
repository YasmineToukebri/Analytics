package com.example.analytics.service.impl;

import com.example.analytics.dto.DataToEmit;
import com.example.analytics.dto.EAction;
import com.example.analytics.models.QuizzAction;
import com.example.analytics.repository.QuizAction;
import com.example.analytics.service.QuizActionService;
import com.example.analytics.service.SSEService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class QuizActionServiceImpl implements QuizActionService {
    private final QuizAction quizActionRepository;
    private final SSEService sseService;
    @Override
    public long countEventQuizzResponses(UUID eventId) {
        long countAll = quizActionRepository.countAllByEventId(eventId);
        sseService.emitData(EAction.countQuizzPerEvent.toString(),countAll+"");
        return countAll;
    }

    @Override
    public long countQuizzByUser(String userName) {
        long countAll = quizActionRepository.countAllByUserName(userName);
        sseService.emitData(EAction.countQuizzPerUser.toString(),countAll+"");
        return countAll;
    }
    @Override
    public void persistQuizz(QuizzAction quizzAction) {
        quizzAction.setPassedAt(LocalDateTime.now());
        quizActionRepository.save(quizzAction);
        List<DataToEmit> dataToEmits = List.of(
                DataToEmit.builder().action(EAction.countQuizzPerEvent.toString()).data(this.countEventQuizzResponses(quizzAction.getEventId())+" " + "event :" + quizzAction.getEventId()).build(),
                DataToEmit.builder().action(EAction.countQuizzPerUser.toString()).data(this.countQuizzByUser(quizzAction.getUserName())+" " + "user :" + quizzAction.getUserName()).build()
        );
        sseService.emitMultipleData(dataToEmits);
    }
}
