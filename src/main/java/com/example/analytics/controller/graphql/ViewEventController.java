package com.example.analytics.controller.graphql;


import com.example.analytics.dto.CountEventViews;
import com.example.analytics.models.ViewEventAction;
import com.example.analytics.service.ViewEventActionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@AllArgsConstructor
@Controller
public class ViewEventController {
    private ViewEventActionService viewEventActionService;

    @MutationMapping
    ViewEventAction viewEventKpi(@Argument("viewEventActionInput") ViewEventAction viewEventAction) throws JsonProcessingException {
        return viewEventActionService.handleViewAction(viewEventAction);
    }
    @QueryMapping
    long countViewsByUser(@Argument String userName) {
        return viewEventActionService.countViewsByUser(userName);
    }

    @QueryMapping
    long countViewsByEvent(@Argument("eventId") UUID viewEvent) {
        return viewEventActionService.viewEvent(viewEvent);
    }

    @QueryMapping
    CountEventViews getMaxViews() {
        return  viewEventActionService.getMaxViews();
    }

    @QueryMapping
    CountEventViews getMinViews() {
        return viewEventActionService.getMinViews();
    }
}
