package com.example.analytics.service.impl;

import com.example.analytics.dto.CountEventViews;
import com.example.analytics.dto.DataToEmit;
import com.example.analytics.dto.EAction;
import com.example.analytics.exception.EmptyListException;
import com.example.analytics.models.ViewEventAction;
import com.example.analytics.repository.ViewEventRepository;
import com.example.analytics.service.SSEService;
import com.example.analytics.service.ViewEventActionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

@RequiredArgsConstructor
@Service
public class ViewEventActionServiceImpl implements ViewEventActionService {
    private ViewEventRepository viewEventRepository;
    private SSEService sseService;
    @Override
    public long viewEvent(UUID viewEvent) {
        long countAll = viewEventRepository.countAllByEventId(viewEvent);
        sseService.emitData("viewEvent",countAll+"");
        return countAll;
    }

    @Override
    public ViewEventAction handleViewAction(ViewEventAction viewEventAction) throws JsonProcessingException {
        viewEventAction.setSeenAt(LocalDateTime.now());
        ViewEventAction action = viewEventRepository.save(viewEventAction);
        JsonMapper jsonMapper = new JsonMapper();
        jsonMapper.registerModule(new JavaTimeModule());
        ObjectWriter ow = new ObjectMapper().registerModule(new JavaTimeModule()).writer().withDefaultPrettyPrinter();
        String maxViews = ow.writeValueAsString(this.getMaxViews());
        String minViews = ow.writeValueAsString(this.getMinViews());
        String userTotalViews = ow.writeValueAsString(this.countViewsByUser(viewEventAction.getUserName()));

        List<DataToEmit> dataToEmits = List.of(
                DataToEmit.builder().action(EAction.viewsPerUser.toString()).data(userTotalViews).build(),
                DataToEmit.builder().action(EAction.maxViewsNumber.toString()).data(maxViews).build(),
                DataToEmit.builder().action(EAction.minViewsNumber.toString()).data(minViews).build()
        );
        sseService.emitMultipleData(dataToEmits);
        return action;
    }

    @Override
    public long countViewsByUser(String userName) {
        return  viewEventRepository.countAllByUserName(userName);
    }

    @Override
    public CountEventViews getMaxViews() {
        List<CountEventViews> views = viewEventRepository.countMaxViews();
        checkNullList(e -> e == null || e.isEmpty(), "No Max views found", views);
        return views.get(0);
    }

    @Override
    public CountEventViews getMinViews() {
        List<CountEventViews> views = viewEventRepository.countMinViews();
        checkNullList(e -> e == null || e.isEmpty(), "No Min views found", views);
        return views.get(0);
    }

    private <T> void checkNullList(Predicate<List<T>> predicate, String message, List<T> list) {
        if(predicate.test(list)) {
            throw new EmptyListException(message);
        }
    }
}
