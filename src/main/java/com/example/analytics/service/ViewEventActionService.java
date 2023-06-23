package com.example.analytics.service;

import com.example.analytics.dto.CountEventViews;
import com.example.analytics.models.ViewEventAction;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.UUID;

public interface ViewEventActionService {
    long viewEvent(UUID viewEvent);
    ViewEventAction handleViewAction(ViewEventAction viewEventAction) throws JsonProcessingException;
    long countViewsByUser(String userName);
    CountEventViews getMaxViews();

    CountEventViews getMinViews();
}
