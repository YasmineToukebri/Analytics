package com.example.Analytics.models;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.UUID;

public abstract class KpiAction {
    @Id
    @GeneratedValue( strategy= GenerationType.AUTO)
    private UUID id;
    @Column(name = "user")
    private String userName;
    @Column(name = "event_id")
    private UUID eventId;
}
