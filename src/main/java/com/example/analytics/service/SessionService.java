package com.example.analytics.service;

import com.example.analytics.dto.MaxMinSession;
import com.example.analytics.dto.Participation;
import com.example.analytics.dto.SessionAction;
import com.example.analytics.models.Session;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.UUID;

public interface SessionService {
    Session handleSessionAction(SessionAction sessionAction);

    Session handleClosingSession(SessionAction sessionAction) throws JsonProcessingException;

    MaxMinSession getSessionDuration(String username, UUID roomId);

    long countParticipantsByRoomId(UUID roomId);

    Session getMaxDurationByRoomId(UUID roomId);

    Session getMinDurationByRoomId(UUID roomId);

    MaxMinSession getMaxSession();

    MaxMinSession getMinSession();

    long getParticipantsNumber();

    Participation maximalParticipation();

    Participation minimalParticipation();

    MaxMinSession getLastSessionDuration(String username);

    long countEventsParticpatedAt(String username);

}
