package com.example.Analytics.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.UUID;
@Entity
@Data
@Table(name = "EventKpi")
@AllArgsConstructor
@NoArgsConstructor
public class EventKpi {
    @Id
    @GeneratedValue( strategy= GenerationType.AUTO)
    private UUID id;
    @Column(name = "creator")
    private String userName;
    @Column(name = "eventId")
    private UUID eventId;
}
