package com.example.analytics.controller;


import com.example.analytics.dto.CountEventViews;
import com.example.analytics.models.ViewEventAction;
import com.example.analytics.service.ViewEventActionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/monitoring")
public class ViewEventController {
    private ViewEventActionService viewEventActionService;

    @PostMapping("/view-event")
    ViewEventAction viewEventKpi(@RequestBody ViewEventAction viewEventAction) throws JsonProcessingException {
        return viewEventActionService.handleViewAction(viewEventAction);
    }
    @GetMapping("/views-by-user")
    long countViewsByUser(@RequestParam(name = "username") String userName) {
        return viewEventActionService.countViewsByUser(userName);
    }

    @GetMapping("/views-by-event")
    long countViewsByEvent(@RequestParam(name = "eventid") UUID viewEvent) {
        return viewEventActionService.viewEvent(viewEvent);
    }

    @GetMapping("/max-views")
    CountEventViews getMaxViews() {
        return  viewEventActionService.getMaxViews();
    }

    @GetMapping("/min-views")
    CountEventViews getMinViews() {
        return viewEventActionService.getMinViews();
    }
}
