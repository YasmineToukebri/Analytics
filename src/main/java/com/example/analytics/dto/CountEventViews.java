package com.example.analytics.dto;

import java.util.UUID;

public interface CountEventViews {
    long getCountViews();
    UUID getEventId();
}
