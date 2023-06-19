package com.example.analytics.models;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "quizz_action")
public class QuizzAction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Column(name = "user_name")
    private String userName;
    @Column(name = "event_id")
    private UUID eventId;
    @Column(name = "quiz_id")
    private String quizId;
    @Column(name = "action_at")
    private LocalDateTime passedAt;
}
