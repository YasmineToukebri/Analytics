package com.example.analytics.models;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Table(name = "AbortEvent")
@AllArgsConstructor
@NoArgsConstructor

@Entity
public class AbortEvent {

    @Id
    @GeneratedValue( strategy= GenerationType.AUTO)
    private UUID id;
    private String userName;
    private UUID eventId;

    private LocalDateTime abortedAt;

}
