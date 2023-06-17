package com.example.Analytics.models;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "views")
public class ViewEvent {
    @Id
    @GeneratedValue( strategy= GenerationType.AUTO)
    private UUID id;
    private String userName;
    private UUID eventId;
    private LocalDateTime seenAt;
}
