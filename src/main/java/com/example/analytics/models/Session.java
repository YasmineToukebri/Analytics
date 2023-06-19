package com.example.analytics.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;
@Entity
@Data
@Builder
@Table(name = "sessions")
@AllArgsConstructor
@NoArgsConstructor
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Column(name = "user_name")
    private String userName;
    @Column(name = "event_id")
    private UUID roomId;
    @Column(name = "action")
    private String action;
    @Column(name = "enter_action_at")
    private LocalDateTime enterActionAt;
    @Column(name = "leave_action_at")
    private LocalDateTime leaveActionAt;
    @Column(name = "duration")
    private Duration duration;

}

