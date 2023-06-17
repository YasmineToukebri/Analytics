package com.example.Analytics.models;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
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
}