package com.example.analytics.models;


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
@Table(name = "view_event")
public class ViewEventAction  {
    @Id
    @GeneratedValue( strategy= GenerationType.AUTO)
    private UUID id;
    @Column(name = "user_name")
    private String userName;
    @Column(name = "event_id")
    private UUID eventId;
    private LocalDateTime seenAt;
}